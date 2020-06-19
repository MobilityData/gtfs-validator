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

class DropOffTypeTest {

    @Test
    void createDropOffFromNullValueShouldReturnRegularDropOff() {
        assertEquals(DropOffType.REGULAR_DROP_OFF, DropOffType.fromInt(null));
    }

    @Test
    void createDropOffFromValidValue0ShouldReturnRegularDropOff() {
        assertEquals(DropOffType.REGULAR_DROP_OFF, DropOffType.fromInt(0));
    }

    @Test
    void createDropOffFromValidValue1ShouldReturnNoDropOff() {
        assertEquals(DropOffType.NO_DROP_OFF, DropOffType.fromInt(1));
    }

    @Test
    void createDropOffFromValidValue2ShouldReturnMustPhoneDropOff() {
        assertEquals(DropOffType.MUST_PHONE_DROP_OFF, DropOffType.fromInt(2));
    }

    @Test
    void createDropOffFromValidValue3ShouldReturnMustAskDriveDropOff() {
        assertEquals(DropOffType.MUST_ASK_DRIVER_DROP_OFF, DropOffType.fromInt(3));
    }


    @Test
    void createDropOffFromInvalidValue5ShouldReturnNull() {
        assertNull(DropOffType.fromInt(5));
    }

    @Test
    void createDropOffFromInvalidValue54ShouldReturnNull() {
        assertNull(DropOffType.fromInt(54));
    }

    @Test
    void nullValueShouldReturnTrue() {
        assertTrue(DropOffType.isEnumValid(null));
    }

    @Test
    void validValue2ShouldReturnTrue() {
        assertTrue(DropOffType.isEnumValid(2));
    }

    @Test
    void invalidValue23ShouldReturnFalse() {
        assertFalse(DropOffType.isEnumValid(23));
    }
}
