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

public class ParsedEntity {

    private final RawFileInfo rawFileInfo;
    private final String entityId;
    private final Map<String, Object> contentByHeaderMap;

    public ParsedEntity(String id, Map<String, Object> contentByHeaderMap, RawFileInfo rawFileInfo) {
        this.contentByHeaderMap = contentByHeaderMap;
        this.rawFileInfo = rawFileInfo;
        this.entityId = id;
    }

    public String getEntityId() {
        return entityId;
    }

    public Object get(final String header) {
        return contentByHeaderMap.get(header);
    }

    public RawFileInfo getRawFileInfo() {
        return rawFileInfo;
    }
}
