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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

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
}
