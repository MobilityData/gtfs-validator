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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies;

/**
 * This enum matches types that can be found in the transfer field of fare_attributes.txt
 * see https://gtfs.org/reference/static#fare_attributestxt
 */
public enum ExactTimes {
    FREQUENCY_BASED_TRIPS(0),
    SCHEDULE_BASED_TRIPS(1);

    private final int value;

    ExactTimes(int value) {
        this.value = value;
    }

    /**
     * Matches enum values to Integer value. Returns the {@link org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.ExactTimes} enum item value matching the integer passed
     * as parameter. Returns null if the integer passed as parameter is null or does not match any
     * {@link org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.ExactTimes} enum item
     *
     * @param fromValue value to match to {@link org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.ExactTimes} enum items
     * @return the enum item matching the integer passed as parameter. Or null if the integer passed as parameter is
     * null or does not match any {@link org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.ExactTimes} enum item
     */
    static public ExactTimes fromInt(final Integer fromValue) {
        if (fromValue == null || fromValue == 0) {
            return FREQUENCY_BASED_TRIPS;
        } else if (fromValue == 1) {
            return SCHEDULE_BASED_TRIPS;
        } else {
            return null;
        }
    }
}