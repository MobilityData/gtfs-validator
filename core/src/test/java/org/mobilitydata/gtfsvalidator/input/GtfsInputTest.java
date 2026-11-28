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

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

import com.sun.net.httpserver.HttpServer;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.InvalidInputFilesInSubfolderNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;

@RunWith(JUnit4.class)
public class GtfsInputTest {
  // The URL tests download from a local HTTP server, so they don't depend on the network.
  // /feed.zip serves a small GTFS zip; any other path answers 404 with an HTML body.
  private HttpServer server;
  private String validUrl;
  private String invalidUrl;

  @Before
  public void startServer() throws IOException {
    ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
    try (ZipOutputStream out = new ZipOutputStream(zipBytes)) {
      out.putNextEntry(new ZipEntry("stops.txt"));
      out.closeEntry();
    }
    byte[] feed = zipBytes.toByteArray();
    byte[] notFound = "<html><body>Not found</body></html>".getBytes(StandardCharsets.UTF_8);

    server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
    server.createContext(
        "/",
        exchange -> {
          boolean found = exchange.getRequestURI().getPath().equals("/feed.zip");
          byte[] body = found ? feed : notFound;
          exchange.sendResponseHeaders(found ? 200 : 404, body.length);
          try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
          }
        });
    server.start();
    String base =
        "http://" + server.getAddress().getHostString() + ":" + server.getAddress().getPort();
    validUrl = base + "/feed.zip";
    invalidUrl = base + "/invalid-feed.zip";
  }

  @After
  public void stopServer() {
    server.stop(0);
  }

  public Path getZipStoreMethodPath() throws URISyntaxException {
    // A ZIP file with compression level 0 ("STORE" method).
    return Path.of(getClass().getResource("stored_with_data_descriptor.zip").toURI());
  }

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();
  NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void inputNotFound_throwsException() {
    assertThrows(
        FileNotFoundException.class,
        () -> GtfsInput.createFromPath(Paths.get("/no/such/file"), noticeContainer));
  }

  @Test
  public void directoryInput() throws IOException {
    File rootDir = tmpDir.newFolder("unarchived");
    tmpDir.newFile("unarchived/stops.txt");

    try (GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.toPath(), noticeContainer)) {
      assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void zipInput() throws IOException {
    File zipFile = tmpDir.newFile("archived.zip");
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
      ZipEntry e = new ZipEntry("stops.txt");
      out.putNextEntry(e);
      out.closeEntry();
    }

    try (GtfsInput gtfsInput = GtfsInput.createFromPath(zipFile.toPath(), noticeContainer)) {
      assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void createFromPath_detectsGtfsFileInSubfolder() throws IOException {
    assertSubfolderNotice("subfolder.zip", "sub/stops.txt", true);
    assertSubfolderNotice("flat.zip", "stops.txt", false);
    assertSubfolderNotice("other.zip", "sub/readme.md", false);
  }

  @Test
  public void createFromPath_rootDirectoryEntry_isIgnored() throws IOException {
    // A zip made by some tools starts with a directory entry named "/" (the archive root).
    File zipFile = tmpDir.newFile("root-dir-entry.zip");
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
      out.putNextEntry(new ZipEntry("/"));
      out.closeEntry();
      out.putNextEntry(new ZipEntry("stops.txt"));
      out.closeEntry();
    }
    NoticeContainer notices = new NoticeContainer();
    try (GtfsInput input = GtfsInput.createFromPath(zipFile.toPath(), notices)) {
      assertThat(notices.getValidationNotices()).isEmpty();
      assertThat(input.getFilenames()).contains("stops.txt");
    }
  }

  private void assertSubfolderNotice(String zipName, String entryName, boolean expected)
      throws IOException {
    File zipFile = tmpDir.newFile(zipName);
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
      out.putNextEntry(new ZipEntry(entryName));
      out.closeEntry();
    }
    NoticeContainer notices = new NoticeContainer();
    try (GtfsInput unused = GtfsInput.createFromPath(zipFile.toPath(), notices)) {
      if (expected) {
        assertThat(notices.getValidationNotices())
            .containsExactly(new InvalidInputFilesInSubfolderNotice());
      } else {
        assertThat(notices.getValidationNotices()).isEmpty();
      }
    }
  }

  @Test
  public void createFromUrl_valid_success() throws IOException, URISyntaxException {
    try (GtfsInput underTest =
        GtfsInput.createFromUrl(
            new URL(validUrl),
            tmpDir.getRoot().toPath().resolve("storage"),
            noticeContainer,
            "1.0.1")) {
      assertThat(underTest).isInstanceOf(GtfsZipFileInput.class);
      assertThat(underTest.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void createFromUrl_invalid_throwsException() {
    assertThrows(
        IOException.class,
        () ->
            GtfsInput.createFromUrl(
                new URL(invalidUrl),
                tmpDir.getRoot().toPath().resolve("storage"),
                noticeContainer,
                "1.0.1"));
  }

  @Test
  public void createFromUrlInMemory_valid_success() throws IOException, URISyntaxException {
    try (GtfsInput underTest =
        GtfsInput.createFromUrlInMemory(new URL(validUrl), noticeContainer, "1.0.1")) {
      assertThat(underTest).isInstanceOf(GtfsZipFileInput.class);
      assertThat(underTest.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void createFromUrlInMemory_invalid_throwsException() {
    assertThrows(
        IOException.class,
        () -> GtfsInput.createFromUrlInMemory(new URL(invalidUrl), noticeContainer, "1.0.1"));
  }

  @Test
  public void createFromZipBytes_returnsReadableInput() throws IOException {
    ByteArrayOutputStream zipBytes = new ByteArrayOutputStream();
    try (ZipOutputStream out = new ZipOutputStream(zipBytes)) {
      out.putNextEntry(new ZipEntry("stops.txt"));
      out.write("stop_id,stop_name\ns1,S\n".getBytes(StandardCharsets.UTF_8));
      out.closeEntry();
    }
    try (GtfsInput underTest =
            GtfsInput.createFromZipBytes(zipBytes.toByteArray(), "feed", noticeContainer);
        InputStream stops = underTest.getFile("stops.txt")) {
      assertThat(underTest.getFilenames()).containsExactly("stops.txt");
      assertThat(new String(stops.readAllBytes(), StandardCharsets.UTF_8)).startsWith("stop_id,");
    }
  }

  @Test
  public void createFromZipBytes_zipWithStoredMethod_success()
      throws IOException, URISyntaxException {
    Path zipStoreMethodPath = getZipStoreMethodPath();
    byte[] zipBytes = Files.readAllBytes(zipStoreMethodPath);
    String fileName = zipStoreMethodPath.getFileName().toString().replace(".zip", "");
    // This will throw if we don't handle ZIP files with compression
    // level 0 ("STORE" level) properly.
    try (GtfsInput underTest = GtfsInput.createFromZipBytes(zipBytes, fileName, noticeContainer)) {
      assertThat(underTest instanceof GtfsZipFileInput);
      // The archive contains two files; we check that they are accessible.
      underTest.getFile("agency.txt");
      underTest.getFile("stops.txt");
    } catch (Exception e) {
      fail("Failed to load ZIP file: " + e.getMessage());
    }
  }

  @Test
  public void createFromPath_zipWithStoredMethod_success() {
    // This will throw if we don't handle ZIP files with compression
    // level 0 ("STORE" level) properly.
    try (GtfsInput underTest = GtfsInput.createFromPath(getZipStoreMethodPath(), noticeContainer)) {
      assertThat(underTest instanceof GtfsZipFileInput);
      // The archive contains two files; we check that they are accessible.
      underTest.getFile("agency.txt");
      underTest.getFile("stops.txt");
    } catch (Exception e) {
      fail("Failed to load ZIP file: " + e.getMessage());
    }
  }
}
