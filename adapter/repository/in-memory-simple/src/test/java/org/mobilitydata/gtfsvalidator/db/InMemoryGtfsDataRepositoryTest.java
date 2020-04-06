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

package org.mobilitydata.gtfsvalidator.db;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Agency;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class InMemoryGtfsDataRepositoryTest {

    private final String STRING_TEST_VALUE = "test_value";

    @Test
    void getAgencyCollectionShouldReturnRouteCollection() {

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_0")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Agency agency00 = mockBuilder.build();
        underTest.addEntity(agency00);

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_1")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final Agency agency01 = mockBuilder.build();
        underTest.addEntity(agency01);

        final List<Agency> toVerify = underTest.getAgencyCollection();

        assertEquals("test_name_0", toVerify.get(1).getAgencyName());
        assertEquals("test_name_1", toVerify.get(0).getAgencyName());
    }

    @Test
    void callToAddEntityShouldAddAgencyToRepoAndReturnSameEntity() {

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_0")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        final Agency agency00 = mockBuilder.build();
        Agency toCheck = underTest.addEntity(agency00);

        assertEquals(1, underTest.getAgencyCollection().size());
        assertEquals(toCheck, agency00);

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_1")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final Agency agency01 = mockBuilder.build();
        toCheck = underTest.addEntity(agency01);

        assertEquals(2, underTest.getAgencyCollection().size());
        assertEquals(toCheck, agency01);
    }

    @Test
    void getAgencyByIdShouldReturnRelatedAgency() {

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);
        when(mockBuilder.agencyId(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyName(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyTimezone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyLang(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyPhone(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyFareUrl(anyString())).thenCallRealMethod();
        when(mockBuilder.agencyEmail(anyString())).thenCallRealMethod();
        when(mockBuilder.build()).thenCallRealMethod();

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_0")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        final InMemoryGtfsDataRepository underTest = new InMemoryGtfsDataRepository();

        underTest.addEntity(mockBuilder.build());

        mockBuilder.agencyId(STRING_TEST_VALUE)
                .agencyName("test_name_1")
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        underTest.addEntity(mockBuilder.build());

        assertEquals("test_name_0", underTest.getAgencyByName("test_name_0").getAgencyName());
        assertEquals("test_name_1", underTest.getAgencyByName("test_name_1").getAgencyName());
    }
}