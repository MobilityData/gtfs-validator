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

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * This enum matches types that can be found in the table_name field of translations.txt
 * // see https://gtfs.org/reference/static#translationstxt
 * It's used to decide which concrete type derived from {@link TranslationTableBase} to instantiate
 */
public enum TableName {
    AGENCY("agency"),
    STOPS("stops"),
    ROUTES("routes"),
    TRIPS("trips"),
    STOP_TIMES("stop_times"),
    CALENDAR("calendar"),
    CALENDAR_DATES("calendar_dates"),
    FARE_RULES("fare_rules"),
    FARE_ATTRIBUTES("fare_attributes"),
    SHAPES("shapes"),
    FREQUENCIES("frequencies"),
    TRANSFERS("transfers"),
    PATHWAYS("pathways"),
    LEVELS("levels"),
    FEED_INFO("feed_info"),
    ATTRIBUTIONS("attributions");

    private String value;

    TableName(final String value) {
        this.value = value;
    }

    /**
     * Matches table_name field defined in translations.txt to its enum value. Throws an {@link IllegalArgumentException}
     * if the value is unexpected; else returns the matching enum value
     *
     * @param tableName defines the table that contains the field to be translated
     * @return the matching enum value
     * @throws IllegalArgumentException if the value is unexpected
     */
    @SuppressWarnings({"OptionalGetWithoutIsPresent", "SuspiciousMethodCalls"})
    static public TableName fromString(final String tableName) throws IllegalArgumentException {
        if (Arrays.asList(TableName.values()).contains(tableName)) {
            throw new IllegalArgumentException("Unexpected enum value for table_name");
        }
        return Stream.of(TableName.values())
                .filter(enumItem -> Objects.equals(enumItem.value, tableName))
                .findAny()
                .get();
    }
}