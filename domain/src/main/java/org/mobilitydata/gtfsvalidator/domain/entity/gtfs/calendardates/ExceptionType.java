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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import java.util.stream.Stream;

public enum ExceptionType {
    ADDED_SERVICE(1),
    REMOVED_SERVICE(2);

    private final int value;

    ExceptionType(final int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the {@link ExceptionType} enum item value matching the integer
     * passed as parameter. Returns null if the integer passed as parameter is null or does not match any
     * {@link ExceptionType} enum item
     *
     * @param fromValue value to match to {@link ExceptionType} enum items
     * @return the enum item matching the integer passed as parameter. Or null if the integer passed as parameter is
     * null or does not match any {@link ExceptionType} enum item
     */
    static public ExceptionType fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return null;
        }
        return Stream.of(ExceptionType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }

    /**
     * Returns true if the integer passed as parameter is expected for this enum, otherwise returns false
     *
     * @param value the integer to associate with this enum values
     * @return true if the integer passed as parameter is expected for this enum, otherwise returns false
     */
    static public boolean isEnumValueValid(final Integer value) {
        if (value == null) {
            return false;

        }
        return Stream.of(ExceptionType.values())
                .filter(enumItem -> enumItem.value == value)
                .findAny()
                .orElse(null) != null;
    }
}
