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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
class FareAttributeTest {
    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final Integer VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final Integer VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void createFareAttributeWithValidValuesShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof FareAttribute);

        verify(mockNoticeCollection, times(1)).clear();
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNullFareIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(null)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("fare_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNullPriceShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(null)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("price", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithInvalidPriceShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(-4.0f)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<FloatFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(FloatFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<FloatFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("price", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Float.MAX_VALUE, noticeList.get(0).getRangeMax());
        assertEquals(-4.0f, noticeList.get(0).getActualValue());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNullCurrencyTypeShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(null)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("currency_type", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNullPaymentMethodShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(null)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("payment_method", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithInvalidPaymentMethodShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(4)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof List);

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("payment_method", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithInvalidTransfersShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(4)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof ArrayList);

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();


        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("transfers", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());
        assertEquals("4", noticeList.get(0).getEnumValue());
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNullTransfersShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(null)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof FareAttribute);

        verify(mockNoticeCollection, times(1)).clear();
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createFareAttributeWithNegativeTimeDurationShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder(mockNoticeCollection);

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(-20);

        assertTrue(underTest.build().getData() instanceof ArrayList);

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals("transfer_duration", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST, noticeList.get(0).getEntityId());
        assertEquals(0, noticeList.get(0).getRangeMin());
        assertEquals(Integer.MAX_VALUE, noticeList.get(0).getRangeMax());

        verify(mockNoticeCollection, times(1)).add(ArgumentMatchers.isA(Notice.class));
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}