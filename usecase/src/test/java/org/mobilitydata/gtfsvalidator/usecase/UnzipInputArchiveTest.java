package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnzipInputArchiveTest {

    @Test
    void unzipFolderThatContainsMacOsXFolderShouldNotGenerateNoticeAndFillRawFileRepo() {}

    @Test
    void unzipFolderThatContainsFolderShouldGenerateNotice() {}

    @Test
    void unzipFolderWithLegitimateFileShouldNotGenerateNoticeAndFillRawFileRepo() {}
}
