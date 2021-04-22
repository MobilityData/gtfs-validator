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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;

/**
 * Implements support for GTFS ZIP archives.
 *
 * <p>The underlying Apache Commons ZipFile supports reading local files as well as bytes in memory.
 */
public class GtfsZipFileInput extends GtfsInput {
  private final ImmutableSet<String> filenames;
  private final ZipFile zipFile;

  public GtfsZipFileInput(ZipFile zipFile) {
    this.zipFile = zipFile;

    ImmutableSet.Builder<String> filenamesBuilder = new Builder<>();
    for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
      ZipArchiveEntry entry = entries.nextElement();
      if (!isInsideZipDirectory(entry)) {
        filenamesBuilder.add(entry.getName());
      }
    }
    filenames = filenamesBuilder.build();
  }

  static boolean isInsideZipDirectory(ZipArchiveEntry entry) {
    // We do not use File.separator because the .zip file specification states:
    // All slashes MUST be forward slashes '/' as opposed to backwards slashes '\' for compatibility
    // with Amiga and
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
    if (!filenames.contains(filename)) {
      throw new FileNotFoundException(filename);
    }
    return zipFile.getInputStream(zipFile.getEntry(filename));
  }

  /**
   * Closes the archive.
   *
   * @throws IOException if an error occurs closing the archive.
   */
  @Override
  public void close() throws IOException {
    zipFile.close();
  }
}
