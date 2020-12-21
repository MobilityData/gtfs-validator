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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(JUnit4.class)
public class GtfsZipInMemoryInputTest {
    @Test
    public void zipInput() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteStream);
        ZipEntry e = new ZipEntry("stops.txt");
        out.putNextEntry(e);
        final String content = "stops";
        out.write(content.getBytes());
        out.closeEntry();
        out.close();

        GtfsInput gtfsInput = new GtfsZipInMemoryInput("archived.zip", byteStream.toByteArray());
        assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
        byte[] bytes = new byte[content.length()];
        gtfsInput.getFile("stops.txt").read(bytes);
        assertThat(bytes).isEqualTo(content.getBytes());
        assertThrows(FileNotFoundException.class, () -> gtfsInput.getFile("missing.txt"));
    }

    @Test
    public void skipFilesInDirectories() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteStream);

        out.putNextEntry(new ZipEntry("stops.txt"));
        out.closeEntry();

        out.putNextEntry(new ZipEntry("nested/file.txt"));
        out.closeEntry();

        out.close();

        GtfsInput gtfsInput = new GtfsZipInMemoryInput("archived.zip", byteStream.toByteArray());
        assertThat(gtfsInput.getFilenames()).containsExactly("stops.txt");
        assertThrows(FileNotFoundException.class, () -> gtfsInput.getFile("nested/file.txt"));
    }

    @Test
    public void noFileExtension() throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(byteStream);

        out.putNextEntry(new ZipEntry("noext"));
        out.closeEntry();

        out.close();

        GtfsInput gtfsInput = new GtfsZipInMemoryInput("archived.zip", byteStream.toByteArray());
        assertThat(gtfsInput.getFilenames()).containsExactly("noext");
    }
}
