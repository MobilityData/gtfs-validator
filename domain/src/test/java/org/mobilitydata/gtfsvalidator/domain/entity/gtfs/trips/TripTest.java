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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.BikesAllowedStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.WheelchairAccessibleStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__ENUM_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.NOTICE_SPECIFIC_KEY__FIELD_NAME;

class TripTest {

    // Field routeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullRouteIdShouldGenerateMissingRequiredValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId(null)
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("route_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("trip id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullServiceIdShouldGenerateMissingRequiredValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId(null)
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("service_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("trip id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field tripId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullTripIdShouldGenerateMissingRequiredValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId(null)
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("trip_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createTripWithInvalidDirectionIdShouldGenerateUnexpectedEnumValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(3)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("direction_id", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("trip id", notice.getEntityId());
        assertEquals(3, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
    }

    @Test
    public void createTripWithValidDirectionIdShouldNotGenerateNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(0);

        final EntityBuildResult<?> buildResult = underTest.build();

        assertTrue(buildResult.getData() instanceof Trip);

        final Trip toCheck = (Trip) buildResult.getData();

        assertEquals("route id", toCheck.getRouteId());
        assertEquals("service id", toCheck.getServiceId());
        assertEquals("trip id", toCheck.getTripId());
        assertEquals("test", toCheck.getTripHeadsign());
        assertEquals("test", toCheck.getTripShortName());
        assertEquals(DirectionId.INBOUND, toCheck.getDirectionId());
        assertEquals("test", toCheck.getBlockId());
        assertEquals("test", toCheck.getShapeId());
        assertEquals(WheelchairAccessibleStatus.WHEELCHAIR_ACCESSIBLE, toCheck.getWheelchairAccessibleStatus());
        assertEquals(BikesAllowedStatus.UNKNOWN_BIKES_ALLOWANCE, toCheck.getBikesAllowedStatus());
    }

    @Test
    public void createTripWithInvalidWheelchairAccessibleShouldGenerateUnexpectedEnumValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(4)
                .bikesAllowed(0);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("wheelchair_accessible", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("trip id", notice.getEntityId());
        assertEquals(4, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
    }

    @Test
    public void createTripWithInvalidBikesAllowedShouldGenerateUnexpectedEnumValueNotice() {
        final Trip.TripBuilder underTest = new Trip.TripBuilder();

        underTest.routeId("route id")
                .serviceId("service id")
                .tripId("trip id")
                .tripHeadsign("test")
                .tripShortName("test")
                .directionId(1)
                .blockId("test")
                .shapeId("test")
                .wheelchairAccessible(1)
                .bikesAllowed(4);

        final EntityBuildResult<?> entityBuildResult = underTest.build();
        assertTrue(entityBuildResult.getData() instanceof ArrayList);

        // to avoid lint regarding cast, the test is designed so that method .getData() returns a list of notices.
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("trips.txt", notice.getFilename());
        assertEquals("bikes_allowed", notice.getExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME));
        assertEquals("trip id", notice.getEntityId());
        assertEquals(4, notice.getExtra(NOTICE_SPECIFIC_KEY__ENUM_VALUE));
    }
}