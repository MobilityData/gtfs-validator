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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RouteTest {

    private static final String VALUE = "test_value";

    @Test
    public void createRouteWithNullValueForRequiredFieldShouldThrowException() {

        Route.RouteBuilder mockBuilder = mock(Route.RouteBuilder.class);

        when(mockBuilder.build()).thenCallRealMethod();

        assertThrows(NullPointerException.class, mockBuilder::build);

    }

    @Test
    public void createRouteWithValidValuesForFieldShouldNotThrowException() {

        Route.RouteBuilder builder = new Route.RouteBuilder();

        builder.routeId(VALUE)
                .agencyId(VALUE)
                .routeShortName(VALUE)
                .routeLongName(VALUE)
                .routeDesc(VALUE)
                .routeType(3)
                .routeUrl(VALUE)
                .routeColor(VALUE)
                .routeTextColor(VALUE)
                .routeSortOrder(1);

        Route route = builder.build();

        assertEquals(route.getRouteId(), VALUE);
        assertEquals(route.getAgencyId(), VALUE);
        assertEquals(route.getRouteShortName(), VALUE);
        assertEquals(route.getRouteLongName(), VALUE);
        assertEquals(route.getRouteDesc(), VALUE);
        assertEquals(route.getRouteType(), RouteType.BUS);
        assertEquals(route.getRouteUrl(), VALUE);
        assertEquals(route.getRouteColor(), VALUE);
        assertEquals(route.getRouteTextColor(), VALUE);
        assertEquals(route.getRouteSortOrder(), 1);
    }
}