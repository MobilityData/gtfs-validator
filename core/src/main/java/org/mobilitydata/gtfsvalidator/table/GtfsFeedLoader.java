/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.table;

import com.google.common.collect.ImmutableList;
import com.google.common.flogger.FluentLogger;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInLoaderError;
import org.mobilitydata.gtfsvalidator.notice.ThreadExecutionError;
import org.mobilitydata.gtfsvalidator.notice.UnknownFileNotice;
import org.mobilitydata.gtfsvalidator.performance.MemoryMonitor;
import org.mobilitydata.gtfsvalidator.performance.MemoryUsageRegister;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidatorUtil;

/**
 * Loader for a whole GTFS feed with all its CSV files.
 *
 * <p>The loader creates a {@link GtfsFeedContainer} object.
 */
public class GtfsFeedLoader {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private final HashMap<String, GtfsTableDescriptor<?>> tableDescriptors = new HashMap<>();
  private int numThreads = 1;

  /**
   * The set of validators that were skipped during validation because their file dependencies had
   * parse errors plus validators that are optional.
   */
  private final List<Class<? extends FileValidator>> multiFileValidatorsWithParsingErrors =
      new ArrayList<>();

  public GtfsFeedLoader(
      ImmutableList<Class<? extends GtfsTableDescriptor<?>>> tableDescriptorClasses) {
    for (Class<? extends GtfsTableDescriptor<?>> clazz : tableDescriptorClasses) {
      GtfsTableDescriptor<?> descriptor;
      try {
        descriptor = clazz.asSubclass(GtfsTableDescriptor.class).getConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Possible bug in GTFS annotation processor: expected a constructor without parameters"
                + " for %s",
            clazz.getName());
        continue;
      }
      tableDescriptors.put(descriptor.gtfsFilename(), descriptor);
    }
  }

  public Collection<GtfsTableDescriptor<?>> getTableDescriptors() {
    return Collections.unmodifiableCollection(tableDescriptors.values());
  }

  public String listTableDescriptors() {
    return String.join(" ", tableDescriptors.keySet());
  }

  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  public List<Class<? extends FileValidator>> getMultiFileValidatorsWithParsingErrors() {
    return Collections.unmodifiableList(multiFileValidatorsWithParsingErrors);
  }

  @SuppressWarnings("unchecked")
  @MemoryMonitor()
  public GtfsFeedContainer loadAndValidate(
      GtfsInput gtfsInput, ValidatorProvider validatorProvider, NoticeContainer noticeContainer)
      throws InterruptedException {
    logger.atInfo().log("Loading in %d threads", numThreads);
    ExecutorService exec = Executors.newFixedThreadPool(numThreads);

    List<Callable<TableAndNoticeContainers>> loaderCallables = new ArrayList<>();
    Map<String, GtfsTableDescriptor<?>> remainingDescriptors =
        (Map<String, GtfsTableDescriptor<?>>) tableDescriptors.clone();
    for (String filename : gtfsInput.getFilenames()) {
      GtfsTableDescriptor<?> tableDescriptor = remainingDescriptors.remove(filename.toLowerCase());
      if (tableDescriptor == null) {
        noticeContainer.addValidationNotice(new UnknownFileNotice(filename));
      } else {
        loaderCallables.add(
            () -> {
              NoticeContainer loaderNotices = new NoticeContainer();
              GtfsTableContainer<?> tableContainer;
              try (InputStream inputStream = gtfsInput.getFile(filename)) {
                try {
                  tableContainer =
                      AnyTableLoader.load(
                          tableDescriptor, validatorProvider, inputStream, loaderNotices);
                } catch (RuntimeException e) {
                  // This handler should prevent ExecutionException for
                  // this thread. We catch an exception here for storing
                  // the context since we know the filename here.
                  logger.atSevere().withCause(e).log("Runtime exception when loading %s", filename);
                  loaderNotices.addSystemError(new RuntimeExceptionInLoaderError(filename, e));
                  // Since the file was not loaded successfully, we treat
                  // it as missing for continuing validation.
                  tableContainer =
                      AnyTableLoader.loadMissingFile(
                          tableDescriptor, validatorProvider, loaderNotices);
                }
              }
              return new TableAndNoticeContainers(tableContainer, loaderNotices);
            });
      }
    }
    ArrayList<GtfsTableContainer<?>> tableContainers = new ArrayList<>();
    tableContainers.ensureCapacity(tableDescriptors.size());
    for (GtfsTableDescriptor<?> tableDescriptor : remainingDescriptors.values()) {
      tableContainers.add(
          AnyTableLoader.loadMissingFile(tableDescriptor, validatorProvider, noticeContainer));
    }
    try {
      var beforeLoading =
          MemoryUsageRegister.getInstance()
              .getMemoryUsageSnapshot("GtfsFeedLoader.loadTables", null);
      loadTables(noticeContainer, exec, loaderCallables, tableContainers);
      MemoryUsageRegister.getInstance()
          .registerMemoryUsage("GtfsFeedLoader.loadTables", beforeLoading);

      GtfsFeedContainer feed = new GtfsFeedContainer(tableContainers);
      var beforeMultiFileValidators =
          MemoryUsageRegister.getInstance()
              .getMemoryUsageSnapshot("GtfsFeedLoader.executeMultiFileValidators", null);
      executeMultiFileValidators(validatorProvider, noticeContainer, feed, exec);
      MemoryUsageRegister.getInstance()
          .registerMemoryUsage(
              "GtfsFeedLoader.executeMultiFileValidators", beforeMultiFileValidators);

      return feed;
    } finally {
      exec.shutdown();
    }
  }

  private static void loadTables(
      NoticeContainer noticeContainer,
      ExecutorService exec,
      List<Callable<TableAndNoticeContainers>> loaderCallables,
      ArrayList<GtfsTableContainer<?>> tableContainers)
      throws InterruptedException {
    for (Future<TableAndNoticeContainers> futureContainer : exec.invokeAll(loaderCallables)) {
      try {
        TableAndNoticeContainers containers = futureContainer.get();
        tableContainers.add(containers.tableContainer);
        noticeContainer.addAll(containers.noticeContainer);
      } catch (ExecutionException e) {
        // All runtime exceptions should be caught above.
        // ExecutionException is not expected to happen.
        addThreadExecutionError(e, noticeContainer);
      }
    }
  }

  private void executeMultiFileValidators(
      ValidatorProvider validatorProvider,
      NoticeContainer noticeContainer,
      GtfsFeedContainer feed,
      ExecutorService exec)
      throws InterruptedException {
    List<Callable<NoticeContainer>> validatorCallables = new ArrayList<>();
    // Validators with parser-error dependencies will not be returned here, but instead added to
    // the skippedValidators list.
    for (FileValidator validator :
        validatorProvider.createMultiFileValidators(
            feed, multiFileValidatorsWithParsingErrors::add)) {
      validatorCallables.add(
          () -> {
            NoticeContainer validatorNotices = new NoticeContainer();
            ValidatorUtil.safeValidate(validator::validate, validator.getClass(), validatorNotices);
            return validatorNotices;
          });
    }
    collectMultiFileValidationNotices(noticeContainer, exec, validatorCallables);
  }

  private static void collectMultiFileValidationNotices(
      NoticeContainer noticeContainer,
      ExecutorService exec,
      List<Callable<NoticeContainer>> validatorCallables)
      throws InterruptedException {
    for (Future<NoticeContainer> futureContainer : exec.invokeAll(validatorCallables)) {
      try {
        noticeContainer.addAll(futureContainer.get());
      } catch (ExecutionException e) {
        // All runtime exceptions should be caught above.
        // ExecutionException is not expected to happen.
        addThreadExecutionError(e, noticeContainer);
      }
    }
  }

  /** Adds a ThreadExecutionError to the notice container. */
  private static void addThreadExecutionError(
      ExecutionException e, NoticeContainer noticeContainer) {
    logger.atSevere().withCause(e).log("Execution exception");
    noticeContainer.addSystemError(new ThreadExecutionError(e));
  }

  static class TableAndNoticeContainers {
    final GtfsTableContainer tableContainer;
    final NoticeContainer noticeContainer;

    public TableAndNoticeContainers(
        GtfsTableContainer tableContainer, NoticeContainer noticeContainer) {
      this.tableContainer = tableContainer;
      this.noticeContainer = noticeContainer;
    }
  }
}
