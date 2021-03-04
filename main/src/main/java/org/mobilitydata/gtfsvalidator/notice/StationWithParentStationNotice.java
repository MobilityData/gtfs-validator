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
 * A station has `parent_station` field set.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class StationWithParentStationNotice extends ValidationNotice {
  public StationWithParentStationNotice(
      long csvRowNumber, String stopId, String stopName, String parentStation) {
    super(
        ImmutableMap.of(
            "stopId",
            stopId,
            "stopName",
            stopName,
            "csvRowNumber",
            csvRowNumber,
            "parentStation",
            parentStation),
        SeverityLevel.ERROR);
  }
}
