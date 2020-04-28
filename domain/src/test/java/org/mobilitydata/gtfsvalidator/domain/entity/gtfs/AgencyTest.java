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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AgencyTest {
    private static final String STRING_TEST_VALUE = "test_value";

    // Field agencyName is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithNullAgencyNameShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(null)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("agency.txt", noticeList.get(0).getFilename());
        assertEquals("agency_name", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST_VALUE, noticeList.get(0).getEntityId());
    }

    // Field agencyUrl is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithNullAgencyUrlShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(null)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("agency.txt", noticeList.get(0).getFilename());
        assertEquals("agency_url", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST_VALUE, noticeList.get(0).getEntityId());
    }

    // Field agencyTimezone is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createAgencyWithTimezoneAgencyUrlShouldGenerateMissingRequiredValueNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(null)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("agency.txt", noticeList.get(0).getFilename());
        assertEquals("agency_timezone", noticeList.get(0).getFieldName());
        assertEquals(STRING_TEST_VALUE, noticeList.get(0).getEntityId());
    }

    @Test
    public void createAgencyWithValidValuesForFieldShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Agency.AgencyBuilder underTest = new Agency.AgencyBuilder(mockNoticeCollection);

        underTest.agencyId(STRING_TEST_VALUE);
        underTest.agencyName(STRING_TEST_VALUE);
        underTest.agencyUrl(STRING_TEST_VALUE);
        underTest.agencyTimezone(STRING_TEST_VALUE);
        underTest.agencyLang(STRING_TEST_VALUE);
        underTest.agencyPhone(STRING_TEST_VALUE);
        underTest.agencyFareUrl(STRING_TEST_VALUE);
        underTest.agencyEmail(STRING_TEST_VALUE);

        final Agency agency = underTest.build();

        assertEquals(agency.getAgencyId(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyName(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyUrl(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyTimezone(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyLang(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyPhone(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyFareUrl(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyEmail(), STRING_TEST_VALUE);
        assertEquals(0, underTest.getNoticeCollection().size());
    }
}