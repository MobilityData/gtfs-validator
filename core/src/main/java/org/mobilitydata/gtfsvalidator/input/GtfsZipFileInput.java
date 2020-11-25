package org.mobilitydata.gtfsvalidator.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implements support for GTFS ZIP archives.
 */
public class GtfsZipFileInput implements GtfsInput {
    private final Set<String> filenames = new HashSet();
    private final ZipFile zipFile;

    public GtfsZipFileInput(File file) throws IOException {
        zipFile = new ZipFile(file);
        for (Enumeration<? extends ZipEntry> i = zipFile.entries(); i
                .hasMoreElements(); ) {
            ZipEntry entry = i.nextElement();
            if (!insideZipDirectory(entry)) {
                filenames.add(entry.getName());
            }
        }
    }

    private boolean insideZipDirectory(ZipEntry entry) {
        // The .zip file specification states:
        // All slashes MUST be forward slashes '/' as opposed to backwards slashes '\' for compatibility with Amiga and
        // UNIX file systems etc.
        //
        // Directory names in end with '/'.
        return entry.getName().contains("/");
    }

    @Override
    public Set<String> getFilenames() {
        return filenames;
    }

    @Override
    public InputStream getFile(String filename) throws IOException {
        ZipEntry entry = zipFile.getEntry(filename);
        if (entry == null) {
            throw new FileNotFoundException(
                    Paths.get(zipFile.getName(), filename).toString());
        }
        return zipFile.getInputStream(entry);
    }
}
