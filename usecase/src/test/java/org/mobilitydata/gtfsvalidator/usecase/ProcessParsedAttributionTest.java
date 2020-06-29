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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Attribution;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
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
import static org.mockito.Mockito.*;

class ProcessParsedAttributionTest {
    private static final String ATTRIBUTION_ID = "attribution_id";
    private static final String AGENCY_ID = "agency_id";
    private static final String ROUTE_ID = "route_id";
    private static final String TRIP_ID = "trip_id";
    private static final String ORGANIZATION_NAME = "organization_name";
    private static final String ATTRIBUTION_URL = "attribution_url";
    private static final String ATTRIBUTION_EMAIL = "attribution_email";
    private static final String ATTRIBUTION_PHONE = "attribution_phone";
    private static final String IS_PRODUCER = "is_producer";
    private static final String IS_AUTHORITY = "is_authority";
    private static final String IS_OPERATOR = "is_operator";

    @Test
    void validParsedAttributionShouldNotGenerateNoticeAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Attribution.AttributionBuilder mockBuilder = mock(Attribution.AttributionBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedAttribution = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockEntityBuildResult = mock(EntityBuildResult.class);
        final Attribution mockAttribution = mock(Attribution.class);

        doReturn(mockEntityBuildResult).when(mockBuilder).build();
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockAttribution).when(mockEntityBuildResult).getData();

        when(mockParsedAttribution.get(ATTRIBUTION_ID)).thenReturn(ATTRIBUTION_ID);
        when(mockParsedAttribution.get(AGENCY_ID)).thenReturn(AGENCY_ID);
        when(mockParsedAttribution.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedAttribution.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedAttribution.get(ORGANIZATION_NAME)).thenReturn(ORGANIZATION_NAME);
        when(mockParsedAttribution.get(IS_PRODUCER)).thenReturn(1);
        when(mockParsedAttribution.get(IS_AUTHORITY)).thenReturn(0);
        when(mockParsedAttribution.get(IS_OPERATOR)).thenReturn(0);
        when(mockParsedAttribution.get(ATTRIBUTION_URL)).thenReturn(ATTRIBUTION_URL);
        when(mockParsedAttribution.get(ATTRIBUTION_EMAIL)).thenReturn(ATTRIBUTION_EMAIL);
        when(mockParsedAttribution.get(ATTRIBUTION_PHONE)).thenReturn(ATTRIBUTION_PHONE);

        when(mockGtfsDataRepo.addAttribution(mockAttribution)).thenReturn(mockAttribution);

        final ProcessParsedAttribution underTest =
                new ProcessParsedAttribution(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedAttribution);

        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_PRODUCER));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_AUTHORITY));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_OPERATOR));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_PHONE));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).attributionId(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq(TRIP_ID));
        verify(mockBuilder, times(1)).organizationName(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockBuilder, times(1)).isProducer(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).isAuthority(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).isOperator(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).attributionUrl(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockBuilder, times(1)).attributionEmail(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockBuilder, times(1)).attributionPhone(ArgumentMatchers.eq(ATTRIBUTION_PHONE));
        verify(mockBuilder, times(1)).build();

        verify(mockGtfsDataRepo, times(1)).addAttribution(ArgumentMatchers.eq(mockAttribution));

        verifyNoMoreInteractions(mockBuilder, mockAttribution, mockParsedAttribution, mockGtfsDataRepo);
    }

    @Test
    void invalidParsedAttributionShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Attribution.AttributionBuilder mockBuilder = mock(Attribution.AttributionBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedAttribution = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final Notice mockNotice = mock(MissingRequiredValueNotice.class);

        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(noticeCollection).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(false);
        noticeCollection.add(mockNotice);

        doReturn(mockGenericObject).when(mockBuilder).build();

        final ProcessParsedAttribution underTest =
                new ProcessParsedAttribution(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedAttribution.get(ATTRIBUTION_ID)).thenReturn(ATTRIBUTION_ID);
        when(mockParsedAttribution.get(AGENCY_ID)).thenReturn(AGENCY_ID);
        when(mockParsedAttribution.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedAttribution.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedAttribution.get(ORGANIZATION_NAME)).thenReturn(ORGANIZATION_NAME);
        when(mockParsedAttribution.get(IS_PRODUCER)).thenReturn(1);
        when(mockParsedAttribution.get(IS_AUTHORITY)).thenReturn(0);
        when(mockParsedAttribution.get(IS_OPERATOR)).thenReturn(0);
        when(mockParsedAttribution.get(ATTRIBUTION_URL)).thenReturn(ATTRIBUTION_URL);
        when(mockParsedAttribution.get(ATTRIBUTION_EMAIL)).thenReturn(ATTRIBUTION_EMAIL);
        when(mockParsedAttribution.get(ATTRIBUTION_PHONE)).thenReturn(ATTRIBUTION_PHONE);

        underTest.execute(mockParsedAttribution);

        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_PRODUCER));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_AUTHORITY));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_OPERATOR));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_PHONE));

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).attributionId(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq(TRIP_ID));
        verify(mockBuilder, times(1)).organizationName(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockBuilder, times(1)).isProducer(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).isAuthority(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).isOperator(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).attributionUrl(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockBuilder, times(1)).attributionEmail(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockBuilder, times(1)).attributionPhone(ArgumentMatchers.eq(ATTRIBUTION_PHONE));
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(MissingRequiredValueNotice.class));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        verifyNoMoreInteractions(mockParsedAttribution, mockGtfsDataRepo, mockBuilder, mockResultRepo,
                mockGenericObject);
    }

    @Test
    void duplicateParsedAttributionShouldGenerateNoticeAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Attribution.AttributionBuilder mockBuilder = mock(Attribution.AttributionBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedAttribution = mock(ParsedEntity.class);
        final EntityBuildResult<?> mockGenericObject = mock(EntityBuildResult.class);

        final Attribution mockAttribution = mock(Attribution.class);
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        doReturn(mockAttribution).when(mockGenericObject).getData();
        when(mockGenericObject.isSuccess()).thenReturn(true);

        doReturn(mockGenericObject).when(mockBuilder).build();
        when(mockGtfsDataRepo.addAttribution(mockAttribution)).thenReturn(null);

        final ProcessParsedAttribution underTest =
                new ProcessParsedAttribution(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedAttribution.get(ATTRIBUTION_ID)).thenReturn(ATTRIBUTION_ID);
        when(mockParsedAttribution.get(AGENCY_ID)).thenReturn(AGENCY_ID);
        when(mockParsedAttribution.get(ROUTE_ID)).thenReturn(ROUTE_ID);
        when(mockParsedAttribution.get(TRIP_ID)).thenReturn(TRIP_ID);
        when(mockParsedAttribution.get(ORGANIZATION_NAME)).thenReturn(ORGANIZATION_NAME);
        when(mockParsedAttribution.get(IS_PRODUCER)).thenReturn(1);
        when(mockParsedAttribution.get(IS_AUTHORITY)).thenReturn(0);
        when(mockParsedAttribution.get(IS_OPERATOR)).thenReturn(0);
        when(mockParsedAttribution.get(ATTRIBUTION_URL)).thenReturn(ATTRIBUTION_URL);
        when(mockParsedAttribution.get(ATTRIBUTION_EMAIL)).thenReturn(ATTRIBUTION_EMAIL);
        when(mockParsedAttribution.get(ATTRIBUTION_PHONE)).thenReturn(ATTRIBUTION_PHONE);

        underTest.execute(mockParsedAttribution);

        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(TRIP_ID));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_PRODUCER));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_AUTHORITY));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(IS_OPERATOR));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockParsedAttribution, times(1)).get(ArgumentMatchers.eq(ATTRIBUTION_PHONE));
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedAttribution, times(1)).getEntityId();

        verify(mockBuilder, times(1)).clear();
        verify(mockBuilder, times(1)).attributionId(ArgumentMatchers.eq(ATTRIBUTION_ID));
        verify(mockBuilder, times(1)).agencyId(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockBuilder, times(1)).routeId(ArgumentMatchers.eq(ROUTE_ID));
        verify(mockBuilder, times(1)).tripId(ArgumentMatchers.eq(TRIP_ID));
        verify(mockBuilder, times(1)).organizationName(ArgumentMatchers.eq(ORGANIZATION_NAME));
        verify(mockBuilder, times(1)).isProducer(ArgumentMatchers.eq(1));
        verify(mockBuilder, times(1)).isAuthority(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).isOperator(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).attributionUrl(ArgumentMatchers.eq(ATTRIBUTION_URL));
        verify(mockBuilder, times(1)).attributionEmail(ArgumentMatchers.eq(ATTRIBUTION_EMAIL));
        verify(mockBuilder, times(1)).attributionPhone(ArgumentMatchers.eq(ATTRIBUTION_PHONE));
        verify(mockBuilder, times(1)).build();

        verify(mockGtfsDataRepo, times(1)).addAttribution(mockAttribution);

        verify(mockGenericObject, times(1)).isSuccess();
        // suppressed warning regarding unused result of method, since this behavior is wanted
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("attributions.txt", noticeList.get(0).getFilename());
        assertEquals("organization_name", noticeList.get(0).getNoticeSpecific(Notice.KEY_FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedAttribution, mockResultRepo, mockGtfsDataRepo, mockAttribution, mockBuilder,
                mockGenericObject);
    }
}