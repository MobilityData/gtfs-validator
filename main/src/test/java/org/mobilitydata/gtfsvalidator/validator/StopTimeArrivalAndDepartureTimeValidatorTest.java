/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithArrivalBeforePreviousDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithDepartureBeforeArrivalTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.StopTimeWithOnlyArrivalOrDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class StopTimeArrivalAndDepartureTimeValidatorTest {
  public static GtfsStopTime createStopTime(
      long csvRowNumber,
      String tripId,
      GtfsTime arrivalTime,
      GtfsTime departureTime,
      String stopId,
      int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .build();
  }

  private static GtfsStopTimeTableContainer createStopTimeTable(
      NoticeContainer noticeContainer, List<GtfsStopTime> entities) {
    return GtfsStopTimeTableContainer.forEntities(entities, noticeContainer);
  }

  @Test
  public void departureTimeAndArrivalTimeNotProvidedShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTimeArrivalAndDepartureTimeValidator underTest =
        new StopTimeArrivalAndDepartureTimeValidator();
    underTest.table =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(createStopTime(0, "first trip id", null, null, "stop id", 2)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void stopTimeWithArrivalBeforePreviousDepartureTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTimeArrivalAndDepartureTimeValidator underTest =
        new StopTimeArrivalAndDepartureTimeValidator();
    underTest.table =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    1,
                    "first trip id",
                    GtfsTime.fromSecondsSinceMidnight(340), // arrival time
                    GtfsTime.fromSecondsSinceMidnight(518), // departure time
                    "stop id",
                    2),
                createStopTime(
                    2,
                    "first trip id",
                    GtfsTime.fromSecondsSinceMidnight(420), // arrival time
                    GtfsTime.fromSecondsSinceMidnight(747), // departure time
                    "stop id",
                    3)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeWithArrivalBeforePreviousDepartureTimeNotice(
                2,
                1,
                "first trip id",
                GtfsTime.fromSecondsSinceMidnight(420),
                GtfsTime.fromSecondsSinceMidnight(518)));
  }

  @Test
  public void stopTimeWithArrivalAfterPreviousDepartureTimeShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTimeArrivalAndDepartureTimeValidator underTest =
        new StopTimeArrivalAndDepartureTimeValidator();
    underTest.table =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    1,
                    "first trip id",
                    GtfsTime.fromSecondsSinceMidnight(340), // arrival time
                    GtfsTime.fromSecondsSinceMidnight(420), // departure time
                    "stop id",
                    2),
                createStopTime(
                    2,
                    "first trip id",
                    GtfsTime.fromSecondsSinceMidnight(518), // arrival time
                    GtfsTime.fromSecondsSinceMidnight(747), // departure time
                    "stop id",
                    3)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void missingArrivalTimeShouldGenerateNoticeIfDepartureTimeIsProvided() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTimeArrivalAndDepartureTimeValidator underTest =
        new StopTimeArrivalAndDepartureTimeValidator();
    underTest.table =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    1,
                    "first trip id",
                    null,
                    GtfsTime.fromSecondsSinceMidnight(518), // departure time
                    "stop id",
                    2)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeWithOnlyArrivalOrDepartureTimeNotice(
                1, "first trip id", 2, "departure_time"));
  }

  @Test
  public void missingDepartureTimeShouldGenerateNoticeIfArrivalTimeIsProvided() {
    NoticeContainer noticeContainer = new NoticeContainer();
    StopTimeArrivalAndDepartureTimeValidator underTest =
        new StopTimeArrivalAndDepartureTimeValidator();
    underTest.table =
        createStopTimeTable(
            noticeContainer,
            ImmutableList.of(
                createStopTime(
                    1,
                    "first trip id",
                    GtfsTime.fromSecondsSinceMidnight(518), // arrival time
                    null,
                    "stop id",
                    2)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeWithOnlyArrivalOrDepartureTimeNotice(
                1, "first trip id", 2, "arrival_time"));
  }
}
