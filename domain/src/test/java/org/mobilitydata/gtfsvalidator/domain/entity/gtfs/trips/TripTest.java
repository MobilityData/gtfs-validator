package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TripTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullRouteIdShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field route_id can not be null", exception.getMessage());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullServiceIdShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field service_id can not be null", exception.getMessage());
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createTripWithNullTripIdShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field trip_id can not be null", exception.getMessage());
    }

    @Test
    public void createTripWithInvalidDirectionIdShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("unexpected value found for field direction_id", exception.getMessage());
    }

    @Test
    public void createTripWithValidDirectionIdShouldNotThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Trip toCheck = underTest.build();

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
    public void createTripWithInvalidWheelchairAccessibleShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value found for field wheelchair_accessible", exception.getMessage());
    }

    @Test
    public void createTripWithInvalidBikesAllowedShouldThrowException() {
        Trip.TripBuilder underTest = new Trip.TripBuilder();

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

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value found for field bikes_allowed", exception.getMessage());
    }
}