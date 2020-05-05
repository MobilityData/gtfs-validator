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

import java.util.stream.Stream;

public enum WheelchairAccessibleStatus {
    UNKNOWN_WHEELCHAIR_ACCESSIBILITY(0),
    WHEELCHAIR_ACCESSIBLE(1),
    NOT_WHEELCHAIR_ACCESSIBLE(2);

    private int value;

    WheelchairAccessibleStatus(final int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the enum item value matching the integer passed as parameter.
     * Returns default value UNKNOWN_WHEELCHAIR_ACCESSIBILITY if the integer passed as parameter is null. This method
     * returns null if no enum item matches the integer passed as parameter. Otherwise, returns the enum item associated
     * to the value passed as parameter.
     * {@link WheelchairAccessibleStatus} enum item
     *
     * @param fromValue value to match to {@link WheelchairAccessibleStatus} enum items
     * @return the default value UNKNOWN_WHEELCHAIR_ACCESSIBILITY if the integer passed as parameter is null.
     * This method returns null if no enum item matches the integer passed as parameter. Otherwise, returns the enum
     * item associated to the value passed as parameter.
     */
    static public WheelchairAccessibleStatus fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return UNKNOWN_WHEELCHAIR_ACCESSIBILITY;
        }
        if (isEnumValueValid(fromValue)) {
            //noinspection OptionalGetWithoutIsPresent
            return Stream.of(WheelchairAccessibleStatus.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .get();
        } else {
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
        if (value == null) {
            return true;
        }
        return Stream.of(WheelchairAccessibleStatus.values())
                .filter(enumItem -> enumItem.value == value)
                .findAny()
                .orElse(null) != null;
    }
}