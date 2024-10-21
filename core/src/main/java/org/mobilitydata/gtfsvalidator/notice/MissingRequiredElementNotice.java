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

/** A required element is missing in `locations.geojson`. */
@GtfsValidationNotice(severity = ERROR)
public class MissingRequiredElementNotice extends ValidationNotice {
  /** Index of the feature in the feature collection. */
  private final Integer featureIndex;

  /** The id of the faulty record. */
  private final String featureId;

  /** The missing required element. */
  private final String missingElement;

  public MissingRequiredElementNotice(
      String featureId, String missingElement, Integer featureIndex) {
    this.featureId = featureId;
    this.featureIndex = featureIndex;
    this.missingElement = missingElement;
  }
}
