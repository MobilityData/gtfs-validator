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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import java.util.stream.Stream;

public enum DirectionId {
    OUTBOUND(0),
    INBOUND(1);

    private final int value;

    DirectionId(final int value) {
        this.value = value;
    }

    static public DirectionId fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return null;
        } else if (isEnumValueValid(fromValue)) {
            //noinspection OptionalGetWithoutIsPresent
            return Stream.of(DirectionId.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .get();
        } else {
            return null;
        }
    }

    static public boolean isEnumValueValid(final Integer value) {
        if (value == null) {
            return true;
        } else {
            return Stream.of(DirectionId.values())
                    .filter(enumItem -> enumItem.value == value)
                    .findAny()
                    .orElse(null) != null;
        }
    }
}
