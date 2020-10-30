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

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.apache.commons.collections4.IterableUtils;

import java.util.Map;

/**
 * Represents a row of a GTFS file as raw string data.
 */
public class RawEntity {

    /**
     * Key: header string from original CSV file
     * Value: a String, the raw value read from the CSV file
     */
    private final Map<String, String> contentByHeaderMap;
    private final int entityIndex;

    /**
     * This class matches a 1 based index identifying the row location within a GTFS CSV file and its content as a
     * map of strings.
     *
     * @param contentByHeaderMap the object mapping rows of a GTFS .txt file on header name
     * @param entityIndex        the 1 based index of the row
     */
    public RawEntity(Map<String, String> contentByHeaderMap, int entityIndex) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.entityIndex = entityIndex;
    }

    /**
     * Returns the value contained in a row for a given header (column)
     *
     * @param header a GTFS file column header
     * @return the value contained in a row for a given header (column)
     */
    public String get(final String header) {
        return contentByHeaderMap.get(header);
    }

    /**
     * Returns the number of headers (columns).
     *
     * @return the number of headers (columns)
     */
    public int size() {
        return contentByHeaderMap.size();
    }

    /**
     * Returns the 1 based index of the row. This is used in use cases.
     *
     * @return the 1 based index of the row
     */
    public int getIndex() {
        return entityIndex;
    }

    /**
     * Returns true if the raw row is a blank line, otherwise returns false
     * @return true if the raw row is a blank line, otherwise returns false
     */
    public boolean isBlankLine() {
        return IterableUtils.matchesAll(contentByHeaderMap.values(),
                (fieldValue) -> fieldValue.isBlank() || fieldValue.isEmpty());
    }
}
