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

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that every trip in "trips.txt" is used by some stop from "stop_times.txt"
 *
 * <p>Generated notice: {@link UnusedTripNotice}.
 */
@GtfsValidator
public class TripUsageValidator extends FileValidator {

  private final GtfsTripTableContainer tripTable;

  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  TripUsageValidator(GtfsTripTableContainer tripTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Do not report the same trip_id multiple times.
    Set<String> reportedTrips = new HashSet<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (reportedTrips.add(tripId) && stopTimeTable.byTripId(tripId).isEmpty()) {
        noticeContainer.addValidationNotice(new UnusedTripNotice(tripId, trip.csvRowNumber()));
      }
    }
  }

  /**
   * Trip is not be used in `stop_times.txt`
   *
   * <p>Trips should be referred to at least once in `stop_times.txt`.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsTripSchema.class, GtfsStopTimeSchema.class}),
      urls = {
        @UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class UnusedTripNotice extends ValidationNotice {

    /** The faulty record's id. */
    private final String tripId;

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    UnusedTripNotice(String tripId, int csvRowNumber) {
      this.tripId = tripId;
      this.csvRowNumber = csvRowNumber;
    }
  }
}
