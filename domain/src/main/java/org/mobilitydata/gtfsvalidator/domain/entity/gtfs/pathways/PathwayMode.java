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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import java.util.stream.Stream;

/**
 * This enum matches types that can be found in the route_type field of pathways.txt
 * see https://gtfs.org/reference/static#pathwaystxt
 */
public enum PathwayMode {
    WALKWAY(1),
    STAIRS(2),
    MOVING_SIDEWALK_TRAVELATOR(3),
    ESCALATOR(4),
    ELEVATOR(5),
    FARE_GATE(6),
    EXIT_GATE(7);

    private final int value;

    PathwayMode(final int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the {@link PathwayMode} enum item value matching the integer passed
     * as parameter. Returns null if the integer passed as parameter is null or does not match any
     * {@link PathwayMode} enum item
     *
     * @param fromValue value to match to {@link PathwayMode} enum items
     * @return the enum item matching the integer passed as parameter. Or null if the integer passed as parameter is
     * null or does not match any {@link PathwayMode} enum item
     */
    static public PathwayMode fromInt(final Integer fromValue) {
        try {
            return Stream.of(PathwayMode.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .orElse(null);
            // Note that a NPE is thrown by findAny when it is called on a null Stream (which happens when `fromValue` is
            // null). Therefore a try/catch block is required to handle such situation.
        } catch (NullPointerException e) {
            return null;
        }
    }

    /**
     * Returns true if the integer passed as parameter is expected for this enum, otherwise returns false
     *
     * @param value the integer to associate with this enum values
     * @return true if the integer passed as parameter is expected for this enum, otherwise returns false
     */
    static public boolean isEnumValueValid(final Integer value) {
        try {
            return Stream.of(PathwayMode.values())
                    .anyMatch(enumItem -> enumItem.value == value);
            // this is equivalent to
            // Stream.of(PathwayMode.values()).filter(enumItem -> enumItem.value == value).findAny().isPresent()
            // Note that a NPE is thrown by anyMatch when it is called on a null Stream (which happens when `value` is
            // null). Therefore a try/catch block is required to handle such situation.
        } catch (NullPointerException e) {
            return false;
        }
    }
}
