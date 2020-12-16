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

import static org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader.FILENAME;

public class SameNameAndDescriptionForRouteNotice extends Notice {
    public SameNameAndDescriptionForRouteNotice(long csvRowNumber,
                                                String routeId,
                                                String routeDesc,
                                                String routeShortOrLongName) {
        super(new ImmutableMap.Builder<String, Object>()
                .put("filename", FILENAME)
                .put("routeId", routeId)
                .put("csvRowNumber", csvRowNumber)
                .put("routeDesc", routeDesc)
                .put("specifiedField", routeShortOrLongName)
                .build());
    }

    @Override
    public String getCode() {
        return "same_route_name_and_description";
    }
}
