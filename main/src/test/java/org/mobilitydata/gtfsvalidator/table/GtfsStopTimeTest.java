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
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.Builder;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_TRIP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_ARRIVAL_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_DEPARTURE_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_STOP_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_STOP_SEQUENCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_STOP_HEADSIGN;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_PICKUP_TYPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_DROP_OFF_TYPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_CONTINUOUS_PICKUP;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_CONTINUOUS_DROP_OFF;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_SHAPE_DIST_TRAVELED;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTime.DEFAULT_TIMEPOINT;

@RunWith(JUnit4.class)
public class GtfsStopTimeTest {
    @Test
    public void shouldReturnFieldValues() {
        Builder builder = new Builder();
        GtfsTime arrivalTime = GtfsTime.fromSecondsSinceMidnight(3425656);
        GtfsTime departureTime = GtfsTime.fromSecondsSinceMidnight(3425660);
        GtfsStopTime underTest = builder
                .setTripId("trip id")
                .setArrivalTime(arrivalTime)
                .setDepartureTime(departureTime)
                .setStopId("stop id")
                .setStopSequence(2)
                .setStopHeadsign("stop headsign")
                .setPickupType(0)
                .setDropOffType(0)
                .setContinuousPickup(0)
                .setContinuousDropOff(0)
                .setShapeDistTraveled(20d)
                .setTimepoint(1)
                .build();

        assertThat(underTest.tripId()).isEqualTo("trip id");
        assertThat(underTest.arrivalTime()).isEqualTo(arrivalTime);
        assertThat(underTest.departureTime()).isEqualTo(departureTime);
        assertThat(underTest.stopId()).isEqualTo("stop id");
        assertThat(underTest.stopSequence()).isEqualTo(2);
        assertThat(underTest.stopHeadsign()).isEqualTo("stop headsign");
        assertThat(underTest.pickupType()).isEqualTo(GtfsPickupDropOff.ALLOWED);
        assertThat(underTest.dropOffType()).isEqualTo(GtfsPickupDropOff.ALLOWED);
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff.ALLOWED);
        assertThat(underTest.continuousDropOff()).isEqualTo(GtfsContinuousPickupDropOff.ALLOWED);
        assertThat(underTest.shapeDistTraveled()).isEqualTo(20d);
        assertThat(underTest.timepoint()).isEqualTo(GtfsStopTimesTimepoint.EXACT);

        assertThat(underTest.hasTripId()).isTrue();
        assertThat(underTest.hasArrivalTime()).isTrue();
        assertThat(underTest.hasDepartureTime()).isTrue();
        assertThat(underTest.hasStopId()).isTrue();
        assertThat(underTest.hasStopSequence()).isTrue();
        assertThat(underTest.hasStopHeadsign()).isTrue();
        assertThat(underTest.hasPickupType()).isTrue();
        assertThat(underTest.hasDropOffType()).isTrue();
        assertThat(underTest.hasContinuousPickup()).isTrue();
        assertThat(underTest.hasContinuousDropOff()).isTrue();
        assertThat(underTest.hasShapeDistTraveled()).isTrue();
        assertThat(underTest.hasTimepoint()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        Builder builder = new Builder();
        GtfsStopTime underTest = builder
                .setTripId(null)
                .setArrivalTime(null)
                .setDepartureTime(null)
                .setStopId(null)
                .setStopSequence(null)
                .setStopHeadsign(null)
                .setPickupType(null)
                .setDropOffType(null)
                .setContinuousPickup(null)
                .setContinuousDropOff(null)
                .setShapeDistTraveled(null)
                .setTimepoint(null)
                .build();

        assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
        assertThat(underTest.arrivalTime()).isEqualTo(DEFAULT_ARRIVAL_TIME);
        assertThat(underTest.departureTime()).isEqualTo(DEFAULT_DEPARTURE_TIME);
        assertThat(underTest.stopId()).isEqualTo(DEFAULT_STOP_ID);
        assertThat(underTest.stopSequence()).isEqualTo(DEFAULT_STOP_SEQUENCE);
        assertThat(underTest.stopHeadsign()).isEqualTo(DEFAULT_STOP_HEADSIGN);
        // drop_off_type and pickup_type are optional fields with a default value.
        assertThat(underTest.pickupType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_PICKUP_TYPE));
        assertThat(underTest.dropOffType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_DROP_OFF_TYPE));
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup())
                .isEqualTo(GtfsContinuousPickupDropOff.forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousDropOff())
                .isEqualTo(GtfsContinuousPickupDropOff.forNumber(DEFAULT_CONTINUOUS_DROP_OFF));
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);
        // timepoint is an optional field with a default value.
        assertThat(underTest.timepoint()).isEqualTo(GtfsStopTimesTimepoint.forNumber(DEFAULT_TIMEPOINT));

        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasArrivalTime()).isFalse();
        assertThat(underTest.hasDepartureTime()).isFalse();
        assertThat(underTest.hasStopId()).isFalse();
        assertThat(underTest.hasStopSequence()).isFalse();
        assertThat(underTest.hasStopHeadsign()).isFalse();
        assertThat(underTest.hasPickupType()).isFalse();
        assertThat(underTest.hasDropOffType()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
        assertThat(underTest.hasTimepoint()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        Builder builder = new Builder();
        GtfsTime arrivalTime = GtfsTime.fromSecondsSinceMidnight(3425656);
        GtfsTime departureTime = GtfsTime.fromSecondsSinceMidnight(3425660);
        builder.setStopId("stop id")
                .setTripId("trip id")
                .setArrivalTime(arrivalTime)
                .setDepartureTime(departureTime)
                .setStopId("stop id")
                .setStopSequence(2)
                .setStopHeadsign("stop headsign")
                .setPickupType(2)
                .setDropOffType(0)
                .setContinuousPickup(1)
                .setContinuousDropOff(2)
                .setShapeDistTraveled(20d)
                .setTimepoint(1);
        builder.clear();
        GtfsStopTime underTest = builder.build();

        assertThat(underTest.tripId()).isEqualTo(DEFAULT_TRIP_ID);
        assertThat(underTest.arrivalTime()).isEqualTo(DEFAULT_ARRIVAL_TIME);
        assertThat(underTest.departureTime()).isEqualTo(DEFAULT_DEPARTURE_TIME);
        assertThat(underTest.stopId()).isEqualTo(DEFAULT_STOP_ID);
        assertThat(underTest.stopSequence()).isEqualTo(DEFAULT_STOP_SEQUENCE);
        assertThat(underTest.stopHeadsign()).isEqualTo(DEFAULT_STOP_HEADSIGN);
        // drop_off_type and pickup_typr are optional fields with a default value.
        assertThat(underTest.pickupType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_PICKUP_TYPE));
        assertThat(underTest.dropOffType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_DROP_OFF_TYPE));
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup())
                .isEqualTo(GtfsContinuousPickupDropOff.forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousDropOff())
                .isEqualTo(GtfsContinuousPickupDropOff.forNumber(DEFAULT_CONTINUOUS_DROP_OFF));
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);
        // timepoint is an optional field with a default value.
        assertThat(underTest.timepoint()).isEqualTo(GtfsStopTimesTimepoint.forNumber(DEFAULT_TIMEPOINT));

        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasArrivalTime()).isFalse();
        assertThat(underTest.hasDepartureTime()).isFalse();
        assertThat(underTest.hasStopId()).isFalse();
        assertThat(underTest.hasStopSequence()).isFalse();
        assertThat(underTest.hasStopHeadsign()).isFalse();
        assertThat(underTest.hasPickupType()).isFalse();
        assertThat(underTest.hasDropOffType()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
        assertThat(underTest.hasTimepoint()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        Builder builder = new Builder();
        GtfsStopTime underTest = builder.build();

        assertThat(underTest.tripId()).isNull();
        assertThat(underTest.arrivalTime()).isNull();
        assertThat(underTest.departureTime()).isNull();
        assertThat(underTest.stopId()).isNull();
        assertThat(underTest.stopSequence()).isEqualTo(DEFAULT_STOP_SEQUENCE);
        assertThat(underTest.stopHeadsign()).isNull();
        // drop_off_type and pickup_typr are optional fields with a default value.
        assertThat(underTest.pickupType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_PICKUP_TYPE));
        assertThat(underTest.dropOffType()).isEqualTo(GtfsPickupDropOff.forNumber(DEFAULT_DROP_OFF_TYPE));
        // continuous_drop_off and continuous_pickup are optional fields with a default value.
        assertThat(underTest.continuousPickup()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_PICKUP));
        assertThat(underTest.continuousDropOff()).isEqualTo(GtfsContinuousPickupDropOff
                .forNumber(DEFAULT_CONTINUOUS_DROP_OFF));
        assertThat(underTest.shapeDistTraveled()).isEqualTo(DEFAULT_SHAPE_DIST_TRAVELED);
        assertThat(underTest.timepoint()).isEqualTo(GtfsStopTimesTimepoint.APPROXIMATE);

        assertThat(underTest.hasTripId()).isFalse();
        assertThat(underTest.hasArrivalTime()).isFalse();
        assertThat(underTest.hasDepartureTime()).isFalse();
        assertThat(underTest.hasStopId()).isFalse();
        assertThat(underTest.hasStopSequence()).isFalse();
        assertThat(underTest.hasStopHeadsign()).isFalse();
        assertThat(underTest.hasPickupType()).isFalse();
        assertThat(underTest.hasDropOffType()).isFalse();
        assertThat(underTest.hasContinuousPickup()).isFalse();
        assertThat(underTest.hasContinuousDropOff()).isFalse();
        assertThat(underTest.hasShapeDistTraveled()).isFalse();
        // timepoint is an optional field with a default value.
        assertThat(underTest.hasTimepoint()).isFalse();
    }
}
