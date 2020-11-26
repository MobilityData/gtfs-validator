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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * Implements support for unarchived GTFS directories.
 */
public class GtfsUnarchivedInput implements GtfsInput {
    private final Set<String> filenames = new HashSet();
    private final File directory;

    public GtfsUnarchivedInput(File directory) throws IOException {
        this.directory = directory;
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                filenames.add(file.getName());
            }
        }
    }

    @Override
    public Set<String> getFilenames() {
        return filenames;
    }

    @Override
    public InputStream getFile(String filename) throws IOException {
        return new FileInputStream(new File(directory, filename));
    }
}
