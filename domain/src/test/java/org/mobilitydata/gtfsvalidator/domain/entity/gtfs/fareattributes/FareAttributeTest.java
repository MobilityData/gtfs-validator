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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedValueNotice;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@SuppressWarnings("unchecked")
class FareAttributeTest {
    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final Integer VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final Integer VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void createFareAttributeWithValidValuesShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        underTest.build(mockNoticeCollection);
        assertTrue(underTest.build(mockNoticeCollection).getData() instanceof FareAttribute);
    }

    @Test
    void createFareAttributeWithNullFareIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(null)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<MissingRequiredValueNotice> data = (List<MissingRequiredValueNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("fare_id", data.get(0).getFieldName());
        assertEquals("no id", data.get(0).getEntityId());
    }

    @Test
    void createFareAttributeWithNullPriceShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(null)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<MissingRequiredValueNotice> data = (List<MissingRequiredValueNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("price", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
    }

    @Test
    void createFareAttributeWithNullCurrencyTypeShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(null)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<MissingRequiredValueNotice> data = (List<MissingRequiredValueNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("currency_type", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
    }

    @Test
    void createFareAttributeWithNullPaymentMethodGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(null)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<MissingRequiredValueNotice> data = (List<MissingRequiredValueNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("payment_method", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
    }

    @Test
    void createFareAttributeWithInvalidTransfersShouldThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(4)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<UnexpectedValueNotice> data = (List<UnexpectedValueNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("transfers", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
        assertEquals("4", data.get(0).getEnumValue());
    }

    @Test
    void createFareAttributeWithNullTransfersShouldNotThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(null)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build(mockNoticeCollection).getData() instanceof FareAttribute);
    }

    @Test
    void createFareAttributeWithNegativePriceShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();
        underTest.fareId(STRING_TEST)
                .price(-2.0F)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<FloatFieldValueOutOfRangeNotice> data = (List<FloatFieldValueOutOfRangeNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("price", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
        assertEquals(0, data.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, data.get(0).getRangeMax());
    }

    @Test
    void createFareAttributeWithNegativeTimeDurationShouldThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(-20);

        final var fareAttribute = underTest.build(mockNoticeCollection);
        assertTrue(fareAttribute.getData() instanceof ArrayList);

        final List<IntegerFieldValueOutOfRangeNotice> data = (List<IntegerFieldValueOutOfRangeNotice>) fareAttribute.getData();

        assertEquals("fare_attributes.txt", data.get(0).getFilename());
        assertEquals("transfer_duration", data.get(0).getFieldName());
        assertEquals(STRING_TEST, data.get(0).getEntityId());
        assertEquals(0, data.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, data.get(0).getRangeMax());
    }
}