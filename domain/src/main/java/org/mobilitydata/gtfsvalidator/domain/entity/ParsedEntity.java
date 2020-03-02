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
    private final Map<String, Object> contentByHeaderMap;

    /**
     * Constructor
     * rawFileInfo is a @code{RawFileInfo} instance
     * contentByHeaderMap is an object that maps rows of a GTFS .txt file on header name.
     * entityId is the id extracted from the original row.
     */
    public ParsedEntity(String id, Map<String, Object> contentByHeaderMap, RawFileInfo rawFileInfo) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.rawFileInfo = rawFileInfo;
        this.entityId = id;
    }

    /**
     * Return the id extracted from the original row.
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * Return a string object that represents the value contained in a parsed row for a given header (column).
     */
    public Object get(final String header) {
        return contentByHeaderMap.get(header);
    }

    /**
     * Return an instance of @code{RawFileInfo} class that contains the information related to the raw file from which
     * this entity's data was parsed.
     */
    public RawFileInfo getRawFileInfo() {
        return rawFileInfo;
    }
}
