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

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_BIKES_ALLOWED;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_BLOCK_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_DIRECTION_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_ROUTE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_SERVICE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_SHAPE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_TRIP_HEADSIGN;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_TRIP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_TRIP_SHORT_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTrip.DEFAULT_WHEELCHAIR_ACCESSIBLE;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsTripTest {
  @Test
  public void shouldReturnFieldValues() {
    Builder builder = new Builder();
    GtfsTrip underTest =
        builder
            .setTripId("trip id")
            .setRouteId("route id")
            .setServiceId("service id")
            .setTripHeadsign("trip headsign")
            .setTripShortName("tip shortname")
            .setDirectionId(1)
            .setBlockId("block id")
            .setShapeId("shape id")
            .setWheelchairAccessible(1)
            .setBikesAllowed(1)
            .build();

    assertThat(underTest.tripId()).isEqualTo("trip id");
    assertThat(underTest.routeId()).isEqualTo("route id");
    assertThat(underTest.serviceId()).isEqualTo("service id");
    assertThat(underTest.tripHeadsign()).isEqualTo("trip headsign");
    assertThat(underTest.tripShortName()).isEqualTo("tip shortname");
    assertThat(underTest.directionId()).isEqualTo(GtfsTripDirectionId.INBOUND);
    assertThat(underTest.blockId()).isEqualTo("block id");
    assertThat(underTest.shapeId()).isEqualTo("shape id");
    assertThat(underTest.wheelchairAccessible()).isEqualTo(GtfsWheelchairBoarding.ACCESSIBLE);
    assertThat(underTest.bikesAllowed()).isEqualTo(GtfsBikesAllowed.ALLOWED);

    assertThat(underTest.hasTripId()).isTrue();
    assertThat(underTest.hasRouteId()).isTrue();
    assertThat(underTest.hasServiceId()).isTrue();
    assertThat(underTest.hasTripHeadsign()).isTrue();
    assertThat(underTest.hasTripShortName()).isTrue();
    assertThat(underTest.hasDirectionId()).isTrue();
    assertThat(underTest.hasBlockId()).isTrue();
    assertThat(underTest.hasShapeId()).isTrue();
    assertThat(underTest.hasWheelchairAccessible()).isTrue();
    assertThat(underTest.hasBikesAllowed()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    Builder builder = new Builder();
    GtfsTrip underTest =
        builder
            .setTripId(null)
            .setRouteId(null)
            .setServiceId(null)
            .setTripHeadsign(null)
            .setTripShortName(null)
            .setDirectionId(null)
            .setBlockId(null)
            .setShapeId(null)
            .setWheelchairAccessible(null)
            .setBikesAllowed(null)
            .build();

    assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
    assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
    assertThat(underTest.serviceId()).isEqualTo(DEFAULT_SERVICE_ID);
    assertThat(underTest.tripHeadsign()).isEqualTo(DEFAULT_TRIP_HEADSIGN);
    assertThat(underTest.tripShortName()).isEqualTo(DEFAULT_TRIP_SHORT_NAME);
    assertThat(underTest.directionId())
        .isEqualTo(GtfsTripDirectionId.forNumber(DEFAULT_DIRECTION_ID));
    assertThat(underTest.blockId()).isEqualTo(DEFAULT_BLOCK_ID);
    assertThat(underTest.shapeId()).isEqualTo(DEFAULT_SHAPE_ID);
    assertThat(underTest.wheelchairAccessible())
        .isEqualTo(GtfsWheelchairBoarding.forNumber(DEFAULT_WHEELCHAIR_ACCESSIBLE));
    assertThat(underTest.bikesAllowed())
        .isEqualTo(GtfsBikesAllowed.forNumber(DEFAULT_BIKES_ALLOWED));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasRouteId()).isFalse();
    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasTripHeadsign()).isFalse();
    assertThat(underTest.hasTripShortName()).isFalse();
    assertThat(underTest.hasDirectionId()).isFalse();
    assertThat(underTest.hasBlockId()).isFalse();
    assertThat(underTest.hasShapeId()).isFalse();
    assertThat(underTest.hasWheelchairAccessible()).isFalse();
    assertThat(underTest.hasBikesAllowed()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    Builder builder = new Builder();
    builder
        .setTripId("trip id")
        .setRouteId("route id")
        .setServiceId("service id")
        .setTripHeadsign("trip headsign")
        .setTripShortName("tip shortname")
        .setDirectionId(1)
        .setBlockId("block id")
        .setShapeId("shape id")
        .setWheelchairAccessible(1)
        .setBikesAllowed(1);
    builder.clear();

    GtfsTrip underTest = builder.build();

    assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
    assertThat(underTest.routeId()).isEqualTo(DEFAULT_ROUTE_ID);
    assertThat(underTest.serviceId()).isEqualTo(DEFAULT_SERVICE_ID);
    assertThat(underTest.tripHeadsign()).isEqualTo(DEFAULT_TRIP_HEADSIGN);
    assertThat(underTest.tripShortName()).isEqualTo(DEFAULT_TRIP_SHORT_NAME);
    assertThat(underTest.directionId())
        .isEqualTo(GtfsTripDirectionId.forNumber(DEFAULT_DIRECTION_ID));
    assertThat(underTest.blockId()).isEqualTo(DEFAULT_BLOCK_ID);
    assertThat(underTest.shapeId()).isEqualTo(DEFAULT_SHAPE_ID);
    assertThat(underTest.wheelchairAccessible())
        .isEqualTo(GtfsWheelchairBoarding.forNumber(DEFAULT_WHEELCHAIR_ACCESSIBLE));
    assertThat(underTest.bikesAllowed())
        .isEqualTo(GtfsBikesAllowed.forNumber(DEFAULT_BIKES_ALLOWED));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasRouteId()).isFalse();
    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasTripHeadsign()).isFalse();
    assertThat(underTest.hasTripShortName()).isFalse();
    assertThat(underTest.hasDirectionId()).isFalse();
    assertThat(underTest.hasBlockId()).isFalse();
    assertThat(underTest.hasShapeId()).isFalse();
    assertThat(underTest.hasWheelchairAccessible()).isFalse();
    assertThat(underTest.hasBikesAllowed()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    Builder builder = new Builder();
    GtfsTrip underTest = builder.build();

    assertThat(underTest.tripId()).isNull();
    assertThat(underTest.routeId()).isNull();
    assertThat(underTest.serviceId()).isNull();
    assertThat(underTest.tripHeadsign()).isNull();
    assertThat(underTest.tripShortName()).isNull();
    assertThat(underTest.directionId())
        .isEqualTo(GtfsTripDirectionId.forNumber(DEFAULT_DIRECTION_ID));
    assertThat(underTest.blockId()).isNull();
    assertThat(underTest.shapeId()).isNull();
    assertThat(underTest.wheelchairAccessible())
        .isEqualTo(GtfsWheelchairBoarding.forNumber(DEFAULT_WHEELCHAIR_ACCESSIBLE));
    assertThat(underTest.bikesAllowed())
        .isEqualTo(GtfsBikesAllowed.forNumber(DEFAULT_BIKES_ALLOWED));

    assertThat(underTest.hasTripId()).isFalse();
    assertThat(underTest.hasRouteId()).isFalse();
    assertThat(underTest.hasServiceId()).isFalse();
    assertThat(underTest.hasTripHeadsign()).isFalse();
    assertThat(underTest.hasTripShortName()).isFalse();
    assertThat(underTest.hasDirectionId()).isFalse();
    assertThat(underTest.hasBlockId()).isFalse();
    assertThat(underTest.hasShapeId()).isFalse();
    assertThat(underTest.hasWheelchairAccessible()).isFalse();
    assertThat(underTest.hasBikesAllowed()).isFalse();
  }
}
