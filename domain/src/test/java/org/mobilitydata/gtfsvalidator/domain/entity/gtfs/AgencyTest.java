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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AgencyTest {
    private static final String STRING_TEST_VALUE = "test_value";

    @Test
    public void createAgencyWithNullAgencyNameShouldThrowException() {
        Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(null)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("agency_name can not be null", exception.getMessage());
    }

    @Test
    public void createAgencyWithNullAgencyUrlShouldThrowException() {
        Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(null)
                .agencyTimezone(STRING_TEST_VALUE)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("agency_url can not be null", exception.getMessage());
    }

    @Test
    public void createAgencyWithTimezoneAgencyUrlShouldThrowException() {
        Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        //noinspection ConstantConditions
        underTest.agencyId(STRING_TEST_VALUE)
                .agencyName(STRING_TEST_VALUE)
                .agencyUrl(STRING_TEST_VALUE)
                .agencyTimezone(null)
                .agencyLang(STRING_TEST_VALUE)
                .agencyPhone(STRING_TEST_VALUE)
                .agencyFareUrl(STRING_TEST_VALUE)
                .agencyEmail(STRING_TEST_VALUE);

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("agency_timezone can not be null", exception.getMessage());
    }

    @Test
    public void createAgencyWithValidValuesForFieldShouldNotThrowException() {
        Agency.AgencyBuilder underTest = new Agency.AgencyBuilder();

        underTest.agencyId(STRING_TEST_VALUE);
        underTest.agencyName(STRING_TEST_VALUE);
        underTest.agencyUrl(STRING_TEST_VALUE);
        underTest.agencyTimezone(STRING_TEST_VALUE);
        underTest.agencyLang(STRING_TEST_VALUE);
        underTest.agencyPhone(STRING_TEST_VALUE);
        underTest.agencyFareUrl(STRING_TEST_VALUE);
        underTest.agencyEmail(STRING_TEST_VALUE);

        Agency agency = underTest.build();

        assertEquals(agency.getAgencyId(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyName(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyUrl(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyTimezone(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyLang(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyPhone(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyFareUrl(), STRING_TEST_VALUE);
        assertEquals(agency.getAgencyEmail(), STRING_TEST_VALUE);
    }
}