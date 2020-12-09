/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import org.apache.commons.io.FileUtils;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * GtfsInput provides a common interface for reading GTFS data, either from a ZIP archive or from a directory.
 */
public interface GtfsInput {
    /**
     * Creates an specific GtfsInput to read data from the given path.
     *
     * @param path  the path to the resource
     * @return the @code{GtfsInput} created after processing the GTFS archive
     * @throws IOException if no file could not be found at the specified location
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
     * Creates an specific GtfsInput to read data from the given URL.
     *
     * @param sourceUrl           the fully qualified URL to download of the resource to download
     * @param targetPathAsString  the path to where the downloaded resource will be stored
     * @return the @code{GtfsInput} created after download of the GTFS archive
     * @throws IOException  if no file could not be found at the specified location
     * @throws URISyntaxException  if URL is malformed
     * @throws InterruptedException when a thread is waiting, sleeping, or otherwise occupied, and the thread is
     * interrupted, either before or during the activity
     */
    static GtfsInput createFromUrl(URL sourceUrl, String targetPathAsString)
            throws IOException, URISyntaxException, InterruptedException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(sourceUrl.toURI());
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        Path targetPath = createPath(targetPathAsString);
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(targetPath.toString()));
        httpResponse.getEntity().writeTo(outputStream);
        outputStream.close();
        httpResponse.close();
        httpClient.close();
        return createFromPath(targetPath.toString());
    }

    /**
     * Creates a path from a given string or cleans it if the path already exists
     *
     * @param toCleanOrCreate  the path to clean or create as string
     * @return the created @code{Path}
     */
    static Path createPath(final String toCleanOrCreate) throws IOException, InterruptedException {
        // to empty any already existing directory
        Path pathToCleanOrCreate = Paths.get(toCleanOrCreate);
        if (Files.exists(pathToCleanOrCreate)) {
            FileUtils.deleteQuietly(new File(toCleanOrCreate));
            Files.createFile(pathToCleanOrCreate);
            return pathToCleanOrCreate;
        }

        // Create the directory
        try {
            Files.createFile(pathToCleanOrCreate);
        } catch (AccessDeniedException e) {
            // Wait and try again - Windows can initially block creating a directory immediately after a delete when a
            // file lock exists (#112)
            Thread.sleep(500);
            Files.createFile(pathToCleanOrCreate);
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
     * @throws IOException if no file could not be found at the specified location
     */
    InputStream getFile(String filename) throws IOException;
}
