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
 * A GeoJSON feature has an unsupported geometry type in `locations.geojson`.
 *
 * <p>Each feature must have a geometry type that is supported by the GTFS spec. The supported
 * geometry types `Polygon` and `MultiPolygon`.
 */
@GtfsValidationNotice(severity = ERROR)
public class UnsupportedGeometryTypeNotice extends ValidationNotice {

  /** The index of the feature in the feature collection. */
  private final int featureIndex;

  /** The id of the faulty record. */
  private final String featureId;

  /** The geometry type of the faulty record. */
  private final String geometryType;

  public UnsupportedGeometryTypeNotice(int featureIndex, String featureId, String geometryType) {
    this.featureIndex = featureIndex;
    this.featureId = featureId;
    this.geometryType = geometryType;
  }
}
