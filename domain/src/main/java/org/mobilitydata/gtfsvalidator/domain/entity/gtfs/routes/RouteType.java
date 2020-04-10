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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes;

import java.util.stream.Stream;

/**
 * This enum matches types that can be found in the route_type field of route.txt
 * see https://gtfs.org/reference/static#routesstxt
 */
public enum RouteType {
    LIGHT_RAIL(0),
    SUBWAY(1),
    RAIL(2),
    BUS(3),
    FERRY(4),
    CABLE_TRAM(5),
    AERIAL_LIFT(6),
    FUNICULAR(7),
    TROLLEY_BUS(11),
    MONORAIL(12);

    private final int value;

    RouteType(int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the {@link RouteType} enum item value matching the integer passed
     * as parameter. Throws {@link NullPointerException} if the integer passed as parameter does not match any
     * {@link RouteType} enum item
     *
     * @param fromValue value to match to {@link RouteType} enum items
     * @return the enum item matching the integer passed as parameter.
     * @throws NullPointerException if the integer passed as parameter does not match any {@link RouteType} enum item
     */
    static public RouteType fromInt(Integer fromValue) throws NullPointerException {

        if (fromValue == null) {
            return null;
        }
        return Stream.of(RouteType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}