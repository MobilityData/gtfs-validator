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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.validator.TripUsabilityValidator.UnusableTripNotice;

public class TripUsabilityValidatorTest {
  public static GtfsTrip createTrip(
      long csvRowNumber, String routeId, String serviceId, String tripId) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .build();
  }

  public static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TripUsabilityValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void tripServingMoreThanOneStopShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createTrip(1, "route id value", "service id value", "t0"),
                    createTrip(3, "route id value", "service id value", "t1")),
                ImmutableList.of(
                    createStopTime(0, "t0", "s0", 2),
                    createStopTime(2, "t0", "s1", 3),
                    createStopTime(0, "t1", "s3", 5),
                    createStopTime(2, "t1", "s4", 9))))
        .isEmpty();
  }

  @Test
  public void tripServingOneStopShouldGenerateNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createTrip(1, "route id value", "service id value", "t0"),
                    createTrip(3, "route id value", "service id value", "t1")),
                ImmutableList.of(
                    createStopTime(0, "t0", "s0", 2),
                    createStopTime(0, "t1", "s3", 5),
                    createStopTime(2, "t1", "s4", 9))))
        .containsExactly(new UnusableTripNotice(1, "t0"));
  }
}
