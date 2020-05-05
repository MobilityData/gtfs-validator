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

import static org.junit.jupiter.api.Assertions.*;

class PaymentMethodTest {

    @Test
    void createPaymentMethodFromNullValueShouldReturnNull() {
        assertNull(PaymentMethod.fromInt(null));
    }

    @Test
    void createPaymentMethodFromInvalidValue2ShouldReturnNull() {
        assertNull(PaymentMethod.fromInt(2));
    }

    @Test
    void createPaymentMethodFromValidValue0ShouldReturnNull() {
        assertEquals(PaymentMethod.ON_BOARD, PaymentMethod.fromInt(0));
    }

    @Test
    void createPaymentMethodFromValidValue1ShouldReturnNull() {
        assertEquals(PaymentMethod.BEF0RE_BOARDING, PaymentMethod.fromInt(1));
    }

    @Test
    void validValue0ShouldReturnTrue() {
        assertTrue(PaymentMethod.isEnumValueValid(0));
    }

    @Test
    void validValue1ShouldReturnTrue() {
        assertTrue(PaymentMethod.isEnumValueValid(1));
    }

    @Test
    void nullValueShouldReturnFalse() {
        assertFalse(PaymentMethod.isEnumValueValid(null));
    }

    @Test
    void invalidValue5ShouldReturnFalse() {
        assertFalse(PaymentMethod.isEnumValueValid(5));
    }
}