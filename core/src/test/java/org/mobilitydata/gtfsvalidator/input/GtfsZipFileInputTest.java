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

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsZipFileInputTest {

  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  @Test
  public void skipFilesInDirectories() throws IOException {
    File zipFile = tmpDir.newFile("archived.zip");
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
      out.putNextEntry(new ZipEntry("stops.txt"));
      out.closeEntry();

      out.putNextEntry(new ZipEntry("nested/file.txt"));
      out.closeEntry();
    }

    try (GtfsZipFileInput gtfsInput = new GtfsZipFileInput(new ZipFile(zipFile))) {
      assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }
  }

  @Test
  public void noFileExtension() throws IOException {
    File zipFile = tmpDir.newFile("archived.zip");
    try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {
      out.putNextEntry(new ZipEntry("noext"));
      out.closeEntry();
    }

    try (GtfsZipFileInput gtfsInput = new GtfsZipFileInput(new ZipFile(zipFile))) {
      assertThat(gtfsInput.getFilenames()).containsExactly("noext");
    }
  }
}
