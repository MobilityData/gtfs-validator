/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferTest {

    private final static String STRING_TEST_VALUE = "to stop id";
    private final static int VALID_TRANSFER_TYPE_VALUE = 1;
    private final static int VALID_MIN_TRANSFER_TIME_VALUE = 20;

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
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

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
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