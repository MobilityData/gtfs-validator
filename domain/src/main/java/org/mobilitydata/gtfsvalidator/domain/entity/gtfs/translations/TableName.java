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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * This enum matches types that can be found in the table_name field of translations.txt
 * // see https://gtfs.org/reference/static#translationstxt
 * It's used to decide which concrete type derived from {@link Translation} to instantiate
 */
public enum TableName {
    AGENCY("agency"),
    STOPS("stops"),
    ROUTES("routes"),
    TRIPS("trips"),
    STOP_TIMES("stop_times"),
    LEVELS("levels"),
    FEED_INFO("feed_info");

    private final String value;

    TableName(final String value) {
        this.value = value;
    }

    /**
     * Matches table_name field defined in translations.txt to its enum value. Returns the {@link TableName} enum item
     * value matching the string passed as parameter. Returns null if the string passed parameter is null or does not
     * match any {@link TableName} enum item.
     *
     * @param tableName defines the table that contains the field to be translated
     * @return the {@link TableName} enum item value matching the string passed as parameter. Or null if the string
     * passed as parameter is null or does not match any {@link TableName} enum item.
     */
    static public TableName fromString(final String tableName)  {
        if (tableName == null) {
            return null;
        } else {
            return Stream.of(TableName.values())
                    .filter(enumItem -> Objects.equals(enumItem.value, tableName))
                    .findAny()
                    .orElse(null);
        }
    }

    /**
     * Returns true if the string passed as parameter is expected for this enum, otherwise returns false
     *
     * @param value the string to associate with this enum values
     * @return true if the integer passed as parameter is expected for this enum, otherwise returns false
     */
    static public boolean isEnumValueValid(final String value) {
        if (value == null) {
            return false;
        }
        return Stream.of(TableName.values())
                .filter(enumItem -> Objects.equals(enumItem.value, value))
                .findAny()
                .orElse(null) != null;
    }
}