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
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.FastTravelBetweenStopsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class TripTravelSpeedValidatorTest {
  public static GtfsStopTime createStopTime(
      long csvRowNumber,
      String tripId,
      String stopId,
      int stopSequence,
      GtfsTime departureTime,
      GtfsTime arrivalTime) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .setDepartureTime(departureTime)
        .setArrivalTime(arrivalTime)
        .build();
  }

  private static GtfsStopTimeTableContainer createStopTimeTable(
      NoticeContainer noticeContainer, List<GtfsStopTime> entities) {
    return GtfsStopTimeTableContainer.forEntities(entities, noticeContainer);
  }

  private static GtfsTripTableContainer createTripTable(
      NoticeContainer noticeContainer, List<GtfsTrip> entities) {
    return GtfsTripTableContainer.forEntities(entities, noticeContainer);
  }

  private static GtfsTrip createTrip() {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(34)
        .setRouteId("route id")
        .setServiceId("service id")
        .setTripId("t1")
        .build();
  }

  private static GtfsStopTableContainer createStopTable(
      NoticeContainer noticeContainer, List<GtfsStop> entities) {
    return GtfsStopTableContainer.forEntities(entities, noticeContainer);
  }

  private static GtfsStop createStop(
      long csvRowNumber, String stopId, double stopLat, double stopLon) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopLat(stopLat)
        .setStopLon(stopLon)
        .setLocationType(4)
        .build();
  }

  @Test
  public void tripWithFastTravelContiguousStopsShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripTravelSpeedValidator underTest = new TripTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233D, -61.591997D),
                createStop(3, "s1", 16.251053D, -61.591241D),
                createStop(4, "s2", 16.242191D, -61.588554D)));

    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    4,
                    "t1",
                    "s0",
                    0,
                    GtfsTime.fromSecondsSinceMidnight(130),
                    GtfsTime.fromSecondsSinceMidnight(100)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(230),
                    GtfsTime.fromSecondsSinceMidnight(131)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(77000),
                    GtfsTime.fromSecondsSinceMidnight(30000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FastTravelBetweenStopsNotice("t1", 299.3383739629327D, Arrays.asList(0, 1)));
  }

  @Test
  public void tripWithFastTravelFarStopsShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripTravelSpeedValidator underTest = new TripTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233D, -61.591997D),
                createStop(3, "s1", 16.251053D, -61.591241D),
                createStop(4, "s2", 16.242191D, -61.588554D),
                createStop(5, "s3", 16.249092D, -61.494831D)));

    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    4,
                    "t1",
                    "s0",
                    0,
                    GtfsTime.fromSecondsSinceMidnight(3130),
                    GtfsTime.fromSecondsSinceMidnight(100)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(5200),
                    GtfsTime.fromSecondsSinceMidnight(5200)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    2,
                    GtfsTime.fromSecondsSinceMidnight(5200),
                    GtfsTime.fromSecondsSinceMidnight(5200)),
                createStopTime(
                    6,
                    "t1",
                    "s3",
                    3,
                    GtfsTime.fromSecondsSinceMidnight(5400),
                    GtfsTime.fromSecondsSinceMidnight(5300))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FastTravelBetweenStopsNotice("t1", 398.1887657929889D, Arrays.asList(1, 2, 3)));
  }

  @Test
  public void noFastTravelShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripTravelSpeedValidator underTest = new TripTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233D, -61.591997D),
                createStop(3, "s1", 16.251053D, -61.591241D),
                createStop(4, "s2", 16.242191D, -61.588554D)));

    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    4,
                    "t1",
                    "s0",
                    0,
                    GtfsTime.fromSecondsSinceMidnight(130),
                    GtfsTime.fromSecondsSinceMidnight(100)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(789),
                    GtfsTime.fromSecondsSinceMidnight(456)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(77000),
                    GtfsTime.fromSecondsSinceMidnight(30000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void fastTravelBetweenForMissingDepartureTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripTravelSpeedValidator underTest = new TripTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233D, -61.591997D),
                createStop(3, "s1", 16.251053D, -61.591241D),
                createStop(4, "s2", 16.242191D, -61.588554D)));

    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    4,
                    "t1",
                    "s0",
                    0,
                    GtfsTime.fromSecondsSinceMidnight(130),
                    GtfsTime.fromSecondsSinceMidnight(100)),
                createStopTime(5, "t1", "s1", 1, null, GtfsTime.fromSecondsSinceMidnight(131)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(77000),
                    GtfsTime.fromSecondsSinceMidnight(30000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FastTravelBetweenStopsNotice("t1", 299.3383739629327D, Arrays.asList(0, 1)));
  }

  @Test
  public void fastTravelBetweenForMissingArrivalTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TripTravelSpeedValidator underTest = new TripTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233D, -61.591997D),
                createStop(3, "s1", 16.251053D, -61.591241D),
                createStop(4, "s2", 16.242191D, -61.588554D)));

    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));

    underTest.stopTimeTable =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    4,
                    "t1",
                    "s0",
                    0,
                    GtfsTime.fromSecondsSinceMidnight(130),
                    GtfsTime.fromSecondsSinceMidnight(100)),
                createStopTime(5, "t1", "s1", 1, GtfsTime.fromSecondsSinceMidnight(230), null),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(77000),
                    GtfsTime.fromSecondsSinceMidnight(30000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FastTravelBetweenStopsNotice("t1", 299.3383739629327D, Arrays.asList(0, 1)));
  }
}
