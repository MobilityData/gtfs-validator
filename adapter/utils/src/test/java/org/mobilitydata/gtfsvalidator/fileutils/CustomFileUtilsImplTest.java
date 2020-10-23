package org.mobilitydata.gtfsvalidator.fileutils;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(5768,
                CustomFileUtilsImpl.getInstance().sizeOfDirectory(
                        Path.of(Resources.getResource("testDirectory").getPath())));
    }

    @Test
    void shouldMathSizeOfZipFile() {
        fileSizeTest(Path.of("./test_file.zip"), 2156);
    }
}
