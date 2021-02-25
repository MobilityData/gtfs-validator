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
import org.mobilitydata.gtfsvalidator.notice.ExcessiveTripTravelSpeedNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class ExcessiveTravelSpeedValidatorTest {
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
  public void fastTravelContiguousStopsShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/473f265f1bc0404c8e4738bbe4977649
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 0, 0.001),
                createStop(3, "s1", 0, 0.002),
                createStop(4, "s2", 0, 0.003)));

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
                    GtfsTime.fromSecondsSinceMidnight(0),
                    GtfsTime.fromSecondsSinceMidnight(5)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(7),
                    GtfsTime.fromSecondsSinceMidnight(9)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(15),
                    GtfsTime.fromSecondsSinceMidnight(20))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new ExcessiveTripTravelSpeedNotice("t1", 200.15114352186376d, 0, 1));
  }

  @Test
  public void fastTravelFarStopsShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/473f265f1bc0404c8e4738bbe4977649
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 0, 0.000),
                createStop(3, "s1", 0, 0.001),
                createStop(4, "s2", 0, 0.002),
                createStop(5, "s3", 0, 0.003)));

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
                    GtfsTime.fromSecondsSinceMidnight(0),
                    GtfsTime.fromSecondsSinceMidnight(5)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(10),
                    GtfsTime.fromSecondsSinceMidnight(10)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    2,
                    GtfsTime.fromSecondsSinceMidnight(10),
                    GtfsTime.fromSecondsSinceMidnight(10)),
                createStopTime(
                    6,
                    "t1",
                    "s3",
                    3,
                    GtfsTime.fromSecondsSinceMidnight(11),
                    GtfsTime.fromSecondsSinceMidnight(13))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new ExcessiveTripTravelSpeedNotice("t1", 800.604574087455d, 1, 3));
  }

  @Test
  public void noFastTravelShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/c556bfa6ae2622166c594eed51f4b468#file-tripwithfasttravelcontiguousstopsshouldgeneratenotice
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 0, 0.001),
                createStop(3, "s1", 0, 0.002),
                createStop(4, "s2", 0, 0.003)));

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
                    GtfsTime.fromSecondsSinceMidnight(0),
                    GtfsTime.fromSecondsSinceMidnight(5)),
                createStopTime(
                    5,
                    "t1",
                    "s1",
                    1,
                    GtfsTime.fromSecondsSinceMidnight(7),
                    GtfsTime.fromSecondsSinceMidnight(12)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(15),
                    GtfsTime.fromSecondsSinceMidnight(20))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices());
  }

  @Test
  public void fastTravelMissingArrivalTimeShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/2924c8c7b8ee4e52aec04fec166849b4
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 0, 0.000),
                createStop(3, "s1", 0, 0.001),
                createStop(4, "s2", 0, 0.002)));

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
                    GtfsTime.fromSecondsSinceMidnight(0),
                    GtfsTime.fromSecondsSinceMidnight(5)),
                createStopTime(5, "t1", "s1", 1, null, GtfsTime.fromSecondsSinceMidnight(6)),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(7),
                    GtfsTime.fromSecondsSinceMidnight(12))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new ExcessiveTripTravelSpeedNotice("t1", 800.604574087455d, 0, 4));
  }

  // FIXME
  @Test
  public void noFastTravelMissingArrivalTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233d, -61.591997d),
                createStop(3, "s1", 16.251053d, -61.591241d),
                createStop(4, "s2", 16.242191d, -61.588554d)));

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
                createStopTime(5, "t1", "s1", 1, GtfsTime.fromSecondsSinceMidnight(131), null),
                createStopTime(
                    6,
                    "t1",
                    "s2",
                    4,
                    GtfsTime.fromSecondsSinceMidnight(770000),
                    GtfsTime.fromSecondsSinceMidnight(390000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

//  // FIXME
//  @Test
//  public void fastTravelMissingDepartureTimeShouldGenerateNotice() {
//    NoticeContainer noticeContainer = new NoticeContainer();
//    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
//    underTest.stopTable =
//        createStopTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStop(2, "s0", 16.251233d, -61.591997d),
//                createStop(3, "s1", 16.251053d, -61.591241d),
//                createStop(4, "s2", 16.242191d, -61.588554d)));
//
//    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));
//
//    underTest.stopTimeTable =
//        createStopTimeTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStopTime(
//                    4,
//                    "t1",
//                    "s0",
//                    0,
//                    GtfsTime.fromSecondsSinceMidnight(130),
//                    GtfsTime.fromSecondsSinceMidnight(100)),
//                createStopTime(5, "t1", "s1", 1, null, GtfsTime.fromSecondsSinceMidnight(131)),
//                createStopTime(
//                    6,
//                    "t1",
//                    "s2",
//                    4,
//                    GtfsTime.fromSecondsSinceMidnight(360),
//                    GtfsTime.fromSecondsSinceMidnight(133))));
//
//    underTest.validate(noticeContainer);
//
//    assertThat(noticeContainer.getValidationNotices())
//        .containsExactly(new ExcessiveTripTravelSpeedNotice("t1", 1331.3543837137777d, 0, 4));
//  }

  // FIXME
  @Test
  public void noFastTravelMissingDepartureTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
    underTest.stopTable =
        createStopTable(
            noticeContainer,
            ImmutableList.of(
                createStop(2, "s0", 16.251233d, -61.591997d),
                createStop(3, "s1", 16.251053d, -61.591241d),
                createStop(4, "s2", 16.242191d, -61.588554d)));

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
                    GtfsTime.fromSecondsSinceMidnight(770000),
                    GtfsTime.fromSecondsSinceMidnight(390000))));

    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

//  // FIXME
//  @Test
//  public void stopTimeWithArrivalBeforePreviousDepartureTimeShouldNotGenerateNotice() {
//    NoticeContainer noticeContainer = new NoticeContainer();
//    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
//    underTest.stopTable =
//        createStopTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStop(2, "s0", 16.251233D, -61.591997D),
//                createStop(3, "s1", 16.251053D, -61.591241D),
//                createStop(4, "s2", 16.242191D, -61.588554D),
//                createStop(5, "s3", 16.249092D, -61.494831D)));
//
//    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));
//
//    underTest.stopTimeTable =
//        createStopTimeTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStopTime(
//                    4,
//                    "t1",
//                    "s0",
//                    0,
//                    GtfsTime.fromSecondsSinceMidnight(3130),
//                    GtfsTime.fromSecondsSinceMidnight(100)),
//                createStopTime(
//                    5,
//                    "t1",
//                    "s1",
//                    1,
//                    GtfsTime.fromSecondsSinceMidnight(5200),
//                    GtfsTime.fromSecondsSinceMidnight(3000)),
//                createStopTime(
//                    6,
//                    "t1",
//                    "s2",
//                    2,
//                    GtfsTime.fromSecondsSinceMidnight(5200),
//                    GtfsTime.fromSecondsSinceMidnight(5200)),
//                createStopTime(
//                    6,
//                    "t1",
//                    "s3",
//                    3,
//                    GtfsTime.fromSecondsSinceMidnight(5400),
//                    GtfsTime.fromSecondsSinceMidnight(5300))));
//
//    underTest.validate(noticeContainer);
//
//    assertThat(noticeContainer.getValidationNotices()).isEmpty();
//  }

  // FIXME
//  @Test
//  public void fastTravel() {
//    NoticeContainer noticeContainer = new NoticeContainer();
//    ExcessiveTravelSpeedValidator underTest = new ExcessiveTravelSpeedValidator();
//    underTest.stopTable =
//        createStopTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStop(2, "s0", 16.251233D, -61.591997D),
//                createStop(3, "s1", 16.251053D, -61.591241D),
//                createStop(4, "s2", 16.242191D, -61.588554D),
//                createStop(5, "s3", 16.249092D, -61.494831D)));
//
//    underTest.tripTable = createTripTable(noticeContainer, ImmutableList.of(createTrip()));
//
//    underTest.stopTimeTable =
//        createStopTimeTable(
//            noticeContainer,
//            ImmutableList.of(
//                createStopTime(
//                    4,
//                    "t1",
//                    "s0",
//                    0,
//                    GtfsTime.fromSecondsSinceMidnight(3130),
//                    GtfsTime.fromSecondsSinceMidnight(100)),
//                createStopTime(
//                    5,
//                    "t1",
//                    "s1",
//                    1,
//                    GtfsTime.fromSecondsSinceMidnight(5200),
//                    GtfsTime.fromSecondsSinceMidnight(3000)),
//                createStopTime(
//                    6,
//                    "t1",
//                    "s2",
//                    2,
//                    GtfsTime.fromSecondsSinceMidnight(5200),
//                    GtfsTime.fromSecondsSinceMidnight(5200)),
//                createStopTime(
//                    6,
//                    "t1",
//                    "s3",
//                    3,
//                    GtfsTime.fromSecondsSinceMidnight(5400),
//                    GtfsTime.fromSecondsSinceMidnight(5300))));
//
//    underTest.validate(noticeContainer);
//
//    assertThat(noticeContainer.getValidationNotices()).isEmpty();
//  }


  private static GtfsStopTime createStopTime(
      long csvRowNumber,
      String tripId,
      String stopId,
      int stopSequence,
      GtfsTime arrivalTime,
      GtfsTime departureTime) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
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
}
