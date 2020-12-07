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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class GtfsInputTest {

    @Rule
    public final TemporaryFolder tmpDir = new TemporaryFolder();

    @Test
    public void inputNotFound() {
        assertThrows(
                FileNotFoundException.class,
                () -> GtfsInput.createFromPath("/no/such/file"));
    }

    @Test
    public void directoryInput() throws IOException {
        File rootDir = tmpDir.newFolder("unarchived");
        tmpDir.newFile("unarchived/stops.txt");

        GtfsInput gtfsInput = GtfsInput.createFromPath(rootDir.getAbsolutePath());
        assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }

    @Test
    public void zipInput() throws IOException {
        File zipFile = tmpDir.newFile("archived.zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));
        ZipEntry e = new ZipEntry("stops.txt");
        out.putNextEntry(e);
        out.closeEntry();
        out.close();

        GtfsInput gtfsInput = GtfsInput.createFromPath(zipFile.getAbsolutePath());
        assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
    }

    @Test
    public void createFromValidUrlShouldNotThrowException() throws IOException {
        GtfsInput underTest = GtfsInput.createFromUrl(
                new URL("https://octa.net/current/google_transit.zip"));
        assertThat(underTest instanceof GtfsZipFileInput);
        // remove created file
        File toDelete = new File("storage");
        assertTrue(toDelete.delete());

    }

    @Test
    public void createFromInvalidUrlShouldThrowException() {
        assertThrows(
                IOException.class, () -> GtfsInput.createFromUrl(
                        new URL("https://octa.net/current/gowkdjhfiouwehfogle_transit.zip")));
    }
}
