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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExactTimesTest {

    @Test
    void createExactTimesFromNullValueShouldReturnFrequencyBasedTrips() {
        assertEquals(ExactTimes.FREQUENCY_BASED_TRIPS, ExactTimes.fromInt(null));
    }

    @Test
    void createExactTimesFromIntegerValue0ShouldReturnFrequencyBasedTrips() {
        assertEquals(ExactTimes.FREQUENCY_BASED_TRIPS, ExactTimes.fromInt(0));
    }

    @Test
    void createExactTimesFromIntegerValue1ShouldReturnScheduleBasedTrips() {
        assertEquals(ExactTimes.SCHEDULE_BASED_TRIPS, ExactTimes.fromInt(1));
    }

    @Test
    void createExactTimesFromInvalidIntegerValue2ShouldReturnNull() {
        assertNull(ExactTimes.fromInt(2));
    }

    @Test
    void createExactTimesFromInvalidNegativeIntegerValueMinus1ShouldReturnNull() {
        assertNull(ExactTimes.fromInt(-1));
    }

    @Test
    void validNullValueShouldReturnTrue() {
        assertTrue(ExactTimes.isEnumValueValid(null));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(ExactTimes.isEnumValueValid(0));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(ExactTimes.isEnumValueValid(1));
    }

    @Test
    void invalidValue2ShouldReturnFalse() {
        assertFalse(ExactTimes.isEnumValueValid(2));
    }

    @Test
    void invalidValueMinus1ShouldReturnFalse() {
        assertFalse(ExactTimes.isEnumValueValid(-1));
    }
}
