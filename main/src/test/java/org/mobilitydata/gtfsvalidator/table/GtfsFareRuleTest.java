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
import static org.mobilitydata.gtfsvalidator.table.GtfsFareRule.DEFAULT_CONTAINS_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareRule.DEFAULT_ORIGIN_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareRule.DEFAULT_DESTINATION_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareRule.DEFAULT_ROUTE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareRule.DEFAULT_FARE_ID;

@RunWith(JUnit4.class)
public class GtfsFareRuleTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsFareRule.Builder builder = new GtfsFareRule.Builder();
        GtfsFareRule underTest = builder
                .setFareId("fare id")
                .setRouteId("route id")
                .setOriginId("origin id")
                .setDestinationId("destination id")
                .setContainsId("contains id")
                .build();

        assertThat(underTest.fareId()).matches("fare id");
        assertThat(underTest.routeId()).isEqualTo("route id");
        assertThat(underTest.originId()).isEqualTo("origin id");
        assertThat(underTest.destinationId()).isEqualTo("destination id");
        assertThat(underTest.containsId()).isEqualTo("contains id");

        assertThat(underTest.hasFareId()).isTrue();
        assertThat(underTest.hasRouteId()).isTrue();
        assertThat(underTest.hasOriginId()).isTrue();
        assertThat(underTest.hasDestinationId()).isTrue();
        assertThat(underTest.hasContainsId()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsFareRule.Builder builder = new GtfsFareRule.Builder();
        GtfsFareRule underTest = builder
                .setFareId(null)
                .setRouteId(null)
                .setOriginId(null)
                .setDestinationId(null)
                .setContainsId(null)
                .build();

        assertThat(underTest.fareId()).matches(DEFAULT_FARE_ID);
        assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
        assertThat(underTest.originId()).isEqualTo(DEFAULT_ORIGIN_ID);
        assertThat(underTest.destinationId()).isEqualTo(DEFAULT_DESTINATION_ID);
        assertThat(underTest.containsId()).isEqualTo(DEFAULT_CONTAINS_ID);

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasOriginId()).isFalse();
        assertThat(underTest.hasDestinationId()).isFalse();
        assertThat(underTest.hasContainsId()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsFareRule.Builder builder = new GtfsFareRule.Builder();
        builder.setFareId("fare id")
                .setRouteId("route id ")
                .setOriginId("origin id")
                .setDestinationId("destination id")
                .setContainsId("contains id");

        builder.clear();
        GtfsFareRule underTest = builder.build();

        assertThat(underTest.fareId()).matches(DEFAULT_FARE_ID);
        assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
        assertThat(underTest.originId()).isEqualTo(DEFAULT_ORIGIN_ID);
        assertThat(underTest.destinationId()).isEqualTo(DEFAULT_DESTINATION_ID);
        assertThat(underTest.containsId()).isEqualTo(DEFAULT_CONTAINS_ID);

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasOriginId()).isFalse();
        assertThat(underTest.hasDestinationId()).isFalse();
        assertThat(underTest.hasContainsId()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsFareRule.Builder builder = new GtfsFareRule.Builder();
        GtfsFareRule underTest = builder.build();

        assertThat(underTest.fareId()).isNull();
        assertThat(underTest.routeId()).isNull();
        assertThat(underTest.originId()).isNull();
        assertThat(underTest.destinationId()).isNull();
        assertThat(underTest.containsId()).isNull();

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasRouteId()).isFalse();
        assertThat(underTest.hasOriginId()).isFalse();
        assertThat(underTest.hasDestinationId()).isFalse();
        assertThat(underTest.hasContainsId()).isFalse();
    }
}
