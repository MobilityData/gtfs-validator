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

import java.util.ArrayList;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.GeospatialUtil;

/**
 * Validates: all records of `trips.txt` refer to a collection of {@code GtfsStopTime} from file
 * `stop_times.txt` of which -when sorted by `stop_sequence`- the travel speed between each stop is
 * not above 150 km/h
 *
 * <p>Generated notice: {@link FastTravelBetweenStopsNotice}.
 */
@GtfsValidator
public class TripTravelSpeedValidator extends FileValidator {
  private static final float METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR = 3.6f;
  @Inject GtfsTripTableContainer tripTable;
  @Inject GtfsStopTimeTableContainer stopTimeTable;
  @Inject GtfsStopTableContainer stopTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    tripTable
        .getEntities()
        .forEach(
            trip -> {
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

              stopTimeTable
                  .byTripId(trip.tripId())
                  .forEach(
                      stopTime -> {

                        // prepare data for current iteration
                        GtfsStop currentStop = stopTable.byStopId(stopTime.stopId());
                        double currentStopLat = currentStop.stopLat();
                        double currentStopLon = currentStop.stopLon();

                        GtfsTime currentArrivalTime = stopTime.arrivalTime();
                        int distanceFromPreviousStopMeter =
                            GeospatialUtil.distanceBetweenMeter(
                                previousStopsData.latitude,
                                previousStopsData.longitude,
                                currentStopLat,
                                currentStopLon);
                        boolean sameArrivalAndDeparture = false;

                        if (previousStopsData.departureTime != null && currentArrivalTime != null) {
                          sameArrivalAndDeparture =
                              currentArrivalTime.equals(previousStopsData.departureTime);

                          if (!sameArrivalAndDeparture) {
                            int durationSecond =
                                currentArrivalTime.getSecondsSinceMidnight()
                                    - previousStopsData.departureTime.getSecondsSinceMidnight();
                            int distanceMeter =
                                distanceFromPreviousStopMeter
                                    + previousStopsData.accumulatedDistanceMeter;
                            int speedMeterPerSecond = distanceMeter / durationSecond;

                            if (speedMeterPerSecond
                                > 42) { // roughly 150 km per hour. Put it in default parameters
                              previousStopsData.accumulatedStopSequence.add(
                                  stopTime.stopSequence());

                              noticeContainer.addValidationNotice(
                                  new FastTravelBetweenStopsNotice(
                                      trip.tripId(),
                                      speedMeterPerSecond
                                          * METER_PER_SECOND_TO_KMH_CONVERSION_FACTOR,
                                      new ArrayList<>(previousStopsData.accumulatedStopSequence)));
                            }
                          }
                        }
                        // Prepare data for next iteration
                        if (sameArrivalAndDeparture) {
                          previousStopsData.accumulatedDistanceMeter +=
                              distanceFromPreviousStopMeter;
                        } else {
                          previousStopsData.accumulatedDistanceMeter = 0;
                          previousStopsData.accumulatedStopSequence.clear();
                        }

                        previousStopsData.departureTime = stopTime.departureTime();
                        previousStopsData.latitude = currentStopLat;
                        previousStopsData.longitude = currentStopLon;
                        previousStopsData.accumulatedStopSequence.add(stopTime.stopSequence());
                      });
            });
  }
}
