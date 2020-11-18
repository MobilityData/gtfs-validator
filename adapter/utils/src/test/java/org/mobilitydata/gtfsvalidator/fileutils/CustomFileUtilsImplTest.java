package org.mobilitydata.gtfsvalidator.fileutils;

import com.google.common.io.Resources;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.usecase.utils.CustomFileUtils;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CustomFileUtilsImplTest {

    private void fileSizeTest(final Path pathToFile, final int expectedSizeByte) {
        //noinspection UnstableApiUsage
        assertEquals(expectedSizeByte,
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(pathToFile.toString()).getPath())
                ),
                50);
    }

    @Test
    void shouldMatchSizeOfTestFile() {
        fileSizeTest(Path.of("testDirectory/test_file.txt"), 1125);
    }

    @Test
    void shouldMatchSizeOfMockDirectory() {
        //noinspection UnstableApiUsage
        assertEquals(5768,
                CustomFileUtilsImpl.getInstance().sizeOfDirectory(
                        Path.of(Resources.getResource("testDirectory").getPath())));
    }

    @Test
    void shouldMatchSizeOfZipFile() {
        fileSizeTest(Path.of("./test_file.zip"), 2156);
    }

    @Test
    void shouldReturnSizeOfFileInBytes() {
        //noinspection UnstableApiUsage
        assertEquals(2156,
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(Path.of("./test_file.zip").toString()).getPath())
                ));

        //noinspection UnstableApiUsage
        assertEquals(2156,
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(Path.of("./test_file.zip").toString()).getPath()),
                        CustomFileUtils.Unit.BYTES
                ));

    }
    @Test
    void nullUnitShouldThrowException() {
        //noinspection UnstableApiUsage
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(Path.of("./test_file.zip").toString()).getPath()),
                        null
                ));

        assertEquals("Unit must not be null if provided", exception.getMessage());
    }

    @Test
    void shouldReturnSizeOfFileInMegabytes() {
        //noinspection UnstableApiUsage
        assertEquals(2156/FileUtils.ONE_MB,
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(Path.of("./test_file.zip").toString()).getPath()),
                        CustomFileUtils.Unit.MEGABYTES
                ));
    }
}
