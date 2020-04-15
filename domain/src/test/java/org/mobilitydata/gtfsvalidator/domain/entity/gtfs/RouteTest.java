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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RouteTest {

    private static final String STRING_TEST_VALUE = "test_value";
    private static final int INT_TEST_VALUE = 0;

    @SuppressWarnings("ConstantConditions")
    @Test
    public void createRouteWithNullRouteIdShouldThrowException() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        underTest.routeId(null)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("route_id can not be null in routes.txt", exception.getMessage());
    }

    @Test
    public void createRouteWithInvalidRouteTypeShouldThrowException() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(15)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("Unexpected value, or null value for field route_type in routes.txt",
                exception.getMessage());
    }

    @Test
    public void createRouteWithValidValuesForFieldShouldNotThrowException() {
        final Route.RouteBuilder underTest = new Route.RouteBuilder();

        underTest.routeId(STRING_TEST_VALUE)
                .agencyId(STRING_TEST_VALUE)
                .routeShortName(STRING_TEST_VALUE)
                .routeLongName(STRING_TEST_VALUE)
                .routeDesc(STRING_TEST_VALUE)
                .routeType(INT_TEST_VALUE)
                .routeUrl(STRING_TEST_VALUE)
                .routeColor(STRING_TEST_VALUE)
                .routeTextColor(STRING_TEST_VALUE)
                .routeSortOrder(INT_TEST_VALUE);

        final Route route = underTest.build();

        assertEquals(route.getRouteId(), STRING_TEST_VALUE);
        assertEquals(route.getAgencyId(), STRING_TEST_VALUE);
        assertEquals(route.getRouteShortName(), STRING_TEST_VALUE);
        assertEquals(route.getRouteLongName(), STRING_TEST_VALUE);
        assertEquals(route.getRouteDesc(), STRING_TEST_VALUE);
        assertEquals(route.getRouteType(), RouteType.LIGHT_RAIL);
        assertEquals(route.getRouteUrl(), STRING_TEST_VALUE);
        assertEquals(route.getRouteColor(), STRING_TEST_VALUE);
        assertEquals(route.getRouteTextColor(), STRING_TEST_VALUE);
        assertEquals(route.getRouteSortOrder(), INT_TEST_VALUE);
    }
}