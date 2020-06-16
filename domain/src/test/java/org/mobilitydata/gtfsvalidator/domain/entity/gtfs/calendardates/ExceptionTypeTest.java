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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionTypeTest {

    @Test
    void createExceptionTypeWithExpectedValue1ShouldReturnAddedService() {
        final int expectedValue = 1;
        assertEquals(ExceptionType.ADDED_SERVICE, ExceptionType.fromInt(expectedValue));
    }

    @Test
    void createExceptionTypeWithExpectedValue2ShouldReturnRemovedService() {
        final int expectedValue = 2;
        assertEquals(ExceptionType.REMOVED_SERVICE, ExceptionType.fromInt(expectedValue));
    }

    @Test
    void createExceptionTypeWithUnexpectedValue3ShouldReturnNull() {
        final int unexpectedValue = 3;
        assertNull(ExceptionType.fromInt(unexpectedValue));
    }


    @Test
    void createExceptionTypeWithUnexpectedValue4ShouldReturnNull() {
        final int unexpectedValue = 4;
        assertNull(ExceptionType.fromInt(unexpectedValue));
    }

    @Test
    void createExceptionTypeWithNullValueShouldReturnNull() {
        assertNull(ExceptionType.fromInt(null));
    }

    @Test
    void invalidValue6ShouldReturnFalse() {
        assertFalse(ExceptionType.isEnumValueValid(6));
    }

    @Test
    void validValue1ShouldReturnFalse() {
        assertTrue(ExceptionType.isEnumValueValid(1));
    }

    @Test
    void validValue2ShouldReturnFalse() {
        assertTrue(ExceptionType.isEnumValueValid(2));
    }
}