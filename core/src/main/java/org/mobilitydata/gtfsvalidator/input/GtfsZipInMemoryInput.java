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

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Implements support for GTFS ZIP archives located at given {@code java.nio.file.Path}.
 */
public class GtfsZipInMemoryInput extends GtfsInput {
    private final Set<String> filenames = new HashSet<>();
    private final String path;
    private final byte[] bytes;

    public GtfsZipInMemoryInput(String path, byte[] bytes) throws IOException {
        this.path = path;
        this.bytes = bytes;

        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry entry = zipInputStream.getNextEntry();
        while (entry != null) {
            if (!insideZipDirectory(entry)) {
                filenames.add(entry.getName());
            }
            entry = zipInputStream.getNextEntry();
        }
    }

    private boolean insideZipDirectory(ZipEntry entry) {
        // We do not use File.separator because the .zip file specification states:
        // All slashes MUST be forward slashes '/' as opposed to backwards slashes '\' for compatibility with Amiga and
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
            throw new FileNotFoundException(path + ":" + filename);
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bytes));
        ZipEntry entry = zipInputStream.getNextEntry();
        for (; ; ) {
            if (entry.getName().equals(filename)) {
                return zipInputStream;
            }
            entry = zipInputStream.getNextEntry();
        }
    }
}
