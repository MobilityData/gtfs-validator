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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Implements support for GTFS ZIP archives.
 */
public class GtfsZipFileInput extends GtfsInput {
    private final Set<String> filenames = new HashSet();
    private final ZipFile zipFile;

    public GtfsZipFileInput(File file) throws IOException {
        zipFile = new ZipFile(file);
        for (Enumeration<? extends ZipEntry> i = zipFile.entries(); i
                .hasMoreElements(); ) {
            ZipEntry entry = i.nextElement();
            if (!isInsideZipDirectory(entry)) {
                filenames.add(entry.getName());
            }
        }
    }

    static boolean isInsideZipDirectory(ZipEntry entry) {
        // We do not use File.separator because the .zip file specification states:
        // All slashes MUST be forward slashes '/' as opposed to backwards slashes '\' for compatibility with Amiga and
        // UNIX file systems etc.
        //
        // Directory names in end with '/'.
        return entry.getName().contains("/");
    }

    @Override
    public Set<String> getFilenames() {
        return Collections.unmodifiableSet(filenames);
    }

    @Override
    public InputStream getFile(String filename) throws IOException {
        ZipEntry entry = zipFile.getEntry(filename);
        if (entry == null) {
            throw new FileNotFoundException(
                    Paths.get(zipFile.getName(), filename).toString());
        }
        return zipFile.getInputStream(entry);
    }
}
