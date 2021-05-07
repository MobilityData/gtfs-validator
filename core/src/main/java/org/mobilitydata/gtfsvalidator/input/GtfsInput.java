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

import com.google.common.collect.ImmutableSet;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * GtfsInput provides a common interface for reading GTFS data, either from a ZIP archive or from a
 * directory.
 */
public abstract class GtfsInput implements Closeable {
  /**
   * Creates a specific GtfsInput to read data from the given path.
   *
   * @param path the path to the resource
   * @return the {@code GtfsInput} created after processing the GTFS archive
   * @throws IOException any IO exception that occurred during loading
   */
  public static GtfsInput createFromPath(Path path) throws IOException {
    if (!Files.exists(path)) {
      throw new FileNotFoundException(path.toString());
    }
    if (Files.isDirectory(path)) {
      return new GtfsUnarchivedInput(path);
    }
    if (path.getFileSystem().equals(FileSystems.getDefault())) {
      // Read from a local ZIP file.
      return new GtfsZipFileInput(new ZipFile(path.toFile()));
    }
    // Load a remote ZIP file to memory.
    return new GtfsZipFileInput(
        new ZipFile(new SeekableInMemoryByteChannel(Files.readAllBytes(path))));
  }

  /**
   * Creates a specific GtfsInput to read a GTFS ZIP archive from the given URL.
   *
   * <p>Necessary parent directories will be created.
   *
   * @param sourceUrl the fully qualified URL to download of the resource to download
   * @param targetPath the path to store the downloaded GTFS archive
   * @return the {@code GtfsInput} created after download of the GTFS archive
   * @throws IOException if GTFS archive cannot be stored at the specified location
   * @throws URISyntaxException if URL is malformed
   */
  public static GtfsInput createFromUrl(URL sourceUrl, Path targetPath)
      throws IOException, URISyntaxException {
    // getParent() may return null if there is no parent, so call toAbsolutePath() first.
    Path targetDirectory = targetPath.toAbsolutePath().getParent();
    if (!Files.isDirectory(targetDirectory)) {
      Files.createDirectories(targetDirectory);
    }
    try (OutputStream outputStream = Files.newOutputStream(targetPath)) {
      loadFromUrl(sourceUrl, outputStream);
    }
    return createFromPath(targetPath);
  }

  /**
   * Creates a specific GtfsInput to read data from the given URL. The loaded ZIP file is kept in
   * memory.
   *
   * @param sourceUrl the fully qualified URL to download of the resource to download
   * @return the {@code GtfsInput} created after download of the GTFS archive
   * @throws IOException if no file could not be found at the specified location
   * @throws URISyntaxException if URL is malformed
   */
  public static GtfsInput createFromUrlInMemory(URL sourceUrl)
      throws IOException, URISyntaxException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      loadFromUrl(sourceUrl, outputStream);
      return new GtfsZipFileInput(
          new ZipFile(new SeekableInMemoryByteChannel(outputStream.toByteArray())));
    }
  }

  /**
   * Downloads data from network.
   *
   * @param sourceUrl the fully qualified URL
   * @param outputStream the output stream
   * @throws IOException if no file could not be found at the specified location
   * @throws URISyntaxException if URL is malformed
   */
  private static void loadFromUrl(URL sourceUrl, OutputStream outputStream)
      throws IOException, URISyntaxException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet httpGet = new HttpGet(sourceUrl.toURI());
      try (CloseableHttpResponse httpResponse = httpClient.execute(httpGet)) {
        httpResponse.getEntity().writeTo(outputStream);
      }
    }
  }

  /**
   * Lists all files inside the GTFS dataset, even if they are not CSV and do not have .txt
   * extension.
   *
   * <p>Directories and files in nested directories are skipped.
   *
   * @return base names of all available files
   */
  public abstract ImmutableSet<String> getFilenames();

  /**
   * Returns a stream to read data from a given file.
   *
   * @param filename relative path to the file, e.g, "stops.txt"
   * @return an stream to read the file data
   * @throws IOException if no file could not be found at the specified location
   */
  public abstract InputStream getFile(String filename) throws IOException;
}
