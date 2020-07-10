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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes;

import java.util.stream.Stream;

/**
 * Indicates drop off method. Valid options are:
 * <p>
 * 0 - Continuous stopping pickup
 * 1 or empty -  No continuous stopping pickup
 * 2 - Must phone agency to arrange continuous stopping pickup
 * 3 - Must coordinate with driver to arrange continuous stopping pickup
 */
public enum ContinuousPickup {
    CONTINUOUS_PICKUP(0),
    NO_CONTINUOUS_PICKUP(1),
    MUST_PHONE_CONTINUOUS_STOPPING_PICKUP(2),
    MUST_ASK_DRIVER_CONTINUOUS_STOPPING_PICKUP(3);

    private int value;

    ContinuousPickup(int value) {
        this.value = value;
    }

    /**
     * Returns the enum value associated to an {@link Integer} provided in the parameters.
     * Throws {@link IllegalArgumentException} if the parameter value is not expected.
     * If the parameter is null, returns NO_CONTINUOUS_PICKUP as default value.
     *
     * @param fromValue {@link Integer} to match with an enum value
     * @return If fromValue is null returns NO_CONTINUOUS_PICKUP by default, else returns the
     * enum value matching the {@link Integer} provided in the parameters.
     */
    static public ContinuousPickup fromInt(Integer fromValue) {
        if (fromValue == null) {
            return NO_CONTINUOUS_PICKUP;
        }
        if (!isEnumValid(fromValue)) {
            return null;
        }
        return Stream.of(ContinuousPickup.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get();
    }

    /**
     * Returns true if the integer passed as parameter is expected for this enum, otherwise returns false
     *
     * @param value the integer to associate with this enum values
     * @return true if the integer passed as parameter is expected for this enum, otherwise returns false
     */
    static public boolean isEnumValid(final Integer value) {
        if (value == null) {
            return true;
        }
        return Stream.of(ContinuousPickup.values())
                .filter(enumItem -> enumItem.value == value)
                .findAny()
                .orElse(null) != null;
    }
}
