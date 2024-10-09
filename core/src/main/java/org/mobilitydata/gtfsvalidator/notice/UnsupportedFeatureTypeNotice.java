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
 * An unsupported feature type is used in the `locations.geojson` file.
 *
 * <p>Use `Feature` instead to comply with the spec.
 */
@GtfsValidationNotice(severity = ERROR)
public class UnsupportedFeatureTypeNotice extends ValidationNotice {

  /** The value of the unsupported GeoJSON type. */
  Integer featureIndex;

  /** The id of the faulty record. */
  String featureId;

  /** The feature type of the faulty record. */
  String featureType;

  public UnsupportedFeatureTypeNotice(Integer featureIndex, String featureId, String featureType) {
    this.featureIndex = featureIndex;
    this.featureId = featureId;
    this.featureType = featureType;
  }
}
