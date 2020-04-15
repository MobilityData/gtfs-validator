package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransferTypeTest {

    @Test
    void createTransferTypeWithValidValue0ShouldReturnRecommendedTransferPoint() {
        int expectedValue = 0;
        assertEquals(TransferType.RECOMMENDED_TRANSFER_POINT, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue1ShouldReturnTimesTransferPoint() {
        int expectedValue = 1;
        assertEquals(TransferType.TIMED_TRANSFER_POINT, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue2ShouldReturnMinimumTimeTransfer() {
        int expectedValue = 2;
        assertEquals(TransferType.MINIMUM_TIME_TRANSFER, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue3ShouldReturnImpossibleTransfers() {
        int expectedValue = 3;
        assertEquals(TransferType.IMPOSSIBLE_TRANSFERS, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithNullValueShouldReturnRecommendedTransferPoint() {
        assertEquals(TransferType.RECOMMENDED_TRANSFER_POINT, TransferType.fromInt(null));
    }

    @Test
    void createTransferTypeWithInvalidValue11ShouldReturnNull() {
        int unexpectedValue = 11;
        assertNull(TransferType.fromInt(unexpectedValue));
    }

    @Test
    void createTransferTypeWithInvalidValue15ShouldReturnNull() {
        int unexpectedValue = 15;
        assertNull(TransferType.fromInt(unexpectedValue));
    }
}