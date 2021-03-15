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

import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTimeTimepointWithoutTimesNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class TimepointTimeValidatorTest {

  private static GtfsStopTime createStopTime(
      long csvRowNumber,
      String tripId,
      GtfsTime arrivalTime,
      GtfsTime departureTime,
      String stopId,
      int stopSequence,
      int timepoint) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setArrivalTime(arrivalTime)
        .setDepartureTime(departureTime)
        .setStopSequence(stopSequence)
        .setStopId(stopId)
        .setTimepoint(timepoint)
        .build();
  }

  @Test
  public void timepointWithNoTimeShouldGenerateNotices() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator underTest = new TimepointTimeValidator();

    underTest.validate(
        createStopTime(1, "first trip id", null, null, "stop id", 2, 1), noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1,
                "first trip id",
                2,
                String.format(
                    "%s and %s",
                    GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME,
                    GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME)));
  }

  @Test
  public void timepointWithBothTimesShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator underTest = new TimepointTimeValidator();

    underTest.validate(
        createStopTime(
            1,
            "first trip id",
            GtfsTime.fromSecondsSinceMidnight(518),
            GtfsTime.fromSecondsSinceMidnight(820),
            "stop id",
            2,
            1),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void missingDepartureTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator underTest = new TimepointTimeValidator();

    underTest.validate(
        createStopTime(
            1, "first trip id", GtfsTime.fromSecondsSinceMidnight(518), null, "stop id", 2, 1),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME));
  }

  @Test
  public void missingArrivalTimeShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator underTest = new TimepointTimeValidator();

    underTest.validate(
        createStopTime(
            1, "first trip id", null, GtfsTime.fromSecondsSinceMidnight(518), "stop id", 2, 1),
        noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME));
  }
}
