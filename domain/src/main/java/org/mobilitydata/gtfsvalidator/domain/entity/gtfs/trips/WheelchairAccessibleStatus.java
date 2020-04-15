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

public enum WheelchairAccessibleStatus {
    UNKNOWN_WHEELCHAIR_ACCESSIBILITY(0),
    WHEELCHAIR_ACCESSIBLE(1),
    NOT_WHEELCHAIR_ACCESSIBLE(2);

    private int value;

    WheelchairAccessibleStatus(final int value) {
        this.value = value;
    }

    static protected List<Integer> getValues() {
        return Stream.of(WheelchairAccessibleStatus.values()).map(enumItem -> enumItem.value).collect(Collectors.toList());
    }

    static public WheelchairAccessibleStatus fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return UNKNOWN_WHEELCHAIR_ACCESSIBILITY;
        }
        if (getValues().contains(fromValue)) {
            //noinspection OptionalGetWithoutIsPresent
            return Stream.of(WheelchairAccessibleStatus.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .get();
        } else {
            return null;
        }
    }
}