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

package org.mobilitydata.gtfsvalidator.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Set;

import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;

/**
 * GtfsInput provides a common interface for reading GTFS data, either from a ZIP archive or from a directory.
 */
public interface GtfsInput {
    int HTTP_TEMP_REDIRECT = 307;
    int HTTP_PERM_REDIRECT = 308;
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

    static GtfsInput createFromUrl(URL sourceUrl, String targetPathAsString) throws IOException {
        InputStream inputStream;

        try {
            final HttpURLConnection httpConnection = (HttpURLConnection) sourceUrl.openConnection();
            final int responseCode = httpConnection.getResponseCode();
            // check response code
            if (responseCode == HTTP_MOVED_PERM || responseCode == HTTP_MOVED_TEMP ||
                    responseCode == HTTP_TEMP_REDIRECT || responseCode == HTTP_PERM_REDIRECT) {
                // use redirection instead of original url
                final String newUrlAsString = httpConnection.getHeaderField("Location");
                final URLConnection connection = new URL(newUrlAsString).openConnection();
                inputStream = connection.getInputStream();
            } else {
                inputStream = sourceUrl.openStream();
            }
            Path path = createPath(targetPathAsString);
            Files.copy(
                    inputStream,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return createFromPath(path.toString());
        } catch (IOException e) {
            throw e;
        }
    }

    static GtfsInput create(String path) throws IOException {
        return createFromPath(path);
    }

    static GtfsInput create(URL url, String targetPathAsString) throws IOException {
        return createFromUrl(url, targetPathAsString);
    }

    static Path createPath(final String toCleanOrCreate) {
        // to empty any already existing directory
        Path pathToCleanOrCreate = Paths.get(toCleanOrCreate);
        if (Files.exists(pathToCleanOrCreate)) {
                try {
                    //noinspection ResultOfMethodCallIgnored -- we ignore if deletion went well or not
                    Files.walk(pathToCleanOrCreate).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
                    Files.createDirectory(pathToCleanOrCreate);
                } catch (IOException e) {
                    e.printStackTrace();
            }
            return pathToCleanOrCreate;
        }

        // Create the directory
        try {
            Files.createDirectory(pathToCleanOrCreate);
        } catch (AccessDeniedException e) {
            // Wait and try again - Windows can initially block creating a directory immediately after a delete when a file lock exists (#112)
            try {
                Thread.sleep(500);
                Files.createDirectory(pathToCleanOrCreate);
            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pathToCleanOrCreate;
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
