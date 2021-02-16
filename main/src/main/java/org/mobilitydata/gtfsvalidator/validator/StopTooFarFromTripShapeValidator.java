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

import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.util.GeospatialUtil;

/**
 * Validates: a {@link GtfsStop} is within a distance threshold for a trip shape.
 *
 * <p>Generated notice: {@link StopTooFarFromTripShapeNotice}.
 */
@GtfsValidator
public class StopTooFarFromTripShapeValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer stopTimeTable;
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsShapeTableContainer shapeTable;
  @Inject GtfsStopTableContainer stopTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    List<StopTooFarFromTripShapeNotice> notices = new ArrayList<>();

    // Cache for previously tested shape_id and stop_id pairs - we don't need to test them more than
    // once
    final Set<String> testedCache = new HashSet<>();

    for (Entry<String, List<GtfsStopTime>> entry :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
      String tripId = entry.getKey();
      List<GtfsStopTime> stopTimesForTrip = entry.getValue();
      GtfsTrip trip = tripTable.byTripId(tripId);
      if (trip == null || !trip.hasShapeId()) {
        // No shape for this trip - skip to the next trip
        continue;
      }
      // Check for possible errors for this combination of stop times and shape points for
      // this trip_id
      List<StopTooFarFromTripShapeNotice> noticesForTrip =
          GeospatialUtil.checkStopsWithinTripShape(
              tripId,
              stopTimesForTrip,
              trip.shapeId(),
              shapeTable.byShapeId(trip.shapeId()),
              stopTable,
              testedCache);
      notices.addAll(noticesForTrip);
    }
    for (StopTooFarFromTripShapeNotice notice : notices) {
      noticeContainer.addValidationNotice(notice);
    }
  }
}
