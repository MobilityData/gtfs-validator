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
        Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        underTest.fromStopId(null)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field from_stop_id can not be null in transfers.txt", exception.getMessage());
    }

    @Test
    void createTransferWithNullToStopIdShouldThrowException() {
        Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        //noinspection ConstantConditions
        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(null)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field to_stop_id can not be null in transfers.txt", exception.getMessage());
    }

    @Test
    void createTransferWithUnexpectedTransferTypeValueShouldThrowException() {
        Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(4)
                .minTransferTime(VALID_MIN_TRANSFER_TIME_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value encountered for field transfer_type transfers.txt",
                exception.getMessage());
    }

    @Test
    void createTransferWithInvalidMinTransferTimeValueShouldThrowException() {
        Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(-20);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value encountered for field min_transfer_time transfers.txt",
                exception.getMessage());
    }

    @Test
    void createTransferWithNullMinTransferTimeValueShouldNotThrowException() {
        Transfer.TransferBuilder underTest = new Transfer.TransferBuilder();

        underTest.fromStopId(STRING_TEST_VALUE)
                .toStopId(STRING_TEST_VALUE)
                .transferType(VALID_TRANSFER_TYPE_VALUE)
                .minTransferTime(null)
                .build();
    }
}