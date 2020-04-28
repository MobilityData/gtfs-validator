package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RouteTypeTest {

    @Test
    public void createRouteTypeWithNullValueShouldReturnNull() {
        assertNull(RouteType.fromInt(null));
    }

    @Test
    public void createRouteTypeWithUnexpectedValueShouldReturnNull() {
        assertNull(RouteType.fromInt(13));
    }

    @Test
    public void createRouteTypeWithExpectedValue0ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(0), RouteType.LIGHT_RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue1ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(1), RouteType.SUBWAY);
    }

    @Test
    public void createRouteTypeWithExpectedValue2ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(2), RouteType.RAIL);
    }

    @Test
    public void createRouteTypeWithExpectedValue3ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(3), RouteType.BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue4ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(4), RouteType.FERRY);
    }

    @Test
    public void createRouteTypeWithExpectedValue5ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(5), RouteType.CABLE_TRAM);
    }

    @Test
    public void createRouteTypeWithExpectedValue6ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(6), RouteType.AERIAL_LIFT);
    }

    @Test
    public void createRouteTypeWithExpectedValue7ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(7), RouteType.FUNICULAR);
    }

    @Test
    public void createRouteTypeWithExpectedValue11ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(11), RouteType.TROLLEY_BUS);
    }

    @Test
    public void createRouteTypeWithExpectedValue12ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(12), RouteType.MONORAIL);
    }
}