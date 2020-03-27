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

package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Represent the possible values for timepoint field in stop_times.txt.
 * Indicates if arrival and departure times for a stop are strictly adhered to by the vehicle or if they
 * are instead approximate and/or interpolated times.
 */
public enum Timepoint {

    APPROXIMATED_TIMES(0),
    EXACT_TIMES(1);

    private int value;

    Timepoint(int value) {
        this.value = value;
    }

    /**
     * Returns the enum value associated to an int provided in the parameters. Throws {@link IllegalArgumentException}
     * if the parameter value is not expected.
     *
     * @param fromValue int to match with an enum value
     * @return enum value matching the int provided in the parameters
     * @throws IllegalArgumentException in case of unexpected value
     */
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static public Timepoint fromInt(Integer fromValue) throws IllegalArgumentException {
        if (fromValue == null) {
            return EXACT_TIMES;
        }
        if (!Arrays.asList(Timepoint.values()).contains(fromValue)) {
            throw new IllegalArgumentException("Unexpected enum value for timepoint");
        }
        return Stream.of(Timepoint.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get();
    }
}