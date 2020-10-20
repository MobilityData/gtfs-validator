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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IsBidirectionalTest {

    @Test
    void createIsBidirectionalWithNullValueShouldReturnNull() {
        final Integer unexpectedEnumValue = null;
        assertNull(IsBidirectional.fromInt(unexpectedEnumValue));
    }

    @Test
    void createIsBidirectionalWithInvalidValue3ShouldReturnNull() {
        final Integer unexpectedEnumValue = 3;
        assertNull(IsBidirectional.fromInt(unexpectedEnumValue));
    }

    @Test
    void createIsBidirectionalWithValidValue0ShouldReturnUnidirectional() {
        assertEquals(IsBidirectional.UNIDIRECTIONAL, IsBidirectional.fromInt(0));
    }

    @Test
    void createIsBidirectionalWithValidValue1ShouldReturnBidirectional() {
        assertEquals(IsBidirectional.BIDIRECTIONAL, IsBidirectional.fromInt(1));
    }

    @Test
    void isEnumValueValidShouldReturnFalseOnNullValue() {
        assertFalse(IsBidirectional.isEnumValueValid(null));
    }
}
