/*
 * Copyright 2024 MobilityData
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

import static org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRef.FILE_REQUIREMENTS;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.SectionRefs;

/**
 * A stop_time entry has more than one geographical id defined.
 *
 * <p>In stop_times.txt, you can have only one of stop_id, location_group_id or location_id defined
 * for given entry.
 */
@GtfsValidationNotice(severity = ERROR, sections = @SectionRefs(FILE_REQUIREMENTS))
public class ForbiddenGeographyIdNotice extends ValidationNotice {

  /** The row of the faulty record. */
  private final int csvRowNumber;

  /** The id that already exists. */
  private final String stopId;

  /** The id that already exists. */
  private final String locationGroupId;

  /** The id that already exists. */
  private final String locationId;

  public ForbiddenGeographyIdNotice(
      int csvRowNumber, String stopId, String locationGroupId, String locationId) {
    this.csvRowNumber = csvRowNumber;
    this.stopId = stopId;
    this.locationGroupId = locationGroupId;
    this.locationId = locationId;
  }
}
