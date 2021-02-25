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
 * All routes should have different `routes.route_long_name`. All routes should have different
 * `routes.route_short_name`.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class DuplicateRouteNameNotice extends ValidationNotice {
  public DuplicateRouteNameNotice(
      String duplicatedField, long csvRowNumber, String routeId, SeverityLevel severityLevel) {
    super(
        ImmutableMap.of(
            "duplicatedField", duplicatedField, "csvRowNumber", csvRowNumber, "routeId", routeId),
        severityLevel);
  }

  @Override
  public String getCode() {
    return "duplicate_route_name";
  }
}
