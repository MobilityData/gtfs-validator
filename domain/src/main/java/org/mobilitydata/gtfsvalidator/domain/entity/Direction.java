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

package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum Direction {
    OUTBOUND(0),
    INBOUND(1);

    private final int value;

    Direction(int value) {
        this.value = value;
    }

    // TODO: implement behavior for unexpected enum value
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static public Direction fromInt(int fromValue) {
        return Stream.of(Direction.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get();
    }
}
