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

import com.google.common.flogger.FluentLogger;
import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.mobilitydata.gtfsvalidator.annotation.GtfsLoader;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.ErrorDetectedException;
import org.mobilitydata.gtfsvalidator.notice.Notice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInLoaderError;
import org.mobilitydata.gtfsvalidator.notice.RuntimeExceptionInValidatorError;
import org.mobilitydata.gtfsvalidator.notice.ThreadExecutionError;
import org.mobilitydata.gtfsvalidator.notice.ThreadInterruptedError;
import org.mobilitydata.gtfsvalidator.notice.UnknownFileNotice;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/**
 * Loader for a whole GTFS feed with all its CSV files.
 *
 * <p>The loader creates a {@code GtfsFeedContainer} object. Loaders for particular tables are
 * discovered dynamically based on {@code GtfsLoader} annotation.
 */
public class GtfsFeedLoader {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();
  private final HashMap<String, GtfsTableLoader> tableLoaders = new HashMap<>();
  private int numThreads = 1;

  public GtfsFeedLoader() {
    ClassPath classPath;
    try {
      classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
    for (ClassPath.ClassInfo classInfo :
        classPath.getTopLevelClassesRecursive("org.mobilitydata.gtfsvalidator.table")) {
      Class<?> clazz = classInfo.load();
      if (!(clazz.isAnnotationPresent(GtfsLoader.class)
          && GtfsTableLoader.class.isAssignableFrom(clazz))) {
        continue;
      }
      GtfsTableLoader loader;
      try {
        loader = clazz.asSubclass(GtfsTableLoader.class).getConstructor().newInstance();
      } catch (ReflectiveOperationException e) {
        logger.atSevere().withCause(e).log(
            "Possible bug in GTFS annotation processor: expected a constructor without parameters"
                + " for %s",
            clazz.getName());
        continue;
      }
      tableLoaders.put(loader.gtfsFilename(), loader);
    }
  }

  public String listTableLoaders() {
    return String.join(" ", tableLoaders.keySet());
  }

  public void setNumThreads(int numThreads) {
    this.numThreads = numThreads;
  }

  public GtfsFeedContainer loadAndValidate(
      GtfsInput gtfsInput,
      ValidationContext validationContext,
      ValidatorLoader validatorLoader,
      NoticeContainer noticeContainer)
      throws ErrorDetectedException {
    logger.atInfo().log("Loading in %d threads", numThreads);
    ExecutorService exec = Executors.newFixedThreadPool(numThreads);

    List<Callable<TableAndNoticeContainers>> loaderCallables = new ArrayList<>();
    Map<String, GtfsTableLoader<?>> remainingLoaders =
        (Map<String, GtfsTableLoader<?>>) tableLoaders.clone();
    for (String filename : gtfsInput.getFilenames()) {
      GtfsTableLoader loader = remainingLoaders.remove(filename.toLowerCase());
      if (loader == null) {
        noticeContainer.addValidationNotice(new UnknownFileNotice(filename));
      } else {
        loaderCallables.add(
            () -> {
              InputStream inputStream = gtfsInput.getFile(filename);
              NoticeContainer loaderNotices = new NoticeContainer();
              GtfsTableContainer tableContainer;
              try {
                tableContainer =
                    loader.load(inputStream, validationContext, validatorLoader, loaderNotices);
              } catch (RuntimeException e) {
                // This handler should prevent ExecutionException for
                // this thread. We catch an exception here for storing
                // the context since we know the filename here.
                logger.atSevere().withCause(e).log("Runtime exception when loading %s", filename);
                loaderNotices.addSystemError(
                    new RuntimeExceptionInLoaderError(
                        filename, e.getClass().getCanonicalName(), e.getMessage()));
                // Since the file was not loaded successfully, we treat
                // it as missing for continuing validation.
                tableContainer =
                    loader.loadMissingFile(validationContext, validatorLoader, loaderNotices);
              } finally {
                inputStream.close();
              }
              return new TableAndNoticeContainers(tableContainer, loaderNotices);
            });
      }
    }
    ArrayList<GtfsTableContainer<?>> tableContainers = new ArrayList<>();
    tableContainers.ensureCapacity(tableLoaders.size());
    for (GtfsTableLoader loader : remainingLoaders.values()) {
      tableContainers.add(
          loader.loadMissingFile(validationContext, validatorLoader, noticeContainer));
    }
    try {
      try {
        for (Future<TableAndNoticeContainers> f : exec.invokeAll(loaderCallables)) {
          try {
            TableAndNoticeContainers containers = f.get();
            tableContainers.add(containers.tableContainer);
            noticeContainer.addAll(containers.noticeContainer);
//          } catch (ErrorDetectedException e) {
//            noticeContainer.addValidationNotice(Notice.fromMessage(e.getCause().getMessage()));
          } catch (ExecutionException e) {
            // All runtime exceptions should be caught above.
            // ExecutionException is not expected to happen.
            logger.atSevere().withCause(e).log("Execution exception in loader");
            final Throwable cause = e.getCause();
            noticeContainer.addSystemError(
                new ThreadExecutionError(cause.getClass().getCanonicalName(), cause.getMessage()));
          } catch (InterruptedException e) {
            logger.atSevere().withCause(e).log("Interrupted during loading a GTFS tables");
            noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
          }
        }
      } catch (InterruptedException e) {
        logger.atSevere().withCause(e).log("Interrupted during loading GTFS tables");
        noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
      }
      GtfsFeedContainer feed = new GtfsFeedContainer(tableContainers);
      if (!feed.isParsedSuccessfully()) {
        // No need to call file validators if any file failed to parse. File validations in that
        // case may lead to confusing error messages.
        //
        // Consider we failed to parse a row trip.txt but there is another row in stop_times.txt
        // that references a trip. Then foreign key validator may notify about a missing trip_id
        // which would be wrong.
        return feed;
      }
      List<Callable<NoticeContainer>> validatorCallables = new ArrayList<>();
      for (FileValidator validator :
          validatorLoader.createMultiFileValidators(feed, validationContext)) {
        validatorCallables.add(
            () -> {
              NoticeContainer validatorNotices = new NoticeContainer();
              try {
                validator.validate(validatorNotices);
              } catch (RuntimeException e) {
                // This handler should prevent ExecutionException for
                // this thread. We catch an exception here for storing
                // the context since we know validator class name here.
                logger.atSevere().withCause(e).log(
                    "Runtime exception in validator %s", validator.getClass().getCanonicalName());
                validatorNotices.addSystemError(
                    new RuntimeExceptionInValidatorError(
                        validator.getClass().getCanonicalName(),
                        e.getClass().getCanonicalName(),
                        e.getMessage()));
              }
              return validatorNotices;
            });
      }
      try {
        for (Future<NoticeContainer> container : exec.invokeAll(validatorCallables)) {
          try {
            noticeContainer.addAll(container.get());
          } catch (ExecutionException e) {
            // All runtime exceptions should be caught above.
            // ExecutionException is not expected to happen.
            logger.atSevere().withCause(e).log("Execution exception in validator");
            final Throwable cause = e.getCause();
            noticeContainer.addSystemError(
                new ThreadExecutionError(cause.getClass().getCanonicalName(), cause.getMessage()));
          } catch (InterruptedException e) {
            logger.atSevere().withCause(e).log("Interrupted during validation of GTFS tables");
            noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
          }
        }
      } catch (InterruptedException e) {
        logger.atSevere().withCause(e).log("Interrupted during validation of GTFS tables");
        noticeContainer.addSystemError(new ThreadInterruptedError(e.getMessage()));
      }
      return feed;
    } finally {
      exec.shutdown();
    }
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
