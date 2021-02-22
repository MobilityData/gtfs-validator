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
import java.util.List;
import java.util.Map.Entry;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
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
 * not above 150 km/h
 *
 * <p>Time complexity: <i>O(n * p)</i>, where <i>n</i> is the number of records in
 * <i>stop_times.txt</i> and <i>p</i> is the number of points in a trip shape.
 *
 * <p>Generated notice: {@link FastTravelBetweenStopsNotice}.
 */
@GtfsValidator
public class TripTravelSpeedValidator extends FileValidator {
  private static final double METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR = 3.6D;
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsStopTimeTableContainer stopTimeTable;
  @Inject GtfsStopTableContainer stopTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (Entry<String, List<GtfsStopTime>> entry :
        Multimaps.asMap(stopTimeTable.byTripIdMap()).entrySet()) {
      List<FastTravelBetweenStopsNotice> noticesForTrip = checkSpeedAlongTrip(entry);
      for (FastTravelBetweenStopsNotice notice : noticesForTrip) {
        noticeContainer.addValidationNotice(notice);
      }
    }
  }

  /**
   * Calculates the travel speed between {@code GtfsStopTime}. Generates a {@code
   * FastTravelBetweenStopsNotice} if the travel speed between {@code GtfsStopTimes} is greater than
   * 150 km/h.
   *
   * @param tripStopTimes the list of {@code GtfsStopTimes} sorted by stop_times.stop_sequence for a
   *     {@code GtfsTrip}
   * @return the list of {@code FastTravelBetweenStopsNotice} generated when checking speed travel
   *     along the given trip
   */
  private List<FastTravelBetweenStopsNotice> checkSpeedAlongTrip(
      Entry<String, List<GtfsStopTime>> tripStopTimes) {
    var previousStopsData =
        new Object() {
          final List<Integer> accumulatedStopSequence = new ArrayList<>();
          GtfsTime departureTime = null;
          double latitude;
          double longitude;
          // used to accumulate distance between stops with same arrival and departure
          // times
          int accumulatedDistanceMeter = 0;
        };
    List<FastTravelBetweenStopsNotice> notices = new ArrayList<>();
    for (GtfsStopTime stopTime : tripStopTimes.getValue()) { // prepare data for current iteration
      GtfsStop currentStop = stopTable.byStopId(stopTime.stopId());
      double currentStopLat = currentStop.stopLat();
      double currentStopLon = currentStop.stopLon();
      GtfsTime currentArrivalTime = stopTime.arrivalTime();
      double distanceFromPreviousStopMeter =
          GeospatialUtil.distanceInMeterBetween(
              previousStopsData.latitude,
              previousStopsData.longitude,
              currentStopLat,
              currentStopLon);
      boolean sameArrivalAndDeparture = false;
      if (previousStopsData.departureTime != null && currentArrivalTime != null) {
        sameArrivalAndDeparture = currentArrivalTime.equals(previousStopsData.departureTime);
        if (!sameArrivalAndDeparture) {
          int durationSecond =
              currentArrivalTime.getSecondsSinceMidnight()
                  - previousStopsData.departureTime.getSecondsSinceMidnight();
          double distanceMeter =
              distanceFromPreviousStopMeter + previousStopsData.accumulatedDistanceMeter;
          double speedMeterPerSecond = distanceMeter / durationSecond;
          if (speedMeterPerSecond > 42) { // roughly 150 km per hour. Put it in default parameters
            previousStopsData.accumulatedStopSequence.add(stopTime.stopSequence());
            notices.add(
                new FastTravelBetweenStopsNotice(
                    tripStopTimes.getKey(),
                    speedMeterPerSecond * METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR,
                    new ArrayList<>(previousStopsData.accumulatedStopSequence)));
          }
        }
      }
      // Prepare data for next iteration
      if (sameArrivalAndDeparture) {
        previousStopsData.accumulatedDistanceMeter += distanceFromPreviousStopMeter;
      } else {
        previousStopsData.accumulatedDistanceMeter = 0;
        previousStopsData.accumulatedStopSequence.clear();
      }
      previousStopsData.departureTime = stopTime.departureTime();
      previousStopsData.latitude = currentStopLat;
      previousStopsData.longitude = currentStopLon;
      previousStopsData.accumulatedStopSequence.add(stopTime.stopSequence());
    }
    return notices;
  }
}
