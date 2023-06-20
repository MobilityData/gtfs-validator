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

import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that bikes_allowed information is present for ferry trips
 *
 * <p>Generated notice: {@link MissingBikeAllowanceInfoNotice}.
 */
@GtfsValidator
public class BikesAllowanceInfoValidator extends FileValidator {

  private final GtfsTripTableContainer tripTable;

  private final GtfsRouteTableContainer routeTable;

  @Inject
  BikesAllowanceInfoValidator(
      GtfsTripTableContainer tripTable, GtfsRouteTableContainer routeTable) {
    this.tripTable = tripTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    List<GtfsRoute> routes = routeTable.byRouteType(GtfsRouteType.FERRY);
    for (GtfsRoute route : routes) {
      List<GtfsTrip> trips = tripTable.byRouteId(route.routeId());
      for (GtfsTrip trip : trips) {
        if (trip.bikesAllowed() != GtfsBikesAllowed.UNRECOGNIZED
            && trip.bikesAllowed() != GtfsBikesAllowed.UNKNOWN) {
          continue;
        }
        noticeContainer.addValidationNotice(
            new MissingBikeAllowanceInfoNotice(trip.csvRowNumber(), trip.routeId(), trip.tripId()));
      }
    }
  }

  /**
   * Ferry trips should include bike allowance information.
   *
   * <p>All ferry trips should have a valid value in the bikes_allowed field in trips.txt.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsRouteSchema.class, GtfsTripSchema.class}))
  static class MissingBikeAllowanceInfoNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's route id. */
    private final String routeId;

    /** The faulty record's trip id. */
    private final String tripId;

    MissingBikeAllowanceInfoNotice(int csvRowNumber, String routeId, String tripId) {
      this.csvRowNumber = csvRowNumber;
      this.routeId = routeId;
      this.tripId = tripId;
    }
  }
}
