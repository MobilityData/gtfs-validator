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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

// some fields are annotated as `@NonNull` but test require these fields to be null. Therefore annotation
// "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
class ProcessParsedAgencyTest {
    private static final String STRING_TEST_VALUE = "test value";
    private static final String AGENCY_ID = "agency_id";
    private static final String AGENCY_NAME = "agency_name";
    private static final String AGENCY_URL = "agency_url";
    private static final String AGENCY_TIMEZONE = "agency_timezone";
    private static final String AGENCY_LANG = "agency_lang";
    private static final String AGENCY_PHONE = "agency_phone";
    private static final String AGENCY_FARE_URL = "agency_fare_url";
    private static final String AGENCY_EMAIL = "agency_email";
    private static final String FILENAME = "agency.txt";
    private static final String ENTITY_ID = "no id";

    @Test
    void validatedParsedAgencyShouldCreateAgencyEntityAndBeAddedToGtfsDataRepository() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class, RETURNS_SELF);
        final Agency mockAgency = mock(Agency.class);
        final ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockEntityBuildResult = mock(EntityBuildResult.class);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockEntityBuildResult);
        when(mockEntityBuildResult.isSuccess()).thenReturn(true);
        when(mockEntityBuildResult.getData()).thenReturn(mockAgency);

        when(mockParsedAgency.get(anyString())).thenReturn(STRING_TEST_VALUE);

        when(mockGtfsDataRepo.addAgency(mockAgency)).thenReturn(mockAgency);

        final ProcessParsedAgency underTest = new ProcessParsedAgency(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedAgency);

        final InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockBuilder, mockParsedAgency);

        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_NAME));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_TIMEZONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_LANG));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_PHONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_FARE_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_EMAIL));

        verify(mockBuilder, times(1)).agencyId(anyString());
        verify(mockBuilder, times(1)).agencyName(anyString());
        verify(mockBuilder, times(1)).agencyUrl(anyString());
        verify(mockBuilder, times(1)).agencyTimezone(anyString());
        verify(mockBuilder, times(1)).agencyLang(anyString());
        verify(mockBuilder, times(1)).agencyPhone(anyString());
        verify(mockBuilder, times(1)).agencyFareUrl(anyString());
        verify(mockBuilder, times(1)).agencyEmail(anyString());

        inOrder.verify(mockBuilder, times(1)).build();
        inOrder.verify(mockGtfsDataRepo, times(1)).addAgency(ArgumentMatchers.eq(mockAgency));

        verifyNoMoreInteractions(mockBuilder, mockAgency, mockParsedAgency, mockGtfsDataRepo);
    }

    @Test
    void invalidAgencyShouldNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        final List<Notice> noticeCollection = new ArrayList<>();
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        when(mockNotice.getFilename()).thenReturn(FILENAME);
        when(mockNotice.getFieldName()).thenReturn(AGENCY_NAME);
        when(mockNotice.getEntityId()).thenReturn(ENTITY_ID);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(noticeCollection);
        when(mockGenericObject.isSuccess()).thenReturn(false);
        noticeCollection.add(mockNotice);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        final ProcessParsedAgency underTest = new ProcessParsedAgency(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedAgency.get(AGENCY_ID)).thenReturn(AGENCY_ID);
        when(mockParsedAgency.get(AGENCY_NAME)).thenReturn(AGENCY_NAME);
        when(mockParsedAgency.get(AGENCY_URL)).thenReturn(AGENCY_URL);
        when(mockParsedAgency.get(AGENCY_TIMEZONE)).thenReturn(AGENCY_TIMEZONE);
        when(mockParsedAgency.get(AGENCY_LANG)).thenReturn(AGENCY_LANG);
        when(mockParsedAgency.get(AGENCY_PHONE)).thenReturn(AGENCY_PHONE);
        when(mockParsedAgency.get(AGENCY_FARE_URL)).thenReturn(AGENCY_FARE_URL);
        when(mockParsedAgency.get(AGENCY_EMAIL)).thenReturn(AGENCY_EMAIL);

        underTest.execute(mockParsedAgency);

        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_NAME));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_TIMEZONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_LANG));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_PHONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_FARE_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_EMAIL));

        verify(mockBuilder, times(1)).agencyId(AGENCY_ID);
        verify(mockBuilder, times(1)).agencyName(AGENCY_NAME);
        verify(mockBuilder, times(1)).agencyUrl(AGENCY_URL);
        verify(mockBuilder, times(1)).agencyTimezone(AGENCY_TIMEZONE);
        verify(mockBuilder, times(1)).agencyLang(AGENCY_LANG);
        verify(mockBuilder, times(1)).agencyPhone(AGENCY_PHONE);
        verify(mockBuilder, times(1)).agencyFareUrl(AGENCY_FARE_URL);
        verify(mockBuilder, times(1)).agencyEmail(AGENCY_EMAIL);
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        verify(mockResultRepo, times(1)).addNotice(isA(MissingRequiredValueNotice.class));

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        verifyNoMoreInteractions(mockParsedAgency, mockGtfsDataRepo, mockBuilder, mockResultRepo, mockGenericObject);
    }

    @Test
    void duplicateAgencyShouldAddDuplicatedEntityNoticeToResultRepoAndNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        final Agency mockAgency = mock(Agency.class);
        when(mockGenericObject.getData()).thenReturn(mockAgency);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addAgency(mockAgency)).thenReturn(null);

        final ProcessParsedAgency underTest = new ProcessParsedAgency(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedAgency.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_TIMEZONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_LANG)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_PHONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_FARE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_EMAIL)).thenReturn(STRING_TEST_VALUE);

        underTest.execute(mockParsedAgency);

        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_ID));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_NAME));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_TIMEZONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_LANG));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_PHONE));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_FARE_URL));
        verify(mockParsedAgency, times(1)).get(ArgumentMatchers.eq(AGENCY_EMAIL));
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedAgency, times(1)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addAgency(ArgumentMatchers.isA(Agency.class));

        verify(mockBuilder, times(1)).agencyId(anyString());
        verify(mockBuilder, times(1)).agencyName(anyString());
        verify(mockBuilder, times(1)).agencyUrl(anyString());
        verify(mockBuilder, times(1)).agencyTimezone(anyString());
        verify(mockBuilder, times(1)).agencyLang(anyString());
        verify(mockBuilder, times(1)).agencyPhone(anyString());
        verify(mockBuilder, times(1)).agencyFareUrl(anyString());
        verify(mockBuilder, times(1)).agencyEmail(anyString());
        verify(mockBuilder, times(1)).build();

        verify(mockGenericObject, times(1)).isSuccess();
        //noinspection ResultOfMethodCallIgnored
        verify(mockGenericObject, times(1)).getData();

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals(AGENCY_ID, noticeList.get(0).getFieldName());
        assertEquals(ENTITY_ID, noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockParsedAgency, mockResultRepo, mockGtfsDataRepo, mockAgency, mockBuilder,
                mockGenericObject);
    }
}