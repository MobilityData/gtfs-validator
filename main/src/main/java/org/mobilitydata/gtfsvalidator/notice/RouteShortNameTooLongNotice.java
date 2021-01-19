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
 * Short name of a route is too long (more than 12 characters, https://gtfs.org/best-practices/#routestxt).
 */
public class RouteShortNameTooLongNotice extends ValidationNotice {
    public RouteShortNameTooLongNotice(String routeId,
                                       long csvRowNumber, String routeShortName) {
        super(ImmutableMap.of(
                "routeId", routeId,
                "csvRowNumber", csvRowNumber,
                "routeShortName", routeShortName));
    }

    @Override
    public String getCode() {
        return "route_short_name_too_long";
    }
}
