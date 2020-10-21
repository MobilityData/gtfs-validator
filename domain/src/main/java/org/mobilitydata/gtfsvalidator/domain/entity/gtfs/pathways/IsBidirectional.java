/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import java.util.stream.Stream;

/**
 * This enum matches types that can be found in the is_bidirectional field of pathways.txt
 * see https://gtfs.org/reference/static#pathwaystxt
 */
public enum IsBidirectional {
    UNIDIRECTIONAL(0),
    BIDIRECTIONAL(1);

    private final int value;

    IsBidirectional(final int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the {@link IsBidirectional} enum item value matching the integer
     * passed as parameter. Returns null if the integer passed as parameter is null or does not match any
     * {@link IsBidirectional} enum item
     *
     * @param fromValue value to match to {@link IsBidirectional} enum items
     * @return the enum item matching the integer passed as parameter. Or null if the integer passed as parameter is
     * null or does not match any {@link IsBidirectional} enum item
     */
    static public IsBidirectional fromInt(final Integer fromValue) {
        try {
            return Stream.of(IsBidirectional.values())
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
            return Stream.of(IsBidirectional.values())
                    .anyMatch(enumItem -> enumItem.value == value);
            // this is equivalent to
            // Stream.of(IsBidirectional.values()).filter(enumItem -> enumItem.value == value).findAny().isPresent()
            // Note that a NPE is thrown by anyMatch when it is called on a null Stream (which happens when `value` is
            // null). Therefore a try/catch block is required to handle such situation.
        } catch (NullPointerException e) {
            return false;
        }
    }
}
