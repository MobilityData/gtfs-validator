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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.GeospatialUtil;

/**
 * Validates: all records of `trips.txt` refer to a collection of {@code GtfsStopTime} from file
 * `stop_times.txt` of which -when sorted by `stop_sequence`- the travel speed between each stop is
 * not above 150 km/h (93 mph or 42 m/s).
 *
 * <p>Time complexity: <i>O(n)</i>, where <i>n</i> is the number of records in
 * <i>stop_times.txt</i>.
 *
 * <p>Generated notice: {@link TooFastTravelNotice}.
 */
@GtfsValidator
public class TooFastTravelValidator extends FileValidator {

  private static final double METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR = 3.6d;
  private static final int MAX_SPEED_METERS_PER_HOUR = 42; // 150 km/h or 93.2 mph

  private final GtfsTripTableContainer tripTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  TooFastTravelValidator(
      GtfsTripTableContainer tripTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsStopTableContainer stopTable) {
    this.tripTable = tripTable;
    this.stopTimeTable = stopTimeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (Entry<String, List<GtfsStopTime>> entry :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
      List<TooFastTravelNotice> noticesForTrip =
          checkSpeedAlongTrip(entry.getKey(), entry.getValue());
      for (TooFastTravelNotice notice : noticesForTrip) {
        noticeContainer.addValidationNotice(notice);
      }
    }
  }

  /**
   * Calculates the travel speed between {@code GtfsStopTime} for a single {@code GtfsTrip}. The
   * algorithm calculates the distance between each pair of stop times. It allows for blank
   * arrival/departure records (e.g., non-timepoints) and skips over them but still accumulates
   * distance. The algorithm will stop in case the conditions to generate a {@code
   * StopTimeWithArrivalBeforePreviousDepartureTimeNotice} are met. A {@code
   * FastTravelBetweenStopsNotice} is generated as soon as a travel speed above 150 km/h (93 mph or
   * 42 m/s) between stops is detected.
   *
   * @param tripId the trip_id as a string
   * @param tripStopTimes the list of {@code GtfsStopTimes} sorted by stop_times.stop_sequence for
   *     the {@code GtfsTrip} related to the tripId provided as parameter
   * @return the list of {@code FastTravelBetweenStopsNotice} for this trip
   */
  private List<TooFastTravelNotice> checkSpeedAlongTrip(
      String tripId, List<GtfsStopTime> tripStopTimes) {
    int beginStopSequence = tripStopTimes.get(0).stopSequence();
    GtfsTime prevDepartureTime = null;
    double prevStopLat = 0d;
    double prevStopLon = 0d;
    // used to accumulate distance between stops with same arrival and departure
    // times
    double accumulatedDistanceMeter = 0;
    List<TooFastTravelNotice> notices = new ArrayList<>();
    for (GtfsStopTime stopTime : tripStopTimes) { // prepare data for current iteration
      GtfsStop currentStop = stopTable.byStopId(stopTime.stopId());
      GtfsTime currentArrivalTime = stopTime.arrivalTime();
      double distanceFromPreviousStopMeters =
          GeospatialUtil.distanceInMeterBetween(
              prevStopLat, prevStopLon, currentStop.stopLat(), currentStop.stopLon());
      boolean sameArrivalAndDeparture = false;
      if (prevDepartureTime != null && stopTime.hasArrivalTime() && stopTime.hasDepartureTime()) {
        if (stopTime.arrivalTime().isBefore(prevDepartureTime)) {
          // Abort here if there is a StopTimeWithArrivalBeforePreviousDepartureTimeNotice for this
          // trip
          return notices;
        }
        sameArrivalAndDeparture = currentArrivalTime.equals(prevDepartureTime);
        if (!sameArrivalAndDeparture) {
          int durationSecond =
              currentArrivalTime.getSecondsSinceMidnight()
                  - prevDepartureTime.getSecondsSinceMidnight();
          double distanceMeter = distanceFromPreviousStopMeters + accumulatedDistanceMeter;
          double speedMeterPerSecond = distanceMeter / durationSecond;
          if (speedMeterPerSecond > MAX_SPEED_METERS_PER_HOUR) {
            notices.add(
                new TooFastTravelNotice(
                    tripId,
                    speedMeterPerSecond * METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR,
                    beginStopSequence,
                    stopTime.stopSequence()));
          }
        }
      }
      // Prepare data for next iteration
      if (!stopTime.hasArrivalTime() || !stopTime.hasDepartureTime() || sameArrivalAndDeparture) {
        accumulatedDistanceMeter += distanceFromPreviousStopMeters;

      } else {
        accumulatedDistanceMeter = 0;
        beginStopSequence = stopTime.stopSequence();
      }
      if (stopTime.hasDepartureTime()) {
        prevDepartureTime = stopTime.departureTime();
      }
      prevStopLat = currentStop.stopLat();
      prevStopLon = currentStop.stopLon();
    }
    return notices;
  }

  /**
   * Trip is too fast
   *
   * <p>SeverityLevel: {@code SeverityLevel.WARNING}
   */
  static class TooFastTravelNotice extends ValidationNotice {
    TooFastTravelNotice(
        String tripId, double speedkmh, int firstStopSequence, int lastStopSequence) {
      super(
          new ImmutableMap.Builder<String, Object>()
              .put("tripId", tripId)
              .put("speedkmh", speedkmh)
              .put("firstStopSequence", firstStopSequence)
              .put("lastStopSequence", lastStopSequence)
              .build(),
          SeverityLevel.WARNING);
    }
  }
}
