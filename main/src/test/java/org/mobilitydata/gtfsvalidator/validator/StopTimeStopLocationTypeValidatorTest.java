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
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.validator.StopTimeStopLocationTypeValidator.WrongStopTimeStopLocationTypeNotice;

@RunWith(JUnit4.class)
public class StopTimeStopLocationTypeValidatorTest {

  private static GtfsStopTime createStopTime(
      long csvRowNumber, String tripId, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTripId(tripId)
        .setStopId(stopId)
        .setStopSequence(stopSequence)
        .build();
  }

  private static GtfsStop createStop(
      long csvRowNumber, String stopId, GtfsLocationType locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setLocationType(locationType)
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsStopTime> stopTimes, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopTimeStopLocationTypeValidator(
            GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stopTimeRefersToStop_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "trip id value", "stop id value", 5),
                    createStopTime(5, "other trip id value", "other stop id value", 8)),
                ImmutableList.of(
                    createStop(88, "stop id value", GtfsLocationType.STOP),
                    createStop(103, "other stop id value", GtfsLocationType.STOP))))
        .isEmpty();
  }

  @Test
  public void wrongForeignKey_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "trip id value", "stop id value", 5),
                    createStopTime(5, "other trip id value", "wrong foreign key", 8)),
                ImmutableList.of(
                    createStop(88, "stop id value", GtfsLocationType.STOP),
                    createStop(103, "other stop id value", GtfsLocationType.STOP))))
        .isEmpty();
  }

  @Test
  public void stopTimesRefersNonStopsLocationType_generatesNotices() {
    WrongStopTimeStopLocationTypeNotice[] validationNotices = {
      new WrongStopTimeStopLocationTypeNotice(
          15, "trip id value", 8, "station id value", GtfsLocationType.STATION.name()),
      new WrongStopTimeStopLocationTypeNotice(
          10, "other trip id value", 55, "entrance id value", GtfsLocationType.ENTRANCE.name()),
      new WrongStopTimeStopLocationTypeNotice(
          12,
          "some trip id value",
          35,
          "generic node id value",
          GtfsLocationType.GENERIC_NODE.name()),
      new WrongStopTimeStopLocationTypeNotice(
          55,
          "another trip id value",
          18,
          "boarding area id value",
          GtfsLocationType.BOARDING_AREA.name()),
    };
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStopTime(1, "trip id value", "stop id value", 5),
                    createStopTime(15, "trip id value", "station id value", 8),
                    createStopTime(10, "other trip id value", "entrance id value", 55),
                    createStopTime(12, "some trip id value", "generic node id value", 35),
                    createStopTime(55, "another trip id value", "boarding area id value", 18)),
                ImmutableList.of(
                    createStop(88, "stop id value", GtfsLocationType.STOP),
                    createStop(77, "station id value", GtfsLocationType.STATION),
                    createStop(66, "entrance id value", GtfsLocationType.ENTRANCE),
                    createStop(147, "generic node id value", GtfsLocationType.GENERIC_NODE),
                    createStop(103, "boarding area id value", GtfsLocationType.BOARDING_AREA))))
        .containsExactlyElementsIn(validationNotices);
  }
}
