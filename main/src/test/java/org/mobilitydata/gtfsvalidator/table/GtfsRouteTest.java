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
import org.mobilitydata.gtfsvalidator.type.GtfsColor;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_AGENCY_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_SHORT_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_LONG_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_DESC;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_TYPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_URL;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_COLOR;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_TEXT_COLOR;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_ROUTE_SORT_ORDER;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_CONTINUOUS_PICKUP;
import static org.mobilitydata.gtfsvalidator.table.GtfsRoute.DEFAULT_CONTINUOUS_DROP_OFF;

@RunWith(JUnit4.class)
public class GtfsRouteTest {
    @Test
    public void shouldReturnFieldValues() {
        Builder builder = new Builder();
        GtfsColor color = GtfsColor.fromString("FFFFFF");
        GtfsRoute underTest = builder
                .setRouteId("route id")
                .setAgencyId("agency id")
                .setRouteShortName("route short name")
                .setRouteLongName("route long name")
                .setRouteDesc("route desc")
                .setRouteType(1)
                .setRouteUrl("https://www.github.com/MobilityData")
                .setRouteColor(color)
                .setRouteTextColor(color)
                .setRouteSortOrder(1)
                .setContinuousPickup(0)
                .setContinuousDropOff(0)
                .build();

        assertThat(underTest.routeId()).isEqualTo("route id");
        assertThat(underTest.agencyId()).isEqualTo("agency id");
        assertThat(underTest.routeShortName()).isEqualTo("route short name");
        assertThat(underTest.routeLongName()).isEqualTo("route long name");
        assertThat(underTest.routeDesc()).isEqualTo("route desc");
        assertThat(underTest.routeType()).isEqualTo(GtfsRouteType.SUBWAY);
        assertThat(underTest.routeUrl()).isEqualTo("https://www.github.com/MobilityData");
        assertThat(underTest.routeColor()).isEqualTo(color);
        assertThat(underTest.routeTextColor()).isEqualTo(color);
        assertThat(underTest.routeSortOrder()).isEqualTo(1);
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff.ALLOWED);
        assertThat(underTest.continuousDropOff()).isEqualTo(GtfsContinuousPickupDropOff.ALLOWED);

        assertThat(underTest.hasRouteId()).isTrue();
        assertThat(underTest.hasAgencyId()).isTrue();
        assertThat(underTest.hasRouteShortName()).isTrue();
        assertThat(underTest.hasRouteLongName()).isTrue();
        assertThat(underTest.hasRouteDesc()).isTrue();
        assertThat(underTest.hasRouteType()).isTrue();
        assertThat(underTest.hasRouteUrl()).isTrue();
        assertThat(underTest.hasRouteColor()).isTrue();
        assertThat(underTest.hasRouteTextColor()).isTrue();
        assertThat(underTest.hasRouteSortOrder()).isTrue();
        assertThat(underTest.hasContinuousPickup()).isTrue();
        assertThat(underTest.hasContinuousDropOff()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        Builder builder = new Builder();
        GtfsRoute underTest = builder
                .setRouteId(null)
                .setAgencyId(null)
                .setRouteShortName(null)
                .setRouteLongName(null)
                .setRouteDesc(null)
                .setRouteType(null)
                .setRouteUrl(null)
                .setRouteColor(null)
                .setRouteTextColor(null)
                .setRouteSortOrder(null)
                .setContinuousPickup(null)
                .setContinuousDropOff(null)
                .build();

        assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
        assertThat(underTest.agencyId()).isEqualTo(DEFAULT_AGENCY_ID);
        assertThat(underTest.routeShortName()).isEqualTo(DEFAULT_ROUTE_SHORT_NAME);
        assertThat(underTest.routeLongName()).isEqualTo(DEFAULT_ROUTE_LONG_NAME);
        assertThat(underTest.routeDesc()).isEqualTo(DEFAULT_ROUTE_DESC);
        // route_type in an optional fields with a default value.
        assertThat(underTest.routeType()).isEqualTo(GtfsRouteType.forNumber(DEFAULT_ROUTE_TYPE));
        assertThat(underTest.routeUrl()).isEqualTo(DEFAULT_ROUTE_URL);
        assertThat(underTest.routeColor()).isEqualTo(DEFAULT_ROUTE_COLOR);
        assertThat(underTest.routeTextColor()).isEqualTo(DEFAULT_ROUTE_TEXT_COLOR);
        assertThat(underTest.routeSortOrder()).isEqualTo(DEFAULT_ROUTE_SORT_ORDER);
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_DROP_OFF));

        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteShortName()).isFalse();
        assertThat(underTest.hasRouteLongName()).isFalse();
        assertThat(underTest.hasRouteDesc()).isFalse();
        assertThat(underTest.hasRouteType()).isFalse();
        assertThat(underTest.hasRouteUrl()).isFalse();
        assertThat(underTest.hasRouteColor()).isFalse();
        assertThat(underTest.hasRouteTextColor()).isFalse();
        assertThat(underTest.hasRouteSortOrder()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        Builder builder = new Builder();
        GtfsColor color = GtfsColor.fromString("FFFFFF");
        builder.setRouteId("route id")
                .setAgencyId("agency id")
                .setRouteShortName("route short name")
                .setRouteLongName("route long name")
                .setRouteDesc("route desc")
                .setRouteType(1)
                .setRouteUrl("https://www.github.com/MobilityData")
                .setRouteColor(color)
                .setRouteTextColor(color)
                .setRouteSortOrder(1)
                .setContinuousPickup(0)
                .setContinuousDropOff(0);
        builder.clear();
        GtfsRoute underTest = builder.build();

        assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
        assertThat(underTest.agencyId()).isEqualTo(DEFAULT_AGENCY_ID);
        assertThat(underTest.routeShortName()).isEqualTo(DEFAULT_ROUTE_SHORT_NAME);
        assertThat(underTest.routeLongName()).isEqualTo(DEFAULT_ROUTE_LONG_NAME);
        assertThat(underTest.routeDesc()).isEqualTo(DEFAULT_ROUTE_DESC);
        // route_type in an optional fields with a default value.
        assertThat(underTest.routeType()).isEqualTo(GtfsRouteType.forNumber(DEFAULT_ROUTE_TYPE));
        assertThat(underTest.routeUrl()).isEqualTo(DEFAULT_ROUTE_URL);
        assertThat(underTest.routeColor()).isEqualTo(DEFAULT_ROUTE_COLOR);
        assertThat(underTest.routeTextColor()).isEqualTo(DEFAULT_ROUTE_TEXT_COLOR);
        assertThat(underTest.routeSortOrder()).isEqualTo(DEFAULT_ROUTE_SORT_ORDER);
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_DROP_OFF));

        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteShortName()).isFalse();
        assertThat(underTest.hasRouteLongName()).isFalse();
        assertThat(underTest.hasRouteDesc()).isFalse();
        assertThat(underTest.hasRouteType()).isFalse();
        assertThat(underTest.hasRouteUrl()).isFalse();
        assertThat(underTest.hasRouteColor()).isFalse();
        assertThat(underTest.hasRouteTextColor()).isFalse();
        assertThat(underTest.hasRouteSortOrder()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        Builder builder = new Builder();
        GtfsRoute underTest = builder.build();

        assertThat(underTest.routeId()).isNull();
        assertThat(underTest.agencyId()).isNull();
        assertThat(underTest.routeShortName()).isNull();
        assertThat(underTest.routeLongName()).isNull();
        assertThat(underTest.routeDesc()).isNull();
        // route_type in an optional fields with a default value.
        assertThat(underTest.routeType()).isEqualTo(GtfsRouteType.forNumber(DEFAULT_ROUTE_TYPE));
        assertThat(underTest.routeUrl()).isNull();
        assertThat(underTest.routeColor()).isNull();
        assertThat(underTest.routeTextColor()).isNull();
        assertThat(underTest.routeSortOrder()).isEqualTo(DEFAULT_ROUTE_SORT_ORDER);
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_DROP_OFF));

        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
        assertThat(underTest.hasRouteShortName()).isFalse();
        assertThat(underTest.hasRouteLongName()).isFalse();
        assertThat(underTest.hasRouteDesc()).isFalse();
        assertThat(underTest.hasRouteType()).isFalse();
        assertThat(underTest.hasRouteUrl()).isFalse();
        assertThat(underTest.hasRouteColor()).isFalse();
        assertThat(underTest.hasRouteTextColor()).isFalse();
        assertThat(underTest.hasRouteSortOrder()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
    }
}
