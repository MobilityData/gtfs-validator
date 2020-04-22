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

    static public PathwayMode fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return null;
        }
        return Stream.of(PathwayMode.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}