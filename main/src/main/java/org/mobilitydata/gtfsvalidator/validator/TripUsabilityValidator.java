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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that every trip in "trips.txt" is used by at least two stops from "stop_times.txt"
 *
 * <p>Generated notice: {@link UnusableTripNotice}.
 */
@GtfsValidator
public class TripUsabilityValidator extends FileValidator {

  private final GtfsTripTableContainer tripTable;

  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripUsabilityValidator(
      GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (stopTimeTable.byTripId(tripId).size() <= 1) {
        noticeContainer.addValidationNotice(new UnusableTripNotice(trip.csvRowNumber(), tripId));
      }
    }
  }

  /**
   * Trips must have more than one stop to be usable.
   *
   * <p>A trip must visit more than one stop in stop_times.txt to be usable by passengers for
   * boarding and alighting.
   */
  @GtfsValidationNotice(severity = WARNING)
  static class UnusableTripNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's id. */
    private final String tripId;

    UnusableTripNotice(int csvRowNumber, String tripId) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
}
