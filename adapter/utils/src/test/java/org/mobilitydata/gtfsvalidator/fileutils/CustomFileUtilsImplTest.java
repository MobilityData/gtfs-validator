package org.mobilitydata.gtfsvalidator.fileutils;

import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;

import static org.junit.jupiter.api.Assertions.*;

class CustomFileUtilsImplTest {

    private void fileOrDirectorySizeTest(final String filename, final int expectedSizeByte, final int bufferByte) {
    //noinspection UnstableApiUsage
        assertEquals(expectedSizeByte,
                CustomFileUtilsImpl.getInstance().sizeOf(Resources.getResource(filename).getPath()),
                bufferByte);
    }

    @Test
    void shouldMatchSizeOfMockFile() {
        fileOrDirectorySizeTest("mockFile.txt", 1125, 50);
    }

    @Test
    void shouldMatchSizeOfMockFile2() {
        fileOrDirectorySizeTest("mockFile2.txt", 2063, 50);
    }

    @Test
    void shouldMatchSizeOfMockFile3() {
        fileOrDirectorySizeTest("mockFile3.txt", 2580, 50);
    }

    @Test
    void shouldMatchSizeOfMockDirectory() {
        fileOrDirectorySizeTest("mockDirectory.txt", 5768, 150);
    }
}
