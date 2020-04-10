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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
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
    LEVELS("levels"),
    FEED_INFO("feed_info");

    private final String value;

    TableName(final String value) {
        this.value = value;
    }

    static public List<String> getValues() {
        return Stream.of(TableName.values()).map(enumItem -> enumItem.value).collect(Collectors.toList());
    }

    /**
     * Matches table_name field defined in translations.txt to its enum value. Throws an {@link IllegalArgumentException}
     * if the value is unexpected; else returns the matching enum value
     *
     * @param tableName defines the table that contains the field to be translated
     * @return the matching enum value
     * @throws IllegalArgumentException if the value is unexpected
     */
    @SuppressWarnings({"OptionalGetWithoutIsPresent"})
    static public TableName fromString(final String tableName) throws IllegalArgumentException {

        if (tableName == null) {
            throw new IllegalArgumentException("Field table_name in translations.txt can not be null");
        } else if (!getValues().contains(tableName)) {
            throw new IllegalArgumentException("Unexpected value for field table_name in translations.txt");
        } else {
            return Stream.of(TableName.values())
                    .filter(enumItem -> Objects.equals(enumItem.value, tableName))
                    .findAny()
                    .get();
        }
    }
}