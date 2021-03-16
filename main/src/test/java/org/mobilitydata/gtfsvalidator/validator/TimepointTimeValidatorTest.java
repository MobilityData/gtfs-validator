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

import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StopTimeTimepointWithoutTimesNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class TimepointTimeValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsStopTime stopTime) {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator validator = new TimepointTimeValidator();
    validator.validate(stopTime, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void timepointWithNoTimeShouldGenerateNotices() {
    assertThat(
            generateNotices(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(1)
                    .setTripId("first trip id")
                    .setArrivalTime(null)
                    .setDepartureTime(null)
                    .setStopId("stop id")
                    .setStopSequence(2)
                    .setTimepoint(1)
                    .build()))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME),
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME));
  }

  @Test
  public void timepointWithBothTimesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(1)
                    .setTripId("first trip id")
                    .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
                    .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(580))
                    .setStopId("stop id")
                    .setStopSequence(2)
                    .setTimepoint(1)
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingDepartureTimeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(1)
                    .setTripId("first trip id")
                    .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
                    .setDepartureTime(null)
                    .setStopId("stop id")
                    .setStopSequence(2)
                    .setTimepoint(1)
                    .build()))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME));
  }

  @Test
  public void missingArrivalTimeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(1)
                    .setTripId("first trip id")
                    .setArrivalTime(null)
                    .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(450))
                    .setStopId("stop id")
                    .setStopSequence(2)
                    .setTimepoint(1)
                    .build()))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME));
  }

  @Test
  public void nonTimepointShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsStopTime.Builder()
                    .setCsvRowNumber(1)
                    .setTripId("first trip id")
                    .setArrivalTime(null)
                    .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(580))
                    .setStopId("stop id")
                    .setStopSequence(2)
                    .setTimepoint(0)
                    .build()))
        .isEmpty();
  }
}
