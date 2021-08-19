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
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.PathwayStopsConsistencyValidator.LocationTypeStationForStopOnPathwayNotice;

@RunWith(JUnit4.class)
public class PathwayStopsConsistencyValidatorTest {

  private static List<ValidationNotice> generateNotices(
      ImmutableList<GtfsStop> stops, ImmutableList<GtfsPathway> pathways) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayStopsConsistencyValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsStop createStop(
      String stopId, String levelId, long csvRowNumber, GtfsLocationType locationType) {
    return new GtfsStop.Builder()
        .setStopId(stopId)
        .setLevelId(levelId)
        .setCsvRowNumber(csvRowNumber)
        .setLocationType(locationType)
        .build();
  }

  private static GtfsPathway createPathway(
      String pathwayId, String fromStopId, String toStopId, long csvRowNumber) {
    return new GtfsPathway.Builder()
        .setPathwayId(pathwayId)
        .setFromStopId(fromStopId)
        .setCsvRowNumber(csvRowNumber)
        .setToStopId(toStopId)
        .build();
  }

  @Test
  public void validLocationType_yieldsZeroNotices() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop("stop id", "level 0", 4, GtfsLocationType.STOP),
                    createStop("entrance id", "level 1", 5, GtfsLocationType.ENTRANCE),
                    createStop("generic node id", "level 2", 6, GtfsLocationType.GENERIC_NODE),
                    createStop("boarding area id", "level 3", 7, GtfsLocationType.BOARDING_AREA)),
                ImmutableList.of(
                    createPathway("first pathway id value", "stop id", "entrance id", 33),
                    createPathway(
                        "second pathway id value", "generic node id", "boarding area id", 66))))
        .isEmpty();
  }

  @Test
  public void locationTypeStation_yieldsNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop("from stop id", "level 0", 3, GtfsLocationType.STATION),
                    createStop("to stop id", "level 1", 4, GtfsLocationType.STATION)),
                ImmutableList.of(
                    createPathway("pathway id value", "from stop id", "to stop id", 33))))
        .containsExactly(
            new LocationTypeStationForStopOnPathwayNotice(
                "pathway id value", 33, "from stop id", 3, "from_stop_id"),
            new LocationTypeStationForStopOnPathwayNotice(
                "pathway id value", 33, "to stop id", 4, "to_stop_id"));
  }
}
