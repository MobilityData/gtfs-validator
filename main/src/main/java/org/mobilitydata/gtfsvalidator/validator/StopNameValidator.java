/*
 * Copyright 2021 MobilityData IO
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
package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;

/**
 * Validates {@code stops.stop_name} and {@code stops.stop_desc} for a single {@code GtfsStop}.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link SameNameAndDescriptionForStopNotice}
 *   <li>{@link MissingStopNameNotice}
 * </ul>
 */
@GtfsValidator
public class StopNameValidator extends SingleEntityValidator<GtfsStop> {

  @Override
  public void validate(GtfsStop stop, NoticeContainer noticeContainer) {
    if (stop.locationType() == GtfsLocationType.STOP
        || stop.locationType() == GtfsLocationType.STATION
        || stop.locationType() == GtfsLocationType.ENTRANCE) {
      if (stop.stopName().isEmpty()) {
        noticeContainer.addValidationNotice(
            new MissingStopNameNotice(stop.csvRowNumber(), stop.stopId(), stop.locationType()));
      }
    }
    if (!stop.hasStopName() || !stop.hasStopDesc()) {
      return;
    }
    if (!isValidStopDesc(stop.stopDesc(), stop.stopName())) {
      noticeContainer.addValidationNotice(
          new SameNameAndDescriptionForStopNotice(
              stop.csvRowNumber(), stop.stopId(), stop.stopDesc()));
    }
  }

  private boolean isValidStopDesc(String stopDesc, String stopName) {
    // ignore lower case and upper case difference
    return !stopDesc.equalsIgnoreCase(stopName);
  }

  /**
   * `stops.stop_name` is required for `location_type` equal to `0`, `1`, or `2`.
   *
   * <p>`stops.stop_name` is required for locations that are stops (`location_type=0`), stations
   * (`location_type=1`) or entrances/exits (`location_type=2`).
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsStopSchema.class))
  static class MissingStopNameNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final long csvRowNumber;

    /** `stops.location_type` of the faulty record. */
    private GtfsLocationType locationType;

    /** The `stops.stop_id` of the faulty record. */
    private final String stopId;

    MissingStopNameNotice(long csvRowNumber, String stopId, GtfsLocationType locationType) {
      super(SeverityLevel.ERROR);
      this.locationType = locationType;
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
    }
  }

  /**
   * Same name and description for stop.
   *
   * <p>The GTFS spec defines `stops.txt`
   * [stop_description](https://gtfs.org/reference/static/#stopstxt) as:
   *
   * <p>Description of the location that provides useful, quality information. Do not simply
   * duplicate the name of the location.
   */
  @GtfsValidationNotice(severity = WARNING)
  static class SameNameAndDescriptionForStopNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The id of the faulty record. */
    private final String stopId;

    /** The faulty record's `stop_desc`. */
    private final String stopDesc;

    SameNameAndDescriptionForStopNotice(int csvRowNumber, String stopId, String stopDesc) {
      super(SeverityLevel.WARNING);
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
      this.stopDesc = stopDesc;
    }
  }
}
