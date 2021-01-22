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
import org.mobilitydata.gtfsvalidator.type.GtfsColor;

public class RouteColorContrastNotice extends ValidationNotice {
  public RouteColorContrastNotice(
      String routeId, long csvRowNumber, GtfsColor routeColor, GtfsColor routeTextColor) {
    super(
        ImmutableMap.of(
            "routeId",
            routeId,
            "csvRowNumber",
            csvRowNumber,
            "routeColor",
            routeColor.toHtmlColor(),
            "routeTextColor",
            routeTextColor.toHtmlColor()));
  }

  @Override
  public String getCode() {
    return "route_color_contrast";
  }
}
