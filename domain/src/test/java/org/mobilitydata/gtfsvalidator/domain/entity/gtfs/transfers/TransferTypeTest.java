package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransferTypeTest {

    @Test
    void createTransferTypeWithValidValue0ShouldReturnRecommendedTransferPoint() {
        final int expectedValue = 0;
        assertEquals(TransferType.RECOMMENDED_TRANSFER_POINT, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue1ShouldReturnTimesTransferPoint() {
        final int expectedValue = 1;
        assertEquals(TransferType.TIMED_TRANSFER_POINT, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue2ShouldReturnMinimumTimeTransfer() {
        final int expectedValue = 2;
        assertEquals(TransferType.MINIMUM_TIME_TRANSFER, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithValidValue3ShouldReturnImpossibleTransfers() {
        final int expectedValue = 3;
        assertEquals(TransferType.IMPOSSIBLE_TRANSFERS, TransferType.fromInt(expectedValue));
    }

    @Test
    void createTransferTypeWithNullValueShouldReturnRecommendedTransferPoint() {
        assertEquals(TransferType.RECOMMENDED_TRANSFER_POINT, TransferType.fromInt(null));
    }

    @Test
    void createTransferTypeWithInvalidValue11ShouldReturnNull() {
        final int unexpectedValue = 11;
        assertNull(TransferType.fromInt(unexpectedValue));
    }

    @Test
    void createTransferTypeWithInvalidValue15ShouldReturnNull() {
        final int unexpectedValue = 15;
        assertNull(TransferType.fromInt(unexpectedValue));
    }
}