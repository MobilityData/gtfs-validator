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
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsInputTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  @Test
  public void inputNotFound() {
    assertThrows(
        FileNotFoundException.class, () -> GtfsInput.createFromPath(Paths.get("/no/such/file")));
  }

  @Test
  public void directoryInput() throws IOException {
    File rootDir = tmpDir.newFolder("unarchived");
    tmpDir.newFile("unarchived/stops.txt");

    try (GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.toPath())) {
      assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void zipInput() throws IOException {
    File zipFile = tmpDir.newFile("archived.zip");
    ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
    ZipEntry e = new ZipEntry("stops.txt");
    out.putNextEntry(e);
    out.closeEntry();
    out.close();

    try (GtfsInput gtfsInput = GtfsInput.createFromPath(zipFile.toPath())) {
      assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void createFromValidUrlShouldNotThrowException()
      throws IOException, URISyntaxException, InterruptedException {
    final Path storage = tmpDir.getRoot().toPath().resolve("storage");
    try (GtfsInput underTest =
        GtfsInput.createFromUrl(
            new URL(
                "https://github.com/MobilityData/gtfs-validator/raw/v1.4.0/usecase/src/test/resources/"
                    + "valid_zip_sample.zip"),
            storage.toString())) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }
  }

  @Test
  public void createFromRedirectedUrlShouldNotThrowException()
      throws IOException, URISyntaxException, InterruptedException {
    final Path storage1 = tmpDir.getRoot().toPath().resolve("storage1");
    try (GtfsInput underTest =
        GtfsInput.createFromUrl(
            new URL(
                "http://github.com/MobilityData/gtfs-validator/raw/v1.4.0/usecase/src/test/resources/"
                    + "valid_zip_sample.zip"),
            storage1.toString())) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }

    // URL from #398
    final Path storage2 = tmpDir.getRoot().toPath().resolve("storage2");
    try (GtfsInput underTest =
        GtfsInput.createFromUrl(
            new URL("https://octa.net/current/google_transit.zip"), storage2.toString())) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }
  }

  @Test
  public void createFromInvalidUrlShouldThrowException() {
    assertThrows(
        IOException.class,
        () ->
            GtfsInput.createFromUrl(
                new URL(
                    "https://openmobilitydata.org/p/mobilitydata-invalid-dataset/197/latest/download"),
                tmpDir.getRoot().toPath().resolve("storage").toString()));
  }

  @Test
  public void createFromValidUrlInMemoryShouldNotThrowException()
      throws IOException, URISyntaxException {
    try (GtfsInput underTest =
        GtfsInput.createFromUrlInMemory(
            new URL(
                "https://github.com/MobilityData/gtfs-validator/raw/v1.4.0/usecase/src/test/resources/"
                    + "valid_zip_sample.zip"))) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }
  }

  @Test
  public void createFromRedirectedUrlInMemoryShouldNotThrowException()
      throws IOException, URISyntaxException {
    try (GtfsInput underTest =
        GtfsInput.createFromUrlInMemory(
            new URL(
                "http://github.com/MobilityData/gtfs-validator/raw/v1.4.0/usecase/src/test/resources/"
                    + "valid_zip_sample.zip"))) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }

    // URL from #398
    try (GtfsInput underTest =
        GtfsInput.createFromUrlInMemory(new URL("https://octa.net/current/google_transit.zip"))) {
      assertThat(underTest instanceof GtfsZipFileInput);
    }
  }
}
