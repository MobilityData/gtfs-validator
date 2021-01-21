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

/** Short and long name are equal for a route. */
public class RouteShortAndLongNameEqualNotice extends ValidationNotice {
  public RouteShortAndLongNameEqualNotice(
      String routeId, long csvRowNumber, String routeShortName, String routeLongName) {
    super(
        ImmutableMap.of(
            "routeId", routeId,
            "csvRowNumber", csvRowNumber,
            "routeShortName", routeShortName,
            "routeLongName", routeLongName));
  }

  @Override
  public String getCode() {
    return "route_short_and_long_name_equal";
  }
}
