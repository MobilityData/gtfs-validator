/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ATTRIBUTION_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_AGENCY_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ROUTE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_TRIP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ORGANIZATION_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_IS_PRODUCER;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_IS_OPERATOR;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_IS_AUTHORITY;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ATTRIBUTION_URL;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ATTRIBUTION_EMAIL;
import static org.mobilitydata.gtfsvalidator.table.GtfsAttribution.DEFAULT_ATTRIBUTION_PHONE;

@RunWith(JUnit4.class)
public class GtfsAttributionTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsAttribution.Builder builder = new GtfsAttribution.Builder();
        GtfsAttribution underTest = builder
                .setAttributionId("attribution id")
                .setAgencyId("agency id")
                .setRouteId("route id")
                .setTripId("trip id")
                .setOrganizationName("organization name")
                .setAttributionEmail("hello@mobilitydata.org")
                .setAttributionUrl("https://www.github.com/MobilityData")
                .setAttributionPhone("123-456-7890")
                .setIsAuthority(1)
                .setIsOperator(1)
                .setIsProducer(0)
                .build();

        assertThat(underTest.attributionId()).matches("attribution id");
        assertThat(underTest.agencyId()).matches("agency id");
        assertThat(underTest.routeId()).matches("route id");
        assertThat(underTest.tripId()).matches("trip id");
        assertThat(underTest.organizationName()).matches("organization name");
        assertThat(underTest.attributionEmail()).matches("hello@mobilitydata.org");
        assertThat(underTest.attributionUrl()).matches("https://www.github.com/MobilityData");
        assertThat(underTest.attributionPhone()).matches("123-456-7890");
        assertThat(underTest.isAuthority()).isEqualTo(GtfsAttributionRole.ASSIGNED);
        assertThat(underTest.isOperator()).isEqualTo(GtfsAttributionRole.ASSIGNED);
        assertThat(underTest.isProducer()).isEqualTo(GtfsAttributionRole.NOT_ASSIGNED);

        assertThat(underTest.hasAttributionId()).isTrue();
        assertThat(underTest.hasAgencyId()).isTrue();
        assertThat(underTest.hasRouteId()).isTrue();
        assertThat(underTest.hasTripId()).isTrue();
        assertThat(underTest.hasOrganizationName()).isTrue();
        assertThat(underTest.hasAttributionEmail()).isTrue();
        assertThat(underTest.hasAttributionUrl()).isTrue();
        assertThat(underTest.hasAttributionPhone()).isTrue();
        assertThat(underTest.hasIsAuthority()).isTrue();
        assertThat(underTest.hasIsOperator()).isTrue();
        assertThat(underTest.hasIsProducer()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsAttribution.Builder builder = new GtfsAttribution.Builder();
        GtfsAttribution underTest = builder
                .setAttributionId(null)
                .setAgencyId(null)
                .setRouteId(null)
                .setTripId(null)
                .setOrganizationName(null)
                .setAttributionEmail(null)
                .setAttributionUrl(null)
                .setAttributionPhone(null)
                .setIsAuthority(null)
                .setIsOperator(null)
                .setIsProducer(null)
                .build();

        assertThat(underTest.attributionId()).matches(DEFAULT_ATTRIBUTION_ID);
        assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);
        assertThat(underTest.routeId()).matches(DEFAULT_ROUTE_ID);
        assertThat(underTest.tripId()).matches(DEFAULT_TRIP_ID);
        assertThat(underTest.organizationName()).matches(DEFAULT_ORGANIZATION_NAME);
        assertThat(underTest.attributionEmail()).matches(DEFAULT_ATTRIBUTION_EMAIL);
        assertThat(underTest.attributionUrl()).matches(DEFAULT_ATTRIBUTION_URL);
        assertThat(underTest.attributionPhone()).matches(DEFAULT_ATTRIBUTION_PHONE);
        assertThat(underTest.isAuthority()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_AUTHORITY));
        assertThat(underTest.isOperator()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_OPERATOR));
        assertThat(underTest.isProducer()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_PRODUCER));

        assertThat(underTest.hasAttributionId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasOrganizationName()).isFalse();
        assertThat(underTest.hasAttributionEmail()).isFalse();
        assertThat(underTest.hasAttributionUrl()).isFalse();
        assertThat(underTest.hasAttributionPhone()).isFalse();
        assertThat(underTest.hasIsAuthority()).isFalse();
        assertThat(underTest.hasIsOperator()).isFalse();
        assertThat(underTest.hasIsProducer()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsAttribution.Builder builder = new GtfsAttribution.Builder();
        builder.setAttributionId("attribution id")
                .setAgencyId("agency id")
                .setRouteId("route id")
                .setTripId("trip id")
                .setOrganizationName("organization name")
                .setAttributionEmail("hello@mobilitydata.org")
                .setAttributionUrl("https://www.github.com/MobilityData")
                .setAttributionPhone("123-456-7890")
                .setIsAuthority(1)
                .setIsOperator(1)
                .setIsProducer(0);

        builder.clear();
        GtfsAttribution underTest = builder.build();

        assertThat(underTest.attributionId()).matches(DEFAULT_ATTRIBUTION_ID);
        assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);
        assertThat(underTest.routeId()).matches(DEFAULT_ROUTE_ID);
        assertThat(underTest.tripId()).matches(DEFAULT_TRIP_ID);
        assertThat(underTest.organizationName()).matches(DEFAULT_ORGANIZATION_NAME);
        assertThat(underTest.attributionEmail()).matches(DEFAULT_ATTRIBUTION_EMAIL);
        assertThat(underTest.attributionUrl()).matches(DEFAULT_ATTRIBUTION_URL);
        assertThat(underTest.attributionPhone()).matches(DEFAULT_ATTRIBUTION_PHONE);
        assertThat(underTest.isAuthority()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_AUTHORITY));
        assertThat(underTest.isOperator()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_OPERATOR));
        assertThat(underTest.isProducer()).isEqualTo(GtfsAttributionRole.forNumber(DEFAULT_IS_PRODUCER));

        assertThat(underTest.hasAttributionId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasOrganizationName()).isFalse();
        assertThat(underTest.hasAttributionEmail()).isFalse();
        assertThat(underTest.hasAttributionUrl()).isFalse();
        assertThat(underTest.hasAttributionPhone()).isFalse();
        assertThat(underTest.hasIsAuthority()).isFalse();
        assertThat(underTest.hasIsOperator()).isFalse();
        assertThat(underTest.hasIsProducer()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsAttribution.Builder builder = new GtfsAttribution.Builder();
        GtfsAttribution underTest = builder.build();

        assertThat(underTest.attributionId()).isNull();
        assertThat(underTest.agencyId()).isNull();
        assertThat(underTest.routeId()).isNull();
        assertThat(underTest.tripId()).isNull();
        assertThat(underTest.organizationName()).isNull();
        assertThat(underTest.attributionEmail()).isNull();
        assertThat(underTest.attributionUrl()).isNull();
        assertThat(underTest.attributionPhone()).isNull();
        assertThat(underTest.isAuthority()).isEqualTo(GtfsAttributionRole.NOT_ASSIGNED);
        assertThat(underTest.isOperator()).isEqualTo(GtfsAttributionRole.NOT_ASSIGNED);
        assertThat(underTest.isProducer()).isEqualTo(GtfsAttributionRole.NOT_ASSIGNED);

        assertThat(underTest.hasAttributionId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasOrganizationName()).isFalse();
        assertThat(underTest.hasAttributionEmail()).isFalse();
        assertThat(underTest.hasAttributionUrl()).isFalse();
        assertThat(underTest.hasAttributionPhone()).isFalse();
        assertThat(underTest.hasIsAuthority()).isFalse();
        assertThat(underTest.hasIsOperator()).isFalse();
        assertThat(underTest.hasIsProducer()).isFalse();
    }
}
