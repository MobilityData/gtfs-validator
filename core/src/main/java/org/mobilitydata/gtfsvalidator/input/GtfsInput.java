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
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
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
     * Creates an specific GtfsInput to read data from the given URL.
     *
     * @param sourceUrl
     * @param targetPathAsString
     * @return
     * @throws IOException
     */
    static GtfsInput createFromUrl(URL sourceUrl, String targetPathAsString) throws IOException, URISyntaxException, InterruptedException {

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            HttpGet httpGet = new HttpGet(sourceUrl.toURI());
            HttpResponse httpResponse = httpClient.execute(httpGet);
            InputStream inputStream = httpResponse.getEntity().getContent();
            Path path = createPath(targetPathAsString);
            Files.copy(
                    inputStream,
                    path,
                    StandardCopyOption.REPLACE_EXISTING
            );
            return createFromPath(path.toString());
        } catch (IOException | URISyntaxException | InterruptedException e) {
            throw e;
        }
    }

    /**
     * Creates a path from a given string
     * @param toCleanOrCreate
     * @return
     */
    static Path createPath(final String toCleanOrCreate) throws IOException, InterruptedException {
        // to empty any already existing directory
        Path pathToCleanOrCreate = Paths.get(toCleanOrCreate);
        if (Files.exists(pathToCleanOrCreate)) {
            try {
                FileUtils.deleteQuietly(new File(toCleanOrCreate));
                Files.createDirectory(pathToCleanOrCreate);
            } catch (IOException e) {
                throw e;
            }
            return pathToCleanOrCreate;
        }

        // Create the directory
        try {
            Files.createFile(pathToCleanOrCreate);
        } catch (AccessDeniedException e) {
            // Wait and try again - Windows can initially block creating a directory immediately after a delete when a file lock exists (#112)
            try {
                Thread.sleep(500);
                Files.createFile(pathToCleanOrCreate);
            } catch (IOException | InterruptedException ex) {
                throw ex;
            }
        } catch (IOException e) {
            throw e;
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
