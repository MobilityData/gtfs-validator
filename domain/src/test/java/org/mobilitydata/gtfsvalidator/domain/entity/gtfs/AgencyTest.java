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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgencyTest {

    private static final String VALUE = "test_value";

    @Test
    public void createAgencyWithNullValueForRequiredFieldShouldThrowException() {

        Agency.AgencyBuilder mockBuilder = mock(Agency.AgencyBuilder.class);

        when(mockBuilder.build()).thenCallRealMethod();

        Assertions.assertThrows(NullPointerException.class, mockBuilder::build);
    }

    @Test
    public void createAgencyWithValidValuesForFieldShouldNotThrowException() {

        Agency.AgencyBuilder builder = new Agency.AgencyBuilder();

        builder.agencyId(VALUE);
        builder.agencyName(VALUE);
        builder.agencyUrl(VALUE);
        builder.agencyTimezone(VALUE);
        builder.agencyLang(VALUE);
        builder.agencyPhone(VALUE);
        builder.agencyFareUrl(VALUE);
        builder.agencyEmail(VALUE);

        Agency agency = builder.build();

        assertEquals(agency.getAgencyId(), VALUE);
        assertEquals(agency.getAgencyName(), VALUE);
        assertEquals(agency.getAgencyUrl(), VALUE);
        assertEquals(agency.getAgencyTimezone(), VALUE);
        assertEquals(agency.getAgencyLang(), VALUE);
        assertEquals(agency.getAgencyPhone(), VALUE);
        assertEquals(agency.getAgencyFareUrl(), VALUE);
        assertEquals(agency.getAgencyEmail(), VALUE);
    }
}