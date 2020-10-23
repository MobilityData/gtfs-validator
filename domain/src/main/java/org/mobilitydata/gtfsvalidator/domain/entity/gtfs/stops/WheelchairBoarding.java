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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops;

import java.util.stream.Stream;

public enum WheelchairBoarding {
    UNKNOWN_WHEELCHAIR_BOARDING(0),
    WHEELCHAIR_ACCESSIBLE(1),
    NOT_WHEELCHAIR_ACCESSIBLE(2),
    INVALID_VALUE(Integer.MAX_VALUE);

    private final int value;

    WheelchairBoarding(int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the enum item value matching the integer passed as parameter.
     * Returns default value UNKNOWN_WHEELCHAIR_BOARDING if the integer passed as parameter is null. This method
     * returns INVALID_VALUE if no enum item matches the integer passed as parameter.
     * {@link WheelchairBoarding} enum item
     *
     * @param fromValue value to match to {@link WheelchairBoarding} enum items
     * @return the default value UNKNOWN_WHEELCHAIR_BOARDING if the integer passed as parameter is null.
     * This method returns INVALID_VALUE if no enum item matches the integer passed as parameter.
     */
    static public WheelchairBoarding fromInt(Integer fromValue) {
        if (fromValue == null) {
            return UNKNOWN_WHEELCHAIR_BOARDING;
        }
        return Stream.of(WheelchairBoarding.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(INVALID_VALUE);
    }
}
