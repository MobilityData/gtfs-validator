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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TransfersTest {

    @Test
    void createTransferFromNullValueShouldReturnDefaultValue() {
        assertEquals(Transfers.UNLIMITED_TRANSFERS, Transfers.fromInt(null));
    }

    @Test
    void createTransferFromIntegerValue0ShouldReturnNoTransferAllowed() {
        assertEquals(Transfers.NO_TRANSFERS_ALLOWED, Transfers.fromInt(0));
    }

    @Test
    void createTransferFromIntegerValue1ShouldReturnOneTransferAllowed() {
        assertEquals(Transfers.ONE_TRANSFER_ALLOWED, Transfers.fromInt(1));
    }

    @Test
    void createTransferFromIntegerValue2ShouldReturnTwoTransfersAllowed() {
        assertEquals(Transfers.TWO_TRANSFER_ALLOWED, Transfers.fromInt(2));
    }

    @Test
    void createTransferFromInvalidIntegerValue3ShouldReturnNull() {
        assertNull(Transfers.fromInt(3));
    }

    @Test
    void createTransferFromInvalidIntegerValue4ShouldReturnNull() {
        assertNull(Transfers.fromInt(4));
    }
}