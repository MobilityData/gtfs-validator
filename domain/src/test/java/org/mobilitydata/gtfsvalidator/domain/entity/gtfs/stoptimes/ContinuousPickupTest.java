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

class ContinuousPickupTest {

    @Test
    void createContinuousPickupFromNullValueShouldReturnNoContinuousPickup() {
        assertEquals(ContinuousPickup.NO_CONTINUOUS_PICKUP, ContinuousPickup.fromInt(null));
    }

    @Test
    void createContinuousPickupFromValidValue0ShouldReturnContinuousPickup() {
        assertEquals(ContinuousPickup.CONTINUOUS_PICKUP, ContinuousPickup.fromInt(0));
    }

    @Test
    void createContinuousPickupFromValidValue1ShouldReturnNoContinuousPickup() {
        assertEquals(ContinuousPickup.NO_CONTINUOUS_PICKUP, ContinuousPickup.fromInt(1));
    }

    @Test
    void createContinuousPickupFromValidValue2ShouldReturnMustPhoneAgencyContinuousPickup() {
        assertEquals(ContinuousPickup.MUST_PHONE_CONTINUOUS_STOPPING_PICKUP, ContinuousPickup.fromInt(2));
    }

    @Test
    void createContinuousPickupFromValidValue3ShouldReturnMustAskDriveContinuousPickup() {
        assertEquals(ContinuousPickup.MUST_ASK_DRIVER_CONTINUOUS_STOPPING_PICKUP, ContinuousPickup.fromInt(3));
    }

    @Test
    void createContinuousPickupFromInvalidValue5ShouldReturnNull() {
        assertNull(ContinuousPickup.fromInt(5));
    }

    @Test
    void createContinuousPickupFromInvalidValue54ShouldReturnNull() {
        assertNull(ContinuousPickup.fromInt(54));
    }

    @Test
    void nullValueShouldReturnTrue() {
        assertTrue(ContinuousPickup.isEnumValid(null));
    }

    @Test
    void validValue2ShouldReturnTrue() {
        assertTrue(ContinuousPickup.isEnumValid(2));
    }

    @Test
    void invalidValue23ShouldReturnFalse() {
        assertFalse(ContinuousPickup.isEnumValid(23));
    }
}
