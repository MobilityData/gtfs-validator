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
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTableLoader.STOP_ID_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.ARRIVAL_TIME_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.CONTINUOUS_DROP_OFF_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.CONTINUOUS_PICKUP_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.DEPARTURE_TIME_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.DROP_OFF_TYPE_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.PICKUP_TYPE_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.SHAPE_DIST_TRAVELED_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.STOP_HEADSIGN_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.STOP_SEQUENCE_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.TIMEPOINT_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader.TRIP_ID_FIELD_NAME;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.TimepointTimeValidator.MissingTimepointColumnNotice;
import org.mobilitydata.gtfsvalidator.validator.TimepointTimeValidator.StopTimeTimepointWithoutTimesNotice;

public class TimepointTimeValidatorTest {

  private static GtfsStopTimeTableContainer createTable(
      CsvHeader header, List<GtfsStopTime> stopTimes, NoticeContainer noticeContainer) {
    return GtfsStopTimeTableContainer.forHeaderAndEntities(header, stopTimes, noticeContainer);
  }

  private static List<ValidationNotice> generateNotices(
      CsvHeader header, List<GtfsStopTime> stopTimes) {
    NoticeContainer noticeContainer = new NoticeContainer();
    TimepointTimeValidator validator =
        new TimepointTimeValidator(createTable(header, stopTimes, noticeContainer));
    validator.validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static CsvHeader createLegacyHeader() {
    return new CsvHeader(
        new String[] {
          TRIP_ID_FIELD_NAME,
          ARRIVAL_TIME_FIELD_NAME,
          DEPARTURE_TIME_FIELD_NAME,
          STOP_ID_FIELD_NAME,
          STOP_SEQUENCE_FIELD_NAME,
          STOP_HEADSIGN_FIELD_NAME,
          PICKUP_TYPE_FIELD_NAME,
          DROP_OFF_TYPE_FIELD_NAME,
          CONTINUOUS_PICKUP_FIELD_NAME,
          CONTINUOUS_DROP_OFF_FIELD_NAME,
          SHAPE_DIST_TRAVELED_FIELD_NAME
        });
  }

  private static CsvHeader createHeaderWithTimepointColumn() {
    return new CsvHeader(
        new String[] {
          TRIP_ID_FIELD_NAME,
          ARRIVAL_TIME_FIELD_NAME,
          DEPARTURE_TIME_FIELD_NAME,
          STOP_ID_FIELD_NAME,
          STOP_SEQUENCE_FIELD_NAME,
          STOP_HEADSIGN_FIELD_NAME,
          PICKUP_TYPE_FIELD_NAME,
          DROP_OFF_TYPE_FIELD_NAME,
          CONTINUOUS_PICKUP_FIELD_NAME,
          CONTINUOUS_DROP_OFF_FIELD_NAME,
          SHAPE_DIST_TRAVELED_FIELD_NAME,
          TIMEPOINT_FIELD_NAME
        });
  }

  @Test
  public void noTimepointColumn_noTimeProvided_shouldGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(null)
            .setDepartureTime(null)
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint((Integer) null)
            .build());
    assertThat(generateNotices(createLegacyHeader(), stopTimes))
        .containsExactly(new MissingTimepointColumnNotice());
  }

  @Test
  public void noTimepointColumn_timesProvided_shouldNotGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
            .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(580))
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint((Integer) null)
            .build());
    assertThat(generateNotices(createLegacyHeader(), stopTimes))
        .containsExactly(new MissingTimepointColumnNotice());
  }

  @Test
  public void timepointWithNoTimeShouldGenerateNotices() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(null)
            .setDepartureTime(null)
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(1)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(1, "first trip id", 2, ARRIVAL_TIME_FIELD_NAME),
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, DEPARTURE_TIME_FIELD_NAME));
  }

  @Test
  public void timepointWithBothTimesShouldNotGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
            .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(580))
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(1)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes)).isEmpty();
  }

  @Test
  public void timepoint_missingDepartureTimeShouldGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
            .setDepartureTime(null)
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(1)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, DEPARTURE_TIME_FIELD_NAME));
  }

  @Test
  public void timepoint_missingArrivalTimeShouldGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(null)
            .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(450))
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(1)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes))
        .containsExactly(
            new StopTimeTimepointWithoutTimesNotice(
                1, "first trip id", 2, ARRIVAL_TIME_FIELD_NAME));
  }

  @Test
  public void nonTimepoint_noTimeProvided_shouldNotGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(null)
            .setDepartureTime(null)
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(0)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes)).isEmpty();
  }

  @Test
  public void nonTimepoint_timesProvided_shouldNotGenerateNotice() {
    List<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.add(
        new GtfsStopTime.Builder()
            .setCsvRowNumber(1)
            .setTripId("first trip id")
            .setArrivalTime(GtfsTime.fromSecondsSinceMidnight(450))
            .setDepartureTime(GtfsTime.fromSecondsSinceMidnight(580))
            .setStopId("stop id")
            .setStopSequence(2)
            .setTimepoint(0)
            .build());
    assertThat(generateNotices(createHeaderWithTimepointColumn(), stopTimes)).isEmpty();
  }
}
