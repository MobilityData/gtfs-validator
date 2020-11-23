package org.mobilitydata.gtfsvalidator.input;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements support for unarchived GTFS directories.
 */
public class GtfsUnarchivedInput implements GtfsInput {
    private final Set<String> filenames = new HashSet();
    private final File directory;

    public GtfsUnarchivedInput(File directory) throws IOException {
        this.directory = directory;
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                filenames.add(file.getName());
            }
        }
    }

    @Override
    public Set<String> getFilenames() {
        return filenames;
    }

    @Override
    public InputStream getFile(String filename) throws IOException {
        return new FileInputStream(new File(directory, filename));
    }
}
