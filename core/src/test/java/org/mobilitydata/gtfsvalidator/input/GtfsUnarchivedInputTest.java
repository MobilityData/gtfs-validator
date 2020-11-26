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

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class GtfsUnarchivedInputTest {

    @Rule
    public final TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void skipFilesInDirectories() throws IOException {
        File rootDir = tmpDir.newFolder("unarchived");
        tmpDir.newFile("unarchived/stops.txt");
        tmpDir.newFolder("unarchived/empty-folder");
        tmpDir.newFolder("unarchived/nested-folder");
        tmpDir.newFile("unarchived/nested-folder/nested.txt");

        GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.getAbsolutePath());
        assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }

    @Test
    public void noExt() throws IOException {
        File rootDir = tmpDir.newFolder("unarchived");
        tmpDir.newFile("unarchived/noext");

        GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.getAbsolutePath());
        assertThat(gtfsInput.getFilenames()).containsExactly("noext");
    }
}
