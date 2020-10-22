package org.mobilitydata.gtfsvalidator.fileutils;

import org.junit.jupiter.api.Test;
import com.google.common.io.Resources;

import static org.junit.jupiter.api.Assertions.*;

class CustomFileUtilsImplTest {

    @Test
    void shouldReturnSizeOfFile() {
        assertEquals(1125,
                CustomFileUtilsImpl.getInstance().sizeOf(Resources.getResource("mockFile.txt").getPath()),
                50);
        assertEquals(2063,
                CustomFileUtilsImpl.getInstance().sizeOf(Resources.getResource("mockFile2.txt").getPath()),
                50);

        assertEquals(2580,
                CustomFileUtilsImpl.getInstance().sizeOf(Resources.getResource("mockFile3.txt").getPath()),
                50);
    }

    @Test
    void shouldReturnSizeOfDirectory() {
        assertEquals(5768,
                CustomFileUtilsImpl.getInstance().sizeOf(Resources.getResource("mockDirectory").getPath()),
                150);
    }
}
