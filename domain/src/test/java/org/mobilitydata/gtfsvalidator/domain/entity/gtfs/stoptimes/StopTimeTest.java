/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IllegalFieldValueCombination;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;

class StopTimeTest {
    private static final int ARRIVAL_TIME = 34;
    private static final int DEPARTURE_TIME = 56;
    private static final String STOP_ID = "stop id";
    private static final int STOP_SEQUENCE = 0;
    private static final String STOP_HEADSIGN = "stop headsign";
    private static final int PICKUP_TYPE = 1;
    private static final int DROP_OFF_TYPE = 1;
    private static final int CONTINUOUS_DROP_OFF = 2;
    private static final int CONTINUOUS_PICKUP = 3;
    private static final float SHAPE_DIST_TRAVELED = 23f;
    private static final int TIMEPOINT = 0;
    private static final String FILENAME = "stop_times.txt";
    private static final String TRIP_ID = "trip id";

    @Test
    void createStopTimeWithNullTripIdShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        // suppressed warning regarding nullability of parameter used in method .tripId for the purpose of
        // this test, since this parameter is annotated as non null
        //noinspection ConstantConditions
        builder.tripId(null)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertNull(notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNullStopIdShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        // suppressed warning regarding nullability of parameter used in method .stopId for the purpose of
        // this test, since this parameter is annotated as non null
        //noinspection ConstantConditions
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(null)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("stop_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNullStopSequenceShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(null)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) buildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertNull(notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithInvalidPickupTypeShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(4)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) buildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("pickup_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(4, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithInvalidDropOffShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(4)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) buildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("drop_off_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(4, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithInvalidContinuousDropOffShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(4)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) buildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("continuous_drop_off", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(4, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithInvalidContinuousPickupShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(4)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) buildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("continuous_pickup", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(4, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNegativeShapeDistTraveledShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(-3f)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<FloatFieldValueOutOfRangeNotice> noticeCollection =
                (List<FloatFieldValueOutOfRangeNotice>) buildResult.getData();
        final FloatFieldValueOutOfRangeNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("shape_dist_traveled", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(0f, notice.getNoticeSpecific(KEY_RANGE_MIN));
        assertEquals(Float.MAX_VALUE, notice.getNoticeSpecific(KEY_RANGE_MAX));
        assertEquals(-3f, notice.getNoticeSpecific(KEY_ACTUAL_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNullShapeDistTraveledShouldNotGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(null)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof StopTime);
    }

    @Test
    void createStopTimeWithInvalidTimepointShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(5);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) buildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("timepoint", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(5, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNullArrivalTimeAndExactTimeShouldGenerateNotice (){
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(null)
                .departureTime(DEPARTURE_TIME)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(1);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> noticeCollection =
                (List<IllegalFieldValueCombination>) buildResult.getData();
        final IllegalFieldValueCombination notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("arrival_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("timepoint", notice.getNoticeSpecific(KEY_CONFLICTING_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithNullDepartureTimeAndExactTimeShouldGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(null)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(1);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof List);

        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<IllegalFieldValueCombination> noticeCollection =
                (List<IllegalFieldValueCombination>) buildResult.getData();
        final IllegalFieldValueCombination notice = noticeCollection.get(0);

        assertEquals(FILENAME, notice.getFilename());
        assertEquals("departure_time", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("timepoint", notice.getNoticeSpecific(KEY_CONFLICTING_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals("trip_id", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART));
        assertEquals("stop_sequence", notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART));
        assertEquals(TRIP_ID, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE));
        assertEquals(STOP_SEQUENCE, notice.getNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createStopTimeWithValidValuesShouldNotGenerateNotice() {
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(null)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        assertTrue(buildResult.getData() instanceof StopTime);
    }

    @Test
    void stopTimeShouldBeComparableByStopSequenceAscending(){
        final StopTime.StopTimeBuilder builder = new StopTime.StopTimeBuilder();
        builder.tripId(TRIP_ID)
                .arrivalTime(ARRIVAL_TIME)
                .departureTime(null)
                .stopId(STOP_ID)
                .stopSequence(STOP_SEQUENCE)
                .stopHeadsign(STOP_HEADSIGN)
                .pickupType(PICKUP_TYPE)
                .dropOffType(DROP_OFF_TYPE)
                .continuousDropOff(CONTINUOUS_DROP_OFF)
                .continuousPickup(CONTINUOUS_PICKUP)
                .shapeDistTraveled(SHAPE_DIST_TRAVELED)
                .timepoint(TIMEPOINT);

        final EntityBuildResult<?> buildResult = builder.build();

        final StopTime firstStopInSequence = (StopTime) buildResult.getData();

        builder.stopSequence(200);

        final StopTime secondStopInSequence = (StopTime) builder.build().getData();

        assertTrue(secondStopInSequence.isGreaterThan(firstStopInSequence));
    }
}
