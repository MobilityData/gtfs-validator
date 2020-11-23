package org.mobilitydata.gtfsvalidator.table;

import com.google.common.reflect.ClassPath;
import org.mobilitydata.gtfsvalidator.annotation.GtfsLoader;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.input.GtfsInput;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedFile;
import org.mobilitydata.gtfsvalidator.validator.FileValidator;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Loader for a whole GTFS feed with all its CSV files.
 * <p>
 * The loader creates a {@code GtfsFeedContainer} object. Loaders for particular tables are discovered dynamically
 * based on {@code GtfsLoader} annotation.
 */
public class GtfsFeedLoader {
    private HashMap<String, GtfsTableLoader> tableLoaders = new HashMap<>();
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
            } catch (ReflectiveOperationException exception) {
                System.err.println("Possible bug in GTFS annotation processor");
                exception.printStackTrace();
                continue;
            }
            tableLoaders.put(loader.gtfsFilename(), loader);
        }
    }

    private static Reader createFileReader(InputStream stream) throws FileNotFoundException {
        return new BufferedReader(new InputStreamReader(stream));
    }

    public String listTableLoaders() {
        return String.join(" ", tableLoaders.keySet());
    }

    public void setNumThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public GtfsFeedContainer load(GtfsInput gtfsInput, GtfsFeedName feedName, ValidatorLoader validatorLoader,
                                  NoticeContainer noticeContainer) throws IOException, InterruptedException {
        System.out.println("Loading in " + numThreads + " threads");
        ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        List<Callable<GtfsTableContainer>> loaderCallables = new ArrayList<>();
        Map<String, GtfsTableLoader> remainingLoaders = (Map<String, GtfsTableLoader>) tableLoaders.clone();
        for (String filename : gtfsInput.getFilenames()) {
            GtfsTableLoader loader = remainingLoaders.remove(filename.toLowerCase());
            if (loader == null) {
                noticeContainer.addNotice(new UnexpectedFile(filename));
            } else {
                loaderCallables.add(() -> loader.load(
                        createFileReader(gtfsInput.getFile(filename)), feedName, validatorLoader, noticeContainer));
            }
        }
        ArrayList<GtfsTableContainer> tableContainers = new ArrayList<>();
        for (GtfsTableLoader loader : remainingLoaders.values()) {
            tableContainers.add(loader.loadMissingFile(validatorLoader, noticeContainer));
        }
        try {
            try {
                List<Future<GtfsTableContainer>> results = exec.invokeAll(loaderCallables);
                tableContainers.ensureCapacity(results.size());
                results.forEach(f -> {
                    try {
                        tableContainers.add(f.get());
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            GtfsFeedContainer feed = new GtfsFeedContainer(tableContainers);
            List<Callable<Void>> validatorCallables = new ArrayList<>();
            for (FileValidator validator : validatorLoader.createMultiFileValidators(feed)) {
                validatorCallables.add(() -> {
                    validator.validate(noticeContainer);
                    return null;
                });
            }
            try {
                exec.invokeAll(validatorCallables);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return feed;
        } finally {
            exec.shutdown();
        }
    }
}
