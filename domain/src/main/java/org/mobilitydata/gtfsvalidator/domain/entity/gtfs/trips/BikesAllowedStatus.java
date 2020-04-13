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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum BikesAllowedStatus {
    UNKNOWN_BIKES_ALLOWANCE(0),
    BIKES_ALLOWED(1),
    NO_BIKES_ALLOWED(2);

    private int value;

    BikesAllowedStatus(int value) {
        this.value = value;
    }

    static protected List<Integer> getValues() {
        return Stream.of(BikesAllowedStatus.values()).map(enumItem -> enumItem.value).collect(Collectors.toList());
    }

    static public BikesAllowedStatus fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return UNKNOWN_BIKES_ALLOWANCE;
        }
        if (getValues().contains(fromValue)) {
            //noinspection OptionalGetWithoutIsPresent
            return Stream.of(BikesAllowedStatus.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .get();
        } else {
            return null;
        }
    }
}