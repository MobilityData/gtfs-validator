package org.mobilitydata.gtfsvalidator.fileutils;

import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CustomFileUtilsImplTest {

    private void fileSizeTest(final Path pathToFile, final int expectedSizeByte, final int bufferByte) {
    //noinspection UnstableApiUsage
        assertEquals(expectedSizeByte,
                CustomFileUtilsImpl.getInstance().sizeOf(
                        Path.of(Resources.getResource(pathToFile.toString()).getPath())
                ),
                bufferByte);
    }

    @Test
    void shouldMatchSizeOfMockFile() {
        fileSizeTest(Path.of("./mockDirectory/mockFile.txt"), 1125, 50);
    }

    @Test
    void shouldMatchSizeOfMockFile2() {
        fileSizeTest(Path.of("./mockDirectory/mockFile2.txt"), 2063, 50);
    }

    @Test
    void shouldMatchSizeOfMockFile3() {
        fileSizeTest(Path.of("./mockDirectory/mockFile3.txt"), 2580, 50);
    }

    @Test
    void shouldMatchSizeOfMockDirectory() {
        assertEquals(5768,
                CustomFileUtilsImpl.getInstance().sizeOfDirectory(
                Path.of(Resources.getResource("mockDirectory").getPath())));
    }

    @Test
    void shouldMathSizeOfZipFile() {
        fileSizeTest(Path.of("./mockZippedFile.zip"), 2156, 50);
    }
}
