/*
 * Copyright 2024 MobilityData LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * A key in `locations.geojson` is duplicated.
 *
 * <p>The key must be unique for each feature in the GeoJSON file.
 */
@GtfsValidationNotice(severity = ERROR)
public class DuplicateGeoJsonKeyNotice extends ValidationNotice {

  /** The duplicated key. */
  private final String featureId;

  /** The index of the first feature with the same key. */
  private final int firstIndex;

  /** The index of the other feature with the same key. */
  private final int secondIndex;

  public DuplicateGeoJsonKeyNotice(String featureId, int firstIndex, int secondIndex) {
    this.featureId = featureId;
    this.firstIndex = firstIndex;
    this.secondIndex = secondIndex;
  }
}
