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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BikesAllowedStatusTest {

    @Test
    void createEnumBikesAllowedStatusWithInvalidValueShouldReturnNull() {
        final int invalidValue = 3;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(invalidValue);
        assertNull(underTest);
    }

    @Test
    void createEnumBikesAllowedStatusWithNullValueShouldReturnDefaultValue() {
        final Integer nullValue = null;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(nullValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue0ShouldReturnMatchingEnumValue() {
        final Integer validValue = 0;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue1ShouldReturnMatchingEnumValue() {
        final Integer validValue = 1;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.BIKES_ALLOWED);
    }

    @Test
    void createEnumBikesAllowedStatusWithValidValue2ShouldReturnMatchingEnumValue() {
        final Integer validValue = 2;
        final BikesAllowedStatus underTest = BikesAllowedStatus.fromInt(validValue);
        assertEquals(underTest, BikesAllowedStatus.NO_BIKES_ALLOWED);
    }

    @Test
    void invalidValue4ShouldReturnFalse() {
        assertFalse(BikesAllowedStatus.isEnumValueValid(4));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(BikesAllowedStatus.isEnumValueValid(0));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(BikesAllowedStatus.isEnumValueValid(1));
    }

    @Test
    void validValue2ShouldReturnTrue() {
        assertTrue(BikesAllowedStatus.isEnumValueValid(2));
    }

    @Test
    void validValueNullShouldReturnTrue() {
        assertTrue(BikesAllowedStatus.isEnumValueValid(null));
    }
}