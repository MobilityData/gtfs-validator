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
import java.lang.reflect.Modifier;
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
  private final HashMap<String, GtfsFileDescriptor<?>> tableDescriptors = new HashMap<>();
  private int numThreads = 1;

  /**
   * The set of validators that were skipped during validation because their file dependencies had
   * parse errors plus validators that are optional.
   */
  private final List<Class<? extends FileValidator>> multiFileValidatorsWithParsingErrors =
      new ArrayList<>();

  public GtfsFeedLoader(
      ImmutableList<Class<? extends GtfsFileDescriptor<?>>> tableDescriptorClasses) {
    for (Class<? extends GtfsFileDescriptor<?>> clazz : tableDescriptorClasses) {
      GtfsFileDescriptor<?> descriptor;
      try {
        //        Skipping abstract classes. Example: GtfsTableDescriptor.
        if (Modifier.isAbstract(clazz.getModifiers())) {
          continue;
        }
        descriptor = clazz.asSubclass(GtfsFileDescriptor.class).getConstructor().newInstance();
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

  public Collection<GtfsFileDescriptor<?>> getTableDescriptors() {
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
  public GtfsFeedContainer loadAndValidate(
      GtfsInput gtfsInput, ValidatorProvider validatorProvider, NoticeContainer noticeContainer)
      throws InterruptedException {
    logger.atInfo().log("Loading in %d threads", numThreads);
    ExecutorService exec = Executors.newFixedThreadPool(numThreads);

    List<Callable<TableAndNoticeContainers>> loaderCallables = new ArrayList<>();
    Map<String, GtfsTableDescriptor<?>> remainingDescriptors =
        (Map<String, GtfsTableDescriptor<?>>) tableDescriptors.clone();
    for (String filename : gtfsInput.getFilenames()) {
      GtfsFileDescriptor<?> tableDescriptor = remainingDescriptors.remove(filename.toLowerCase());
      if (tableDescriptor == null) {
        noticeContainer.addValidationNotice(new UnknownFileNotice(filename));
      } else {
        loaderCallables.add(
            () -> {
              NoticeContainer loaderNotices = new NoticeContainer();
              GtfsEntityContainer<?, ?> tableContainer;
              try (InputStream inputStream = gtfsInput.getFile(filename)) {
                try {
                  if (tableDescriptor instanceof GtfsTableDescriptor) {
                    tableContainer =
                        AnyTableLoader.load(
                            (GtfsTableDescriptor) tableDescriptor,
                            validatorProvider,
                            inputStream,
                            loaderNotices);
                  } else if (tableDescriptor instanceof GtfsGeojsonFileDescriptor) {
                    tableContainer =
                        JsonFileLoader.load(
                            (GtfsGeojsonFileDescriptor) tableDescriptor,
                            validatorProvider,
                            inputStream,
                            loaderNotices);
                  } else {
                    logger.atSevere().log(
                        "Runtime exception table descriptor not supported: %s",
                        tableDescriptor.getClass().getName());
                    throw new RuntimeException("Table descriptor is not a supported type");
                  }
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
    ArrayList<GtfsEntityContainer<?, ?>> tableContainers = new ArrayList<>();
    tableContainers.ensureCapacity(tableDescriptors.size());
    for (GtfsFileDescriptor<?> tableDescriptor : remainingDescriptors.values()) {
      tableContainers.add(
          AnyTableLoader.loadMissingFile(tableDescriptor, validatorProvider, noticeContainer));
    }
    try {
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
      GtfsFeedContainer feed = new GtfsFeedContainer(tableContainers);
      List<Callable<NoticeContainer>> validatorCallables = new ArrayList<>();
      // Validators with parser-error dependencies will not be returned here, but instead added to
      // the skippedValidators list.
      for (FileValidator validator :
          validatorProvider.createMultiFileValidators(
              feed, multiFileValidatorsWithParsingErrors::add)) {
        validatorCallables.add(
            () -> {
              NoticeContainer validatorNotices = new NoticeContainer();
              ValidatorUtil.safeValidate(
                  validator::validate, validator.getClass(), validatorNotices);
              return validatorNotices;
            });
      }
      for (Future<NoticeContainer> futureContainer : exec.invokeAll(validatorCallables)) {
        try {
          noticeContainer.addAll(futureContainer.get());
        } catch (ExecutionException e) {
          // All runtime exceptions should be caught above.
          // ExecutionException is not expected to happen.
          addThreadExecutionError(e, noticeContainer);
        }
      }
      return feed;
    } finally {
      exec.shutdown();
    }
  }

  /** Adds a ThreadExecutionError to the notice container. */
  private static void addThreadExecutionError(
      ExecutionException e, NoticeContainer noticeContainer) {
    logger.atSevere().withCause(e).log("Execution exception");
    noticeContainer.addSystemError(new ThreadExecutionError(e));
  }

  static class TableAndNoticeContainers {
    final GtfsEntityContainer tableContainer;
    final NoticeContainer noticeContainer;

    public TableAndNoticeContainers(
        GtfsEntityContainer tableContainer, NoticeContainer noticeContainer) {
      this.tableContainer = tableContainer;
      this.noticeContainer = noticeContainer;
    }
  }
}
