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

import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME;

import com.google.common.collect.Multimaps;
import java.util.List;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingTripEdgeNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Validates: the first and last stop times (when ordering by `stop_sequence` value) of a trip
 * define a value for both `stop_times.departure_time` and `stop_times.arrival_times` fields.
 *
 * <p>Generated notice: {@link MissingTripEdgeNotice}.
 */
@GtfsValidator
public class MissingTripEdgeValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (Entry<String, List<GtfsStopTime>> entry :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
      String tripId = entry.getKey();
      List<GtfsStopTime> stopTimesForTrip = entry.getValue();
      GtfsStopTime tripFirstStop = stopTimesForTrip.get(0);
      GtfsStopTime tripLastStop = stopTimesForTrip.get(stopTimesForTrip.size() - 1);
      if (!tripFirstStop.hasArrivalTime()) {
        noticeContainer.addValidationNotice(
            new MissingTripEdgeNotice(
                tripFirstStop.csvRowNumber(),
                tripFirstStop.stopSequence(),
                tripId,
                ARRIVAL_TIME_FIELD_NAME));
      }
      if (!tripFirstStop.hasDepartureTime()) {
        noticeContainer.addValidationNotice(
            new MissingTripEdgeNotice(
                tripFirstStop.csvRowNumber(),
                tripFirstStop.stopSequence(),
                tripId,
                DEPARTURE_TIME_FIELD_NAME));
      }
      if (!tripLastStop.hasArrivalTime()) {
        noticeContainer.addValidationNotice(
            new MissingTripEdgeNotice(
                tripLastStop.csvRowNumber(),
                tripLastStop.stopSequence(),
                tripId,
                ARRIVAL_TIME_FIELD_NAME));
      }
      if (!tripLastStop.hasDepartureTime()) {
        noticeContainer.addValidationNotice(
            new MissingTripEdgeNotice(
                tripLastStop.csvRowNumber(),
                tripLastStop.stopSequence(),
                tripId,
                DEPARTURE_TIME_FIELD_NAME));
      }
    }
  }
}
