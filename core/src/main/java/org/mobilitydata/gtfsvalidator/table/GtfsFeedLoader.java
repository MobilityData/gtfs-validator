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

import com.google.common.reflect.ClassPath;
import org.mobilitydata.gtfsvalidator.annotation.GtfsLoader;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnknownFileNotice;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Loader for a whole GTFS feed with all its CSV files.
 * <p>
 * The loader creates a {@code GtfsFeedContainer} object. Loaders for particular tables are discovered dynamically
 * based on {@code GtfsLoader} annotation.
 */
public class GtfsFeedLoader {
    private final HashMap<String, GtfsTableLoader> tableLoaders = new HashMap<>();
    private int numThreads = 1;

    public GtfsFeedLoader() {
        ClassPath classPath;
        try {
            classPath = ClassPath.from(ClassLoader.getSystemClassLoader());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        for (ClassPath.ClassInfo classInfo : classPath
                .getTopLevelClassesRecursive("org.mobilitydata.gtfsvalidator.table")) {
            Class<?> clazz = classInfo.load();
            if (!(clazz.isAnnotationPresent(GtfsLoader.class) && GtfsTableLoader.class.isAssignableFrom(clazz))) {
                continue;
            }
            GtfsTableLoader loader;
            try {
                loader = clazz.asSubclass(GtfsTableLoader.class).getConstructor().newInstance();
            } catch (ReflectiveOperationException e) {
                System.err.println("Possible bug in GTFS annotation processor - " +
                        "expected a constructor without parameters for " + clazz.getName());
                e.printStackTrace();
                continue;
            }
            tableLoaders.put(loader.gtfsFilename(), loader);
        }
    }

    private static Reader createFileReader(InputStream stream) {
        return new BufferedReader(new InputStreamReader(stream));
    }

    public String listTableLoaders() {
        return String.join(" ", tableLoaders.keySet());
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public GtfsFeedContainer loadAndValidate(GtfsInput gtfsInput, GtfsFeedName feedName, ValidatorLoader validatorLoader,
                                             NoticeContainer noticeContainer) {
        System.out.println("Loading in " + numThreads + " threads");
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);

        List<Callable<TableAndNoticeContainers>> loaderCallables = new ArrayList<>();
        Map<String, GtfsTableLoader> remainingLoaders = (Map<String, GtfsTableLoader>) tableLoaders.clone();
        for (String filename : gtfsInput.getFilenames()) {
            GtfsTableLoader loader = remainingLoaders.remove(filename.toLowerCase());
            if (loader == null) {
                noticeContainer.addNotice(new UnknownFileNotice(filename));
            } else {
                loaderCallables.add(() -> {
                    Reader reader = createFileReader(gtfsInput.getFile(filename));
                    NoticeContainer loaderNotices = new NoticeContainer();
                    GtfsTableContainer tableContainer;
                    try {
                        tableContainer = loader.load(reader, feedName, validatorLoader, loaderNotices);
                    } finally {
                        reader.close();
                    }
                    return new TableAndNoticeContainers(tableContainer, loaderNotices);
                });
            }
        }
        ArrayList<GtfsTableContainer> tableContainers = new ArrayList<>();
        tableContainers.ensureCapacity(tableLoaders.size());
        for (GtfsTableLoader loader : remainingLoaders.values()) {
            tableContainers.add(loader.loadMissingFile(validatorLoader, noticeContainer));
        }
        try {
            try {
                exec.invokeAll(loaderCallables).forEach(f -> {
                    try {
                        TableAndNoticeContainers containers = f.get();
                        tableContainers.add(containers.tableContainer);
                        noticeContainer.addAll(containers.noticeContainer);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GtfsFeedContainer feed = new GtfsFeedContainer(tableContainers);
            List<Callable<NoticeContainer>> validatorCallables = new ArrayList<>();
            for (FileValidator validator : validatorLoader.createMultiFileValidators(feed)) {
                validatorCallables.add(() -> {
                    NoticeContainer validatorNotices = new NoticeContainer();
                    validator.validate(validatorNotices);
                    return validatorNotices;
                });
            }
            try {
                exec.invokeAll(validatorCallables).forEach(f -> {
                    try {
                        noticeContainer.addAll(f.get());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return feed;
        } finally {
            exec.shutdown();
        }
    }

    static class TableAndNoticeContainers {
        final GtfsTableContainer tableContainer;
        final NoticeContainer noticeContainer;

        public TableAndNoticeContainers(GtfsTableContainer tableContainer, NoticeContainer noticeContainer) {
            this.tableContainer = tableContainer;
            this.noticeContainer = noticeContainer;
        }
    }
}
