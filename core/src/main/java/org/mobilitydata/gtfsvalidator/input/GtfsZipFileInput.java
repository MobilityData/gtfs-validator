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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
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

  private final String MACOSX_FILE_IN_ZIP = ".DS_Store";

  public GtfsZipFileInput(ZipFile zipFile, String zipFileName) {
    this.zipFile = zipFile;
    StringBuilder strBuilder = new StringBuilder();
    strBuilder.append(zipFileName);
    strBuilder.append("/");
    String macDirectory = strBuilder.toString();
    ImmutableSet.Builder<String> filenamesBuilder = new ImmutableSet.Builder<>();
    boolean isMacZip = false;
    for (Enumeration<ZipArchiveEntry> entries = zipFile.getEntries(); entries.hasMoreElements(); ) {
      ZipArchiveEntry entry = entries.nextElement();
      String entryName = entry.getName();
      // check if the first entry is a directory with the name of the zip file
      if (entryName.endsWith("/")) {
        String firstEntryName = entryName.replaceFirst("/", "");
        if (entry.isDirectory() && zipFileName.equals(firstEntryName)) {
          isMacZip = true;
        }
      }
      if (isMacZip) {
        entryName = entry.getName().replaceFirst(macDirectory, "");
      }
      // Check for .DS_Store to prevent generating unknown_file notice.
      // Directory names in end with '/'.
      if (!entryName.isBlank()
          && !entryName.equals(MACOSX_FILE_IN_ZIP)
          && !entryName.contains("/")) {
        filenamesBuilder.add(entryName);
      }
    }
    filenames = filenamesBuilder.build();
  }

  @Override
  public ImmutableSet<String> getFilenames() {
    return filenames;
  }

  @Override
  public InputStream getFile(String filename) throws IOException {
    if (!filenames.contains(filename)) {
      throw new FileNotFoundException(filename);
    }
    synchronized (zipFile) {
      return zipFile.getInputStream(zipFile.getEntry(filename));
    }
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
