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

class FareAttributeTest {

    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final Integer VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final Integer VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void createFareAttributeWithValidValuesShouldNotThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertDoesNotThrow(builder::build);
    }

    @Test
    void createFareAttributeWithNullFareIdShouldThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(null)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        Exception exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("field `fare_id` in file `fare_attributes.txt` can not be null", exception.getMessage());
    }

    @Test
    void createFareAttributeWithNullPriceShouldThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(null)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        Exception exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("field `price` in file `fare_attributes.txt` can not be null", exception.getMessage());
    }

    @Test
    void createFareAttributeWithNullCurrencyTypeShouldThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(null)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        Exception exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("field `currency_type` in file `fare_attributes.txt` can not be null", exception.getMessage());
    }

    @Test
    void createFareAttributeWithNullPaymentMethodShouldThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(null)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        Exception exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("unexpected value encountered for field `payment_method` in file `fare_attributes.txt`",
                exception.getMessage());
    }

    @Test
    void createFareAttributeWithInvalidTransfersShouldThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(4)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        Exception exception = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("unexpected value encountered for field `transfers` in file `fare_attributes.txt`",
                exception.getMessage());
    }

    @Test
    void createFareAttributeWithNullTransfersShouldNotThrowException() {
        FareAttribute.FareAttributeBuilder builder = new FareAttribute.FareAttributeBuilder();

        builder.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(null)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertDoesNotThrow(builder::build);
    }
}