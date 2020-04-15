package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RouteTypeTest {

    @Test
    public void createRouteTypeWithNullValueShouldReturnNull() {
        final Integer unexpectedEnumValue = null;

        assertNull(RouteType.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createRouteTypeWithUnexpectedValueShouldReturnNull() {
        final int unexpectedEnumValue = 13;

        assertNull(RouteType.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createRouteTypeWithExpectedValue0ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 0;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.LIGHT_RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue1ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 1;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.SUBWAY);
    }

    @Test
    public void createRouteTypeWithExpectedValue2ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 2;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue3ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 3;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue4ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 4;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.FERRY);
    }

    @Test
    public void createRouteTypeWithExpectedValue5ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 5;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.CABLE_TRAM);
    }

    @Test
    public void createRouteTypeWithExpectedValue6ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        int unexpectedEnumValue = 6;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.AERIAL_LIFT);
    }

    @Test
    public void createRouteTypeWithExpectedValue7ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 7;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.FUNICULAR);
    }

    @Test
    public void createRouteTypeWithExpectedValue11ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 11;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.TROLLEY_BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue12ShouldNotThrowExceptionAndReturnCorrectRouteType() {
        final int unexpectedEnumValue = 12;

        final RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.MONORAIL);
    }
}