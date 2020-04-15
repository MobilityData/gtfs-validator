package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferTest {

    private final static String STRING_TEST_VALUE = "to stop id";
    private final static int VALID_TRANSFER_TYPE_VALUE = 1;
    private final static int VALID_MIN_TRANSFER_TIME_VALUE = 20;

    @Test
    void createTransferWithNullFromStopIdShouldThrowException() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        underTest.fromStopId(null)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field from_stop_id can not be null in transfers.txt", exception.getMessage());
    }

    @Test
    void createTransferWithNullToStopIdShouldThrowException() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(null)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field to_stop_id can not be null in transfers.txt", exception.getMessage());
    }

    @Test
    void createTransferWithUnexpectedTransferTypeValueShouldThrowException() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(4)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value encountered for field transfer_type transfers.txt",
                exception.getMessage());
    }

    @Test
    void createTransferWithInvalidMinTransferTimeValueShouldThrowException() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(-20);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value encountered for field min_transfer_time transfers.txt",
                exception.getMessage());
    }

    @Test
    void createTransferWithNullMinTransferTimeValueShouldNotThrowException() {
        final Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(null)
                .build();
    }
}