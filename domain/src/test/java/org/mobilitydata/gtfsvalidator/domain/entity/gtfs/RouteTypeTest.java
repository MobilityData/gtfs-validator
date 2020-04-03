package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTypeTest {

    @Test
    public void createRouteTypeWithNullValueShouldThrowException() {

        Integer unexpectedEnumValue = null;

        Exception exception = assertThrows(NullPointerException.class, () -> RouteType.fromInt(unexpectedEnumValue));

        assertEquals("Unexpected value for field route_type in routes.txt", exception.getMessage());
    }

    @Test
    public void createRouteTypeWithUnexpectedValueShouldThrowException() {

        int unexpectedEnumValue = 13;

        assertThrows(NullPointerException.class, () -> RouteType.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createRouteTypeWithExpectedValue0ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 0;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.LIGHT_RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue1ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 1;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.SUBWAY);
    }

    @Test
    public void createRouteTypeWithExpectedValue2ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 2;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue3ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 3;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue4ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 4;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.FERRY);
    }

    @Test
    public void createRouteTypeWithExpectedValue5ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 5;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

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

        int unexpectedEnumValue = 7;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.FUNICULAR);
    }

    @Test
    public void createRouteTypeWithExpectedValue11ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 11;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.TROLLEY_BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue12ShouldNotThrowExceptionAndReturnCorrectRouteType() {

        int unexpectedEnumValue = 12;

        RouteType routeType = RouteType.fromInt(unexpectedEnumValue);

        assertEquals(routeType, RouteType.MONORAIL);
    }
}