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

class PickupTypeTest {

    @Test
    void createPickupFromNullValueShouldReturnRegularPickup() {
        assertEquals(PickupType.REGULAR_PICKUP, PickupType.fromInt(null));
    }

    @Test
    void createPickupFromValidValue0ShouldReturnRegularPickup() {
        assertEquals(PickupType.REGULAR_PICKUP, PickupType.fromInt(0));
    }

    @Test
    void createPickupFromValidValue1ShouldReturnNoPickup() {
        assertEquals(PickupType.NO_PICKUP, PickupType.fromInt(1));
    }

    @Test
    void createPickupFromValidValue2ShouldReturnMustPhonePickup() {
        assertEquals(PickupType.MUST_PHONE_PICKUP, PickupType.fromInt(2));
    }

    @Test
    void createPickupFromValidValue3ShouldReturnMustAskDriverPickup() {
        assertEquals(PickupType.MUST_ASK_DRIVER_PICKUP, PickupType.fromInt(3));
    }

    @Test
    void createPickupFromInvalidValue5ShouldReturnNull() {
        assertNull(PickupType.fromInt(5));
    }

    @Test
    void createPickupFromInvalidValue54ShouldReturnNull() {
        assertNull(PickupType.fromInt(54));
    }

    @Test
    void nullValueShouldReturnTrue() {
        assertTrue(PickupType.isEnumValid(null));
    }

    @Test
    void validValue2ShouldReturnTrue() {
        assertTrue(PickupType.isEnumValid(2));
    }

    @Test
    void invalidValue23ShouldReturnFalse() {
        assertFalse(PickupType.isEnumValid(23));
    }
}
