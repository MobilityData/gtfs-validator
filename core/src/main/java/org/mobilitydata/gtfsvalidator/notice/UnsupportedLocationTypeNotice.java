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
 * An unsupported location type is used in the `locations.geojson` file.
 *
 * <p>The supported type is `FeatureCollection`.
 */
@GtfsValidationNotice(severity = ERROR)
public class UnsupportedLocationTypeNotice extends ValidationNotice {

  /** The value of the unsupported location type. */
  private final String locationTypeValue;

  public UnsupportedLocationTypeNotice(String locationTypeValue) {
    this.locationTypeValue = locationTypeValue;
  }
}
