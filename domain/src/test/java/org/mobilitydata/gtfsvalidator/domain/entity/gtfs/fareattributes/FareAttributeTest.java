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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;

class FareAttributeTest {
    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final Integer VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final Integer VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;

    @Test
    void createFareAttributeWithValidValuesShouldNotGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof FareAttribute);
    }

    @Test
    void createFareAttributeWithNullFareIdShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(null)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("fare_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithNullPriceShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(null)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("price", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithInvalidPriceShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(-4.0f)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("price", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(0.0f, notice.getExtra(NOTICE_SPECIFIC_KEY__RANGE_MIN));
        assertEquals(Float.MAX_VALUE, notice.getExtra(NOTICE_SPECIFIC_KEY__RANGE_MAX));
        assertEquals(-4.0f, notice.getExtra(NOTICE_SPECIFIC_KEY__ACTUAL_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithNullCurrencyTypeShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(null)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("currency_type", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithNullPaymentMethodShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(null)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("payment_method", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithInvalidPaymentMethodShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(4)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("payment_method", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(4, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithInvalidTransfersShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(4)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("transfers", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(4, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createFareAttributeWithNullTransfersShouldNotGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(null)
                .agencyId(STRING_TEST)
                .transferDuration(VALID_TRANSFER_DURATION_INTEGER);

        assertTrue(underTest.build().getData() instanceof FareAttribute);
    }

    @Test
    void createFareAttributeWithNegativeTimeDurationShouldGenerateNotice() {
        final FareAttribute.FareAttributeBuilder underTest = new FareAttribute.FareAttributeBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.fareId(STRING_TEST)
                .price(VALID_PRICE_FLOAT)
                .currencyType(STRING_TEST)
                .paymentMethod(VALID_PAYMENT_METHOD_INTEGER)
                .transfers(VALID_TRANSFERS_INTEGER)
                .agencyId(STRING_TEST)
                .transferDuration(-20)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This is designed so that method getData returns a list of notices. Thereby there is no need for cast check.
        //noinspection unchecked
        final List<IntegerFieldValueOutOfRangeNotice> noticeCollection =
                (List<IntegerFieldValueOutOfRangeNotice>) entityBuildResult.getData();
        final IntegerFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals("fare_attributes.txt", notice.getFilename());
        assertEquals("transfer_duration", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals(STRING_TEST, notice.getEntityId());
        assertEquals(0, notice.getExtra(NOTICE_SPECIFIC_KEY__RANGE_MIN));
        assertEquals(Integer.MAX_VALUE, notice.getExtra(NOTICE_SPECIFIC_KEY__RANGE_MAX));
        assertEquals(1, noticeCollection.size());
    }
}