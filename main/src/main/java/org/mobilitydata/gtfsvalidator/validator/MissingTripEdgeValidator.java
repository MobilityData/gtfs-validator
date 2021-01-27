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

import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.MissingTripEdgeNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

/**
 * Validates: the first and last stop times (when ordering by `stop_sequence` value) of a trip
 * define a value for both `stop_times.departure_time` and `stop_times.arrival_times` fields.
 *
 * <p>Generated notice: {@link MissingTripEdgeNotice}.
 */
@GtfsValidator
public class MissingTripEdgeValidator extends FileValidator {
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    tripTable
        .getEntities()
        .forEach(
            trip -> {
              List<GtfsStopTime> stopTimesForTrip = stopTimeTable.byTripId(trip.tripId());
              if (!stopTimesForTrip.isEmpty()) {
                GtfsStopTime tripFirstStop = stopTimesForTrip.get(0);
                GtfsStopTime tripLastStop = stopTimesForTrip.get(stopTimesForTrip.size() - 1);
                if (!tripFirstStop.hasArrivalTime()) {
                  noticeContainer.addValidationNotice(
                      new MissingTripEdgeNotice(
                          tripFirstStop.csvRowNumber(),
                          tripFirstStop.stopSequence(),
                          trip.tripId(),
                          "arrival_time"));
                }
                if (!tripFirstStop.hasDepartureTime()) {
                  noticeContainer.addValidationNotice(
                      new MissingTripEdgeNotice(
                          tripFirstStop.csvRowNumber(),
                          tripFirstStop.stopSequence(),
                          trip.tripId(),
                          "departure_time"));
                }
                if (!tripLastStop.hasArrivalTime()) {
                  noticeContainer.addValidationNotice(
                      new MissingTripEdgeNotice(
                          tripLastStop.csvRowNumber(),
                          tripLastStop.stopSequence(),
                          trip.tripId(),
                          "arrival_time"));
                }
                if (!tripLastStop.hasDepartureTime()) {
                  noticeContainer.addValidationNotice(
                      new MissingTripEdgeNotice(
                          tripLastStop.csvRowNumber(),
                          tripLastStop.stopSequence(),
                          trip.tripId(),
                          "departure_time"));
                }
              }
            });
  }
}
