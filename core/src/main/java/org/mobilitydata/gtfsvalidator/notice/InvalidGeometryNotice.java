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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.TERM_DEFINITIONS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A polygon in `locations.geojson` is unparsable or invalid.
 *
 * <p>Each polygon must be valid by the definition of the <a
 * href="http://www.opengis.net/doc/is/sfa/1.2.1" target="_blank"> OpenGIS Simple Features
 * Specification, section 6.1.11 </a>.
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(TERM_DEFINITIONS))
public class InvalidGeometryNotice extends ValidationNotice {

  /** The id of the faulty record. */
  private final String featureId;

  /** The geometry type of the feature containing the invalid polygon. */
  private final String geometryType;

  /** The validation error details. */
  private final String message;

  public InvalidGeometryNotice(String featureId, String geometryType, String validationError) {
    this.featureId = featureId;
    this.geometryType = geometryType;
    this.message = validationError;
  }
}
