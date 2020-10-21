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

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void isEnumValueValidShouldReturnTrueOnNullValue() {
        assertTrue(TransferType.isEnumValueValid(null));
    }
}
