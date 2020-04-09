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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;
import org.mobilitydata.gtfsvalidator.usecase.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsSpecRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.List;

import static org.mockito.Mockito.*;

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
    public void validatedParsedAgencyShouldCreateAgencyEntityAndBeAddedToGtfsDataRepository() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyName(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyTimezone(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyLang(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyPhone(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyFareUrl(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.agencyEmail(anyString())).thenReturn(mockBuilder);

        Agency mockAgency = mock(Agency.class);
        when(mockBuilder.build()).thenReturn(mockAgency);

        ProcessParsedAgency underTest = new ProcessParsedAgency(mockSpecRepo, mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        when(mockParsedAgency.get(anyString())).thenReturn(STRING_TEST_VALUE);

        underTest.execute(mockParsedAgency);

        InOrder inOrder = Mockito.inOrder(mockGtfsDataRepo, mockBuilder);

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

        inOrder.verify(mockGtfsDataRepo, times(1)).addEntity(ArgumentMatchers.eq(mockAgency));

        verifyNoMoreInteractions(mockBuilder, mockAgency, mockParsedAgency, mockGtfsDataRepo);
    }

    @Test
    public void invalidAgencyNameShouldThrowExceptionAndGeneratedMissingRequiredValueNoticeShouldBeAddedToResultRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Agency.AgencyBuilder builder = new Agency.AgencyBuilder();

        ProcessParsedAgency underTest = new ProcessParsedAgency(mockSpecRepo, mockResultRepo, mockGtfsDataRepo,
                builder);

        ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        when(mockParsedAgency.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_NAME)).thenReturn(null);
        when(mockParsedAgency.get(AGENCY_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_TIMEZONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_LANG)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_PHONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_FARE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_EMAIL)).thenReturn(STRING_TEST_VALUE);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedAgency));

        Assertions.assertEquals("agency_name can not be null", exception.getMessage());

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

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assert (noticeList.get(0).getFilename().equals(FILENAME));
        assert (noticeList.get(0).getFieldName().equals(AGENCY_NAME));
        assert (noticeList.get(0).getEntityId().equals(ENTITY_ID));

        verifyNoMoreInteractions(mockParsedAgency, mockGtfsDataRepo);
    }

    @Test
    public void invalidAgencyUrlShouldThrowExceptionAndGeneratedMissingRequiredValueNoticeShouldBeAddedToResultRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Agency.AgencyBuilder builder = new Agency.AgencyBuilder();

        ProcessParsedAgency underTest = new ProcessParsedAgency(mockSpecRepo, mockResultRepo, mockGtfsDataRepo,
                builder);

        ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        when(mockParsedAgency.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_URL)).thenReturn(null);
        when(mockParsedAgency.get(AGENCY_TIMEZONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_LANG)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_PHONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_FARE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_EMAIL)).thenReturn(STRING_TEST_VALUE);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedAgency));

        Assertions.assertEquals("agency_url can not be null", exception.getMessage());

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

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assert (noticeList.get(0).getFilename().equals(FILENAME));
        assert (noticeList.get(0).getFieldName().equals(AGENCY_URL));
        assert (noticeList.get(0).getEntityId().equals(ENTITY_ID));

        verifyNoMoreInteractions(mockParsedAgency, mockGtfsDataRepo);
    }

    @Test
    public void invalidAgencyTimezoneShouldThrowExceptionAndGeneratedMissingRequiredValueNoticeShouldBeAddedToResultRepo() {

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        GtfsSpecRepository mockSpecRepo = mock(GtfsSpecRepository.class);
        GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);

        Agency.AgencyBuilder builder = new Agency.AgencyBuilder();

        ProcessParsedAgency underTest = new ProcessParsedAgency(mockSpecRepo, mockResultRepo, mockGtfsDataRepo,
                builder);

        ParsedEntity mockParsedAgency = mock(ParsedEntity.class);
        when(mockParsedAgency.get(AGENCY_ID)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_NAME)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_TIMEZONE)).thenReturn(null);
        when(mockParsedAgency.get(AGENCY_LANG)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_PHONE)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_FARE_URL)).thenReturn(STRING_TEST_VALUE);
        when(mockParsedAgency.get(AGENCY_EMAIL)).thenReturn(STRING_TEST_VALUE);

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> underTest.execute(mockParsedAgency));

        Assertions.assertEquals("agency_timezone can not be null", exception.getMessage());

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

        ArgumentCaptor<MissingRequiredValueNotice> captor = ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockResultRepo, times(1)).
                addNotice(captor.capture());

        List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assert (noticeList.get(0).getFilename().equals(FILENAME));
        assert (noticeList.get(0).getFieldName().equals(AGENCY_TIMEZONE));
        assert (noticeList.get(0).getEntityId().equals(ENTITY_ID));

        verifyNoMoreInteractions(mockParsedAgency, mockGtfsDataRepo);
    }
}