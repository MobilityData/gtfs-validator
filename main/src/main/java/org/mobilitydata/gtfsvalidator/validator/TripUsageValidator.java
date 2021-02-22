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

import java.util.HashSet;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.ErrorDetectedException;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.UnusedShapeNotice;
import org.mobilitydata.gtfsvalidator.notice.UnusedTripNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates that every trip in "trips.txt" is used by some stop from "stop_times.txt"
 *
 * <p>Generated notice: {@link UnusedShapeNotice}.
 */
@GtfsValidator
public class TripUsageValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) throws ErrorDetectedException {
    // Do not report the same trip_id multiple times.
    Set<String> reportedTrips = new HashSet<>();
    for (GtfsTrip trip : tripTable.getEntities()) {
      String tripId = trip.tripId();
      if (reportedTrips.add(tripId) && stopTimeTable.byTripId(tripId).isEmpty()) {
        noticeContainer.addValidationNotice(new UnusedTripNotice(tripId, trip.csvRowNumber()));
      }
    }
  }
}
