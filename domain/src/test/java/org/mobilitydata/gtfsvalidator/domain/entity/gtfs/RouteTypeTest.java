package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;

import static org.junit.jupiter.api.Assertions.*;

class RouteTypeTest {

    @Test
    void createRouteTypeWithNullValueShouldReturnNull() {
        assertNull(RouteType.fromInt(null));
    }

    @Test
    void createRouteTypeWithUnexpectedValueShouldReturnNull() {
        assertNull(RouteType.fromInt(13));
    }

    @Test
    void createRouteTypeWithExpectedValue0ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(0), RouteType.LIGHT_RAIL);
    }

    @Test
    void createRouteTypeWithExpectedValue1ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(1), RouteType.SUBWAY);
    }

    @Test
    void createRouteTypeWithExpectedValue2ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(2), RouteType.RAIL);
    }

    @Test
    void createRouteTypeWithExpectedValue3ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(3), RouteType.BUS);
    }

    @Test
    void createRouteTypeWithExpectedValue4ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(4), RouteType.FERRY);
    }

    @Test
    void createRouteTypeWithExpectedValue5ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(5), RouteType.CABLE_TRAM);
    }

    @Test
    void createRouteTypeWithExpectedValue6ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(6), RouteType.AERIAL_LIFT);
    }

    @Test
    void createRouteTypeWithExpectedValue7ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(7), RouteType.FUNICULAR);
    }

    @Test
    void createRouteTypeWithExpectedValue11ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(11), RouteType.TROLLEY_BUS);
    }

    @Test
    void createRouteTypeWithExpectedValue12ShouldReturnCorrectRouteType() {
        assertEquals(RouteType.fromInt(12), RouteType.MONORAIL);
    }

    @Test
    void shouldReturnFalseForUnexpectedValue13() {
        assertFalse(RouteType.isEnumValueValid(13));
    }

    @Test
    void shouldReturnTrueForExpectedValue11() {
        assertTrue(RouteType.isEnumValueValid(11));
    }

    @Test
    void shouldReturnFalseForUnexpectedNullValue() {
        assertFalse(RouteType.isEnumValueValid(null));
    }
}
