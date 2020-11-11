package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DownloadArchiveFromNetworkTest {

    @Test
    void downloadDataFromValidUrlShouldNotGenerateNotice() {
        // TODO
    }

    @Test
    void downloadDataFromInvalidUrlShouldGenerateAddCannotDownloadArchiveFromNetworkNoticeToResultRepo() {
        // TODO
    }

    @Test
    void downloadDataFromInvalidUrlShouldThrowException() {
        // TODO
    }

    @Test
    void tooBigDatasetShouldGenerateAndAddDatasetTooBigNoticeToResultRepo() {
        // TODO
    }

    @Test
    void notTooBigDatasetShouldNotGenerateNotice() {
        // TODO
    }

    @Test
    void datasetShouldBeDownloadedRegardlessOfRedirection() {
        // TODO
    }
}
