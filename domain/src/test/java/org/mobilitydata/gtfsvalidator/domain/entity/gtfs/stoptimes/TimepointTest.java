/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TimepointTest {

    @Test
    void createTimepointFromValidValue0shouldReturnApproximatedTimes() {
        assertEquals(Timepoint.APPROXIMATED_TIMES, Timepoint.fromInt(0));
    }

    @Test
    void createTimepointFromValidValue1ShouldReturnExactTimes() {
        assertEquals(Timepoint.EXACT_TIMES, Timepoint.fromInt(1));
    }

    @Test
    void createTimepointFromNullValueShouldReturnExactTimes() {
        assertEquals(Timepoint.EXACT_TIMES, Timepoint.fromInt(null));
    }

    @Test
    void createTimepointFromInvalidValue5ShouldReturnNull() {
        assertNull(Timepoint.fromInt(5));
    }

    @Test
    void createTimepointFromInvalidValue54ShouldReturnNull() {
        assertNull(Timepoint.fromInt(54));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(Timepoint.isEnumValid(0));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(Timepoint.isEnumValid(1));
    }

    @Test
    void invalidValue13ShouldReturnFalse() {
        assertFalse(Timepoint.isEnumValid(13));
    }

    @Test
    void invalidNullValueShouldReturnTrue() {
        assertTrue(Timepoint.isEnumValid(null));
    }
}
