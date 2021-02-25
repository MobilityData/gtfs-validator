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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.UnusedTripNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class TripUsageValidatorTest {

  private static GtfsTripTableContainer createTripTable(
      NoticeContainer noticeContainer, List<GtfsTrip> entities) {
    return GtfsTripTableContainer.forEntities(entities, noticeContainer);
  }

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
      long csvRowNumber, String tripId, String time, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setArrivalTime(GtfsTime.fromString(time))
        .setDepartureTime(GtfsTime.fromString(time))
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .build();
  }

  private static GtfsStopTimeTableContainer createStopTimeTable(
      String[] tripIds, String[] stopIds, String[][] times, NoticeContainer noticeContainer) {
    Preconditions.checkArgument(
        tripIds.length == times.length, "tripIds.length must be equal to times.length");

    ArrayList<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.ensureCapacity(tripIds.length * stopIds.length);
    for (int i = 0; i < tripIds.length; ++i) {
      Preconditions.checkArgument(
          stopIds.length == times[i].length, "stopIds.length must be equal to times[%d].length", i);
      for (int j = 0; j < stopIds.length; ++j) {
        stopTimes.add(createStopTime(stopTimes.size() + 1, tripIds[i], times[i][j], stopIds[j], j));
      }
    }

    return GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
  }

  @Test
  public void allTripsUsedShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripUsageValidator underTest = new TripUsageValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(
                createTrip(1, "route id value", "service id value", "t0"),
                createTrip(3, "route id value", "service id value", "t1")));
    underTest.stopTimeTable =
        createStopTimeTable(
            new String[] {"t0", "t1"},
            new String[] {"s0", "s1"},
            new String[][] {
              new String[] {"08:00:00", "09:00:00"}, new String[] {"10:00:00", "11:00:00"},
            },
            noticeContainer);

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void unusedTripShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripUsageValidator underTest = new TripUsageValidator();

    underTest.tripTable =
        createTripTable(
            noticeContainer,
            ImmutableList.of(
                createTrip(1, "route id value", "service id value", "used trip id value"),
                createTrip(3, "route id value", "service id value", "unused trip id value")));
    underTest.stopTimeTable =
        createStopTimeTable(
            new String[] {"used trip id value"},
            new String[] {"s0", "s1"},
            new String[][] {
              new String[] {"08:00:00", "09:00:00"},
            },
            noticeContainer);

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new UnusedTripNotice("unused trip id value", 3, SeverityLevel.WARNING));
  }
}
