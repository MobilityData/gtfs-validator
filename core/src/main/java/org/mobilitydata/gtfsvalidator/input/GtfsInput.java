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
import com.google.common.flogger.FluentLogger;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mobilitydata.gtfsvalidator.notice.InvalidInputFilesInSubfolderNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

/**
 * GtfsInput provides a common interface for reading GTFS data, either from a ZIP archive or from a
 * directory.
 */
public abstract class GtfsInput implements Closeable {
  public static final String invalidInputMessage =
      "At least 1 GTFS file is in a subfolder. All GTFS files must reside at the root level directly.";

  /**
   * Creates a specific GtfsInput to read data from the given path.
   *
   * @param path the path to the resource
   * @param noticeContainer
   * @return the {@code GtfsInput} created after processing the GTFS archive
   * @throws IOException any IO exception that occurred during loading
   */
  public static GtfsInput createFromPath(Path path, NoticeContainer noticeContainer)
      throws IOException {
    ZipFile zipFile;
    if (!Files.exists(path)) {
      throw new FileNotFoundException(path.toString());
    }
    if (Files.isDirectory(path)) {
      return new GtfsUnarchivedInput(path);
    }
    String fileName = path.getFileName().toString().replace(".zip", "");
    if (path.getFileSystem().equals(FileSystems.getDefault())) {
      // Read from a local ZIP file.
      zipFile = new ZipFile(path.toFile());
      if (hasSubfolderWithGtfsFile(path)) {
        noticeContainer.addValidationNotice(
            new InvalidInputFilesInSubfolderNotice(invalidInputMessage));
      }

      return new GtfsZipFileInput(zipFile, fileName);
    }
    // Load a remote ZIP file to memory.
    zipFile = new ZipFile(new SeekableInMemoryByteChannel(Files.readAllBytes(path)));
    if (hasSubfolderWithGtfsFile(path)) {
      noticeContainer.addValidationNotice(
          new InvalidInputFilesInSubfolderNotice(invalidInputMessage));
    }
    return new GtfsZipFileInput(zipFile, fileName);
  }

  /**
   * Check if a zip file contains a subfolder with GTFS files
   *
   * @param path
   * @return
   * @throws IOException
   */
  public static boolean hasSubfolderWithGtfsFile(Path path) throws IOException {
    String fileName = path.getFileName().toString().replace(".zip", "");
    ZipInputStream zipInputStream =
        new ZipInputStream(new BufferedInputStream(new FileInputStream(path.toFile())));
    return containsGtfsFileInSubfolder(zipInputStream);
  }

  /**
   * Check if an input zip file from an URL contains a subfolder with GTFS files
   *
   * @param url
   * @return
   * @throws IOException
   */
  public static boolean hasSubfolderWithGtfsFile(URL url) throws IOException {
    ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(url.openStream()));
    return containsGtfsFileInSubfolder(zipInputStream);
  }

  /**
   * Common method used by two overloaded hasSubfolderWithGtfsFile methods
   *
   * @param zipInputStream
   * @return
   * @throws IOException
   */
  private static boolean containsGtfsFileInSubfolder(ZipInputStream zipInputStream)
      throws IOException {
    boolean containsSubfolder = false;
    String subfolder = null;
    ZipEntry entry;
    while ((entry = zipInputStream.getNextEntry()) != null) {
      String entryName = entry.getName();

      if (entry.isDirectory()) {
        subfolder = entryName;
        containsSubfolder = true;
      }
      if (containsSubfolder && entryName.contains(subfolder) && entryName.endsWith(".txt")) {
        String[] files = entryName.split("/");
        String lastElement = files[files.length - 1];

        if (GtfsFiles.containsGtfsFile(lastElement)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Creates a specific GtfsInput to read a GTFS ZIP archive from the given URL.
   *
   * <p>Necessary parent directories will be created.
   *
   * @param sourceUrl the fully qualified URL to download of the resource to download
   * @param targetPath the path to store the downloaded GTFS archive
   * @param noticeContainer
   * @return the {@code GtfsInput} created after download of the GTFS archive
   * @throws IOException if GTFS archive cannot be stored at the specified location
   * @throws URISyntaxException if URL is malformed
   */
  public static GtfsInput createFromUrl(
      URL sourceUrl, Path targetPath, NoticeContainer noticeContainer)
      throws IOException, URISyntaxException {
    // getParent() may return null if there is no parent, so call toAbsolutePath() first.
    Path targetDirectory = targetPath.toAbsolutePath().getParent();
    if (!Files.isDirectory(targetDirectory)) {
      Files.createDirectories(targetDirectory);
    }
    try (OutputStream outputStream = Files.newOutputStream(targetPath)) {
      loadFromUrl(sourceUrl, outputStream);
    }
    return createFromPath(targetPath, noticeContainer);
  }

  /**
   * Creates a specific GtfsInput to read data from the given URL. The loaded ZIP file is kept in
   * memory.
   *
   * @param sourceUrl the fully qualified URL to download of the resource to download
   * @param noticeContainer
   * @return the {@code GtfsInput} created after download of the GTFS archive
   * @throws IOException if no file could not be found at the specified location
   * @throws URISyntaxException if URL is malformed
   */
  public static GtfsInput createFromUrlInMemory(URL sourceUrl, NoticeContainer noticeContainer)
      throws IOException, URISyntaxException {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      loadFromUrl(sourceUrl, outputStream);
      File zipFile = new File(sourceUrl.toString());
      String fileName = zipFile.getName().replace(".zip", "");
      if (hasSubfolderWithGtfsFile(sourceUrl)) {

        noticeContainer.addValidationNotice(
            new InvalidInputFilesInSubfolderNotice(invalidInputMessage));
      }
      return new GtfsZipFileInput(
          new ZipFile(new SeekableInMemoryByteChannel(outputStream.toByteArray())),
          fileName);
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
