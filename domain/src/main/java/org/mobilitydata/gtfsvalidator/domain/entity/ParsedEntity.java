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

import java.util.Map;

/**
 * The {@code ParsedEntity} class represents a row of a GTFS file that has been parsed: type of each column of the raw
 * has been determined.
 */
public class ParsedEntity {

    private final RawFileInfo rawFileInfo;
    private final String entityId;
    /**
     * Key: header string from original CSV file
     * Value: a String, Integer or Float, depending on the declared type of the column
     */
    private final Map<String, Object> contentByHeaderMap;

    /**
     * @param rawFileInfo        is a @code{RawFileInfo} instance
     * @param contentByHeaderMap is an object that maps rows of a GTFS .txt file on header name.
     * @param id                 is the id extracted from the original row.
     */
    public ParsedEntity(String id, Map<String, Object> contentByHeaderMap, RawFileInfo rawFileInfo) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.rawFileInfo = rawFileInfo;
        this.entityId = id;
    }

    /**
     * @return the id extracted from the original row.
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @return a string object that represents the value contained in a parsed row for a given header (column).
     */
    public Object get(final String header) {
        return contentByHeaderMap.get(header);
    }

    /**
     * @return an instance of @link{RawFileInfo} containing information related to the raw file from which
     * this entity's data was parsed.
     */
    public RawFileInfo getRawFileInfo() {
        return rawFileInfo;
    }
}
