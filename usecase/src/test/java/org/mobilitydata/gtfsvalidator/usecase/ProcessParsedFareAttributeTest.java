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

package org.mobilitydata.gtfsvalidator.usecase;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes.FareAttribute;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedFareAttributeTest {
    private static final String STRING_TEST = "string test";
    private static final float VALID_PRICE_FLOAT = 2.0f;
    private static final int VALID_TRANSFERS_INTEGER = 0;
    private static final int VALID_PAYMENT_METHOD_INTEGER = 1;
    private static final int VALID_TRANSFER_DURATION_INTEGER = 20;
    private static final String FARE_ID = "fare_id";
    private static final String PRICE = "price";
    private static final String CURRENCY_TYPE = "currency_type";
    private static final String PAYMENT_METHOD = "payment_method";
    private static final String TRANSFERS = "transfers";
    private static final String AGENCY_ID = "agency_id";
    private static final String TRANSFER_DURATION = "transfer_duration";

    @Test
    void validatedFareAttributeShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder =
                mock(FareAttribute.FareAttributeBuilder.class, RETURNS_SELF);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFareAttribute).when(mockGenericObject).getData();

        doReturn(mockGenericObject).when(mockBuilder).build();

        when(mockParsedFareAttribute.get(FARE_ID)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(PRICE)).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get(CURRENCY_TYPE)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(PAYMENT_METHOD)).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get(TRANSFERS)).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get(AGENCY_ID)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(TRANSFER_DURATION)).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        when(mockGtfsDataRepo.addFareAttribute(mockFareAttribute)).thenReturn(mockFareAttribute);

        final ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedFareAttribute);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PRICE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(CURRENCY_TYPE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PAYMENT_METHOD));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFERS));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFER_DURATION));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();


        verify(mockGtfsDataRepo, times(1))
                .addFareAttribute(ArgumentMatchers.eq(mockFareAttribute));

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void invalidFareAttributeShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final List<Notice> mockNoticeCollection = spy(new ArrayList<>());
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(false);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockNoticeCollection).when(mockGenericObject).getData();

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedFareAttribute.get(FARE_ID)).thenReturn(null);
        when(mockParsedFareAttribute.get(PRICE)).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get(CURRENCY_TYPE)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(PAYMENT_METHOD)).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get(TRANSFERS)).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get(AGENCY_ID)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(TRANSFER_DURATION)).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        underTest.execute(mockParsedFareAttribute);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PRICE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(CURRENCY_TYPE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PAYMENT_METHOD));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFERS));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFER_DURATION));

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(null));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    void duplicateFareAttributeShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final FareAttribute.FareAttributeBuilder mockBuilder = mock(FareAttribute.FareAttributeBuilder.class,
                RETURNS_SELF);
        final ParsedEntity mockParsedFareAttribute = mock(ParsedEntity.class);
        final FareAttribute mockFareAttribute = mock(FareAttribute.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);
        when(mockGenericObject.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockFareAttribute).when(mockGenericObject).getData();

        when(mockFareAttribute.getFareId()).thenReturn(FARE_ID);

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedFareAttribute underTest =
                new ProcessParsedFareAttribute(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedFareAttribute.get(FARE_ID)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(PRICE)).thenReturn(VALID_PRICE_FLOAT);
        when(mockParsedFareAttribute.get(CURRENCY_TYPE)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(PAYMENT_METHOD)).thenReturn(VALID_PAYMENT_METHOD_INTEGER);
        when(mockParsedFareAttribute.get(TRANSFERS)).thenReturn(VALID_TRANSFERS_INTEGER);
        when(mockParsedFareAttribute.get(AGENCY_ID)).thenReturn(STRING_TEST);
        when(mockParsedFareAttribute.get(TRANSFER_DURATION)).thenReturn(VALID_TRANSFER_DURATION_INTEGER);

        underTest.execute(mockParsedFareAttribute);

        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(FARE_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PRICE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(CURRENCY_TYPE));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(PAYMENT_METHOD));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFERS));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedFareAttribute, times(1)).get(ArgumentMatchers.eq(TRANSFER_DURATION));

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedFareAttribute, times(1)).getEntityId();

        verify(mockBuilder, times(1)).fareId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1)).price(ArgumentMatchers.eq(VALID_PRICE_FLOAT));
        verify(mockBuilder, times(1)).currencyType(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .paymentMethod(ArgumentMatchers.eq(VALID_PAYMENT_METHOD_INTEGER));
        verify(mockBuilder, times(1)).transfers(ArgumentMatchers.eq(VALID_TRANSFERS_INTEGER));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(STRING_TEST));
        verify(mockBuilder, times(1))
                .transferDuration(ArgumentMatchers.eq(VALID_TRANSFER_DURATION_INTEGER));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockGtfsDataRepo, times(1)).addFareAttribute(mockFareAttribute);

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("fare_attributes.txt", noticeList.get(0).getFilename());
        assertEquals(FARE_ID, noticeList.get(0).getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedFareAttribute, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }
}