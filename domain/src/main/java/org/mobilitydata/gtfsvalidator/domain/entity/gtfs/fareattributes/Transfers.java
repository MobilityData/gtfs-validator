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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes;

import java.util.stream.Stream;

public enum Transfers {
    NO_TRANSFERS_ALLOWED(0),
    ONE_TRANSFER_ALLOWED(1),
    TWO_TRANSFER_ALLOWED(2),
    UNLIMITED_TRANSFERS(-1);

    private final int value;

    Transfers(int value) {
        this.value = value;
    }

    static public Transfers fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return UNLIMITED_TRANSFERS;
        }
        if (0 <= fromValue && fromValue <= 2) {
            //noinspection OptionalGetWithoutIsPresent
            return Stream.of(Transfers.values())
                    .filter(enumItem -> enumItem.value == fromValue)
                    .findAny()
                    .get();
        } else {
            return null;
        }
    }
}
