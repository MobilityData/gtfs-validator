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
 * A location that must have `parent_station` field does not have it.
 *
 * <p>The following location types must have `parent_station`: entrance, generic node, boarding
 * area.
 */
public class LocationWithoutParentStationNotice extends ValidationNotice {
  public LocationWithoutParentStationNotice(
      long csvRowNumber, String stopId, String stopName, int locationType) {
    super(
        ImmutableMap.of(
            "csvRowNumber",
            csvRowNumber,
            "stopId",
            stopId,
            "stopName",
            stopName,
            "locationType",
            locationType));
  }

  @Override
  public String getCode() {
    return "location_without_parent_station";
  }
}
