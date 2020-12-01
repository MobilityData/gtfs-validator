/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * Incorrect type of the parent location (e.g., a parent for a stop or an entrance must be a station).
 */
public class WrongParentLocationTypeNotice extends Notice {
    public WrongParentLocationTypeNotice(String stopId, long csvRowNumber, int locationType, String parentStation,
                                         long parentCsvRowNumber, int parentLocationType, int expectedLocationType) {
        super(new ImmutableMap.Builder<String, Object>()
                .put("stopId", stopId)
                .put("csvRowNumber", csvRowNumber)
                .put("locationType", locationType)
                .put("parentStation", parentStation)
                .put("parentCsvRowNumber", parentCsvRowNumber)
                .put("parentLocationType", parentLocationType)
                .put("expectedLocationType", expectedLocationType).build());
    }

    @Override
    public String getCode() {
        return "wrong_parent_location_type";
    }
}
