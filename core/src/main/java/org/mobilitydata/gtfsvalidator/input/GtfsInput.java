package org.mobilitydata.gtfsvalidator.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

/**
 * GtfsInput provides a common interface for reading GTFS data, either from a ZIP archive or from a directory.
 */
public interface GtfsInput {
    /**
     * Creates an specific GtfsInput to read data from the given path.
     *
     * @param path
     * @return
     * @throws IOException
     */
    static GtfsInput createFromPath(String path) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException(path);
        }
        if (file.isDirectory()) {
            return new GtfsUnarchivedInput(file);
        }
        return new GtfsZipFileInput(file);
    }

    /**
     * Lists all files inside the GTFS dataset, even if they are not CSV and do not have .txt extension.
     * <p>
     * Directories and files in nested directories are skipped.
     *
     * @return base names of all available files
     */
    Set<String> getFilenames();

    /**
     * Returns a stream to read data from a given file.
     *
     * @param filename relative path to the file, e.g, "stops.txt"
     * @return an stream to read the file data
     * @throws IOException
     */
    InputStream getFile(String filename) throws IOException;
}
