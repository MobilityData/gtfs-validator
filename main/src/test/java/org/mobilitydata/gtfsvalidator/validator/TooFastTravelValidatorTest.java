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
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.TooFastTravelValidator.TooFastTravelNotice;

public class TooFastTravelValidatorTest {

  private static GtfsStop createStop(long csvRowNumber, String stopId, double stopLon) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopLat((double) 0)
        .setStopLon(stopLon)
        .setLocationType(4)
        .build();
  }

  private static GtfsStopTime createStopTime(
      long csvRowNumber,
      String stopId,
      int stopSequence,
      GtfsTime arrivalTime,
      GtfsTime departureTime) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId("t1")
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
        .build();
  }

  private static GtfsTrip createTrip() {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(34)
        .setRouteId("route id")
        .setServiceId("service id")
        .setTripId("t1")
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops, List<GtfsTrip> trips, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new TooFastTravelValidator(
            GtfsTripTableContainer.forEntities(trips, noticeContainer),
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void fastTravelContiguousStopsShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/473f265f1bc0404c8e4738bbe4977649
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.001),
                    createStop(3, "s1", 0.002),
                    createStop(4, "s2", 0.003)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(
                        5,
                        "s1",
                        1,
                        GtfsTime.fromSecondsSinceMidnight(7),
                        GtfsTime.fromSecondsSinceMidnight(9)),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(15),
                        GtfsTime.fromSecondsSinceMidnight(20)))))
        .containsExactly(new TooFastTravelNotice("t1", 200.15114352186376d, 0, 1));
  }

  @Test
  public void fastTravelFarStopsShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/d21cc8b1519d4f44f9ebd53858cda78d
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002),
                    createStop(5, "s3", 0.003)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(
                        5,
                        "s1",
                        1,
                        GtfsTime.fromSecondsSinceMidnight(10),
                        GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(
                        6,
                        "s2",
                        2,
                        GtfsTime.fromSecondsSinceMidnight(10),
                        GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(
                        6,
                        "s3",
                        3,
                        GtfsTime.fromSecondsSinceMidnight(11),
                        GtfsTime.fromSecondsSinceMidnight(13)))))
        .containsExactly(new TooFastTravelNotice("t1", 800.604574087455d, 1, 3));
  }

  @Test
  public void noFastTravelShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/c556bfa6ae2622166c594eed51f4b468
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.001),
                    createStop(3, "s1", 0.002),
                    createStop(4, "s2", 0.003)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(
                        5,
                        "s1",
                        1,
                        GtfsTime.fromSecondsSinceMidnight(28),
                        GtfsTime.fromSecondsSinceMidnight(44)),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(75),
                        GtfsTime.fromSecondsSinceMidnight(122)))))
        .isEmpty();
  }

  @Test
  public void fastTravelMissingArrivalTimeShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/2924c8c7b8ee4e52aec04fec166849b4
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(5, "s1", 1, null, GtfsTime.fromSecondsSinceMidnight(6)),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(7),
                        GtfsTime.fromSecondsSinceMidnight(12)))))
        .containsExactly(new TooFastTravelNotice("t1", 800.604574087455d, 0, 4));
  }

  @Test
  public void noFastTravelMissingArrivalTimeShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/e1a1095287677758598cb4e897b4f439
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(5, "s1", 1, null, GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(55),
                        GtfsTime.fromSecondsSinceMidnight(100)))))
        .isEmpty();
  }

  @Test
  public void fastTravelMissingDepartureTimeShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/dc1996244f2d2a86ababb8ae81b8eab0
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(5, "s1", 1, GtfsTime.fromSecondsSinceMidnight(6), null),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(7),
                        GtfsTime.fromSecondsSinceMidnight(12)))))
        .containsExactly(new TooFastTravelNotice("t1", 400.3022870437275, 0, 4));
  }

  @Test
  public void noFastTravelMissingDepartureTimeShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/633d17dade4955230f67f567f32365d9
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(5, "s1", 1, GtfsTime.fromSecondsSinceMidnight(10), null),
                    createStopTime(
                        6,
                        "s2",
                        4,
                        GtfsTime.fromSecondsSinceMidnight(15),
                        GtfsTime.fromSecondsSinceMidnight(20)))))
        .isEmpty();
  }

  @Test
  public void stopTimeWithArrivalBeforePreviousDepartureTimeShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/d88be566850d47ea60cbcab0e869052c
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002),
                    createStop(5, "s3", 0.003)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(
                        5,
                        "s1",
                        1,
                        GtfsTime.fromSecondsSinceMidnight(4),
                        GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(
                        6,
                        "s2",
                        2,
                        GtfsTime.fromSecondsSinceMidnight(10),
                        GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(
                        6,
                        "s3",
                        3,
                        GtfsTime.fromSecondsSinceMidnight(11),
                        GtfsTime.fromSecondsSinceMidnight(13)))))
        .isEmpty();
  }

  @Test
  public void fastTravelBetweenIncludingNonTimepointsShouldGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/583b53f2e46f02f0276877831cc7a54e
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002),
                    createStop(5, "s3", 0.003),
                    createStop(6, "s4", 0.004),
                    createStop(7, "s5", 0.005)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(3, "s1", 1, null, null),
                    createStopTime(4, "s2", 2, null, null),
                    createStopTime(
                        8,
                        "s3",
                        3,
                        GtfsTime.fromSecondsSinceMidnight(7),
                        GtfsTime.fromSecondsSinceMidnight(10)),
                    createStopTime(6, "s4", 4, null, null),
                    createStopTime(
                        7,
                        "s5",
                        5,
                        GtfsTime.fromSecondsSinceMidnight(20),
                        GtfsTime.fromSecondsSinceMidnight(28)))))
        .containsExactly(new TooFastTravelNotice("t1", 600.4534305655912, 0, 3));
  }

  @Test
  public void noFastTravelBetweenIncludingNonTimepointsShouldNotGenerateNotice() {
    // sample table descriptions available at:
    // https://gist.github.com/lionel-nj/b2fe94906679fe30ca2c444fa8c92ce0
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(2, "s0", 0.000),
                    createStop(3, "s1", 0.001),
                    createStop(4, "s2", 0.002),
                    createStop(5, "s3", 0.003),
                    createStop(6, "s4", 0.004),
                    createStop(7, "s5", 0.005)),
                ImmutableList.of(createTrip()),
                ImmutableList.of(
                    createStopTime(
                        4,
                        "s0",
                        0,
                        GtfsTime.fromSecondsSinceMidnight(0),
                        GtfsTime.fromSecondsSinceMidnight(5)),
                    createStopTime(3, "s1", 1, null, null),
                    createStopTime(4, "s2", 2, null, null),
                    createStopTime(
                        8,
                        "s3",
                        3,
                        GtfsTime.fromSecondsSinceMidnight(20),
                        GtfsTime.fromSecondsSinceMidnight(25)),
                    createStopTime(6, "s4", 4, null, null),
                    createStopTime(
                        7,
                        "s5",
                        5,
                        GtfsTime.fromSecondsSinceMidnight(39),
                        GtfsTime.fromSecondsSinceMidnight(44)))))
        .isEmpty();
  }
}
