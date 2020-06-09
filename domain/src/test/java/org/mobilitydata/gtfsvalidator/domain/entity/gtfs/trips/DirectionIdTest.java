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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.INBOUND;
import static org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.DirectionId.OUTBOUND;

class DirectionIdTest {

    @Test
    void createEnumDirectionIdWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final DirectionId underTest = DirectionId.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    void createEnumDirectionIdWithNullValueShouldReturnNull() {
        assertNull(DirectionId.fromInt(null));
    }

    @Test
    void createEnumDirectionIdWithValidValue0ShouldReturnOutbound() {
        final int validValue = 0;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(OUTBOUND, underTest);
    }

    @Test
    void createEnumDirectionIdWithValidValue0ShouldReturnInbound() {
        final int validValue = 1;
        final DirectionId underTest = DirectionId.fromInt(validValue);
        assertEquals(INBOUND, underTest);
    }

    @Test
    void invalidValue23ShouldReturnFalse() {
        assertFalse(DirectionId.isEnumValueValid(23));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(1));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(0));
    }

    @Test
    void validValueNullShouldReturnTrue() {
        assertTrue(DirectionId.isEnumValueValid(null));
    }
}