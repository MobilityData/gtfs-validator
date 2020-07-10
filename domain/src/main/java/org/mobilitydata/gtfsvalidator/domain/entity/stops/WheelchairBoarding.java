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

package org.mobilitydata.gtfsvalidator.domain.entity.stops;

import java.util.Objects;
import java.util.stream.Stream;

public enum WheelchairBoarding {
    INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING(0),
    WHEELCHAIR_ACCESSIBLE(1),
    NOT_WHEELCHAIR_ACCESSIBLE(2);

    private final int value;

    WheelchairBoarding(int value) {
        this.value = value;
    }

    static public WheelchairBoarding fromInt(Integer fromValue) {
        if (fromValue == null) {
            return INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING;
        }
        return Stream.of(WheelchairBoarding.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(INHERIT_OR_UNKNOWN_WHEELCHAIR_BOARDING);
    }

    /**
     * Returns true if the integer passed as parameter is expected for this enum, otherwise returns false
     *
     * @param value the integer to associate with this enum values
     * @return true if the integer passed as parameter is expected for this enum, otherwise returns false
     */
    static public boolean isEnumValueValid(final Integer value) {
        if (value == null) {
            return true;
        }
        return Stream.of(WheelchairBoarding.values())
                .filter(enumItem -> Objects.equals(enumItem.value, value))
                .findAny()
                .orElse(null) != null;
    }
}
