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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.transfers;

import java.util.stream.Stream;

public enum TransferType {
    RECOMMENDED_TRANSFER_POINT(0),
    TIMED_TRANSFER_POINT(1),
    MINIMUM_TIME_TRANSFER(2),
    IMPOSSIBLE_TRANSFERS(3);

    private final int value;

    TransferType(final int value) {
        this.value = value;
    }

    static public TransferType fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return RECOMMENDED_TRANSFER_POINT;
        }
        return Stream.of(TransferType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}