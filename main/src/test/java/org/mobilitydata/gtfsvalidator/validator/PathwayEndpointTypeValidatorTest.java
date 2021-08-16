/*
 * Copyright 2021 Google LLC
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
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.BOARDING_AREA;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.ENTRANCE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.GENERIC_NODE;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STATION;
import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.STOP;

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
import org.mobilitydata.gtfsvalidator.validator.PathwayEndpointTypeValidator.PathwayToPlatformWithBoardingAreasNotice;
import org.mobilitydata.gtfsvalidator.validator.PathwayEndpointTypeValidator.PathwayWrongEndpointTypeNotice;

@RunWith(JUnit4.class)
public class PathwayEndpointTypeValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsPathway> pathways, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayEndpointTypeValidator(
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  static String rowToStopId(long csvRowNumber) {
    return "stop" + csvRowNumber;
  }

  static String rowToPathwayId(long csvRowNumber) {
    return "pathway" + csvRowNumber;
  }

  static GtfsPathway createPathway(long fromStopRow, long toStopRow) {
    long row = fromStopRow * 1000 + toStopRow;
    return new GtfsPathway.Builder()
        .setCsvRowNumber(row)
        .setPathwayId(rowToPathwayId(row))
        .setFromStopId(rowToStopId(fromStopRow))
        .setToStopId(rowToStopId(toStopRow))
        .build();
  }

  static GtfsStop createStop(long csvRowNumber, GtfsLocationType locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(locationType)
        .build();
  }

  static GtfsStop createBoardingArea(long csvRowNumber, long parentRow) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(BOARDING_AREA)
        .setParentStation(rowToStopId(parentRow))
        .build();
  }

  @Test
  public void platformEndpoints_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createPathway(2, 3)),
                ImmutableList.of(createStop(2, STOP), createStop(3, STOP))))
        .isEmpty();
  }

  @Test
  public void entranceEndpoints_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createPathway(2, 3)),
                ImmutableList.of(createStop(2, ENTRANCE), createStop(3, ENTRANCE))))
        .isEmpty();
  }

  @Test
  public void genericNodeEndpoints_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createPathway(2, 3)),
                ImmutableList.of(createStop(2, GENERIC_NODE), createStop(3, GENERIC_NODE))))
        .isEmpty();
  }

  @Test
  public void boardingAreaEndpoints_yieldsNoNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createPathway(2, 3)),
                ImmutableList.of(createStop(2, BOARDING_AREA), createStop(3, BOARDING_AREA))))
        .isEmpty();
  }

  @Test
  public void stationEndpoints_yieldsNotices() {
    GtfsPathway pathway = createPathway(2, 3);
    assertThat(
            generateNotices(
                ImmutableList.of(pathway),
                ImmutableList.of(createStop(2, STATION), createStop(3, STATION))))
        .containsExactly(
            new PathwayWrongEndpointTypeNotice(pathway, "from_stop_id", pathway.fromStopId()),
            new PathwayWrongEndpointTypeNotice(pathway, "to_stop_id", pathway.toStopId()));
  }

  @Test
  public void platformWithBoardingAreasEndpoints_yieldsNotices() {
    GtfsPathway pathway = createPathway(2, 3);
    assertThat(
            generateNotices(
                ImmutableList.of(pathway),
                ImmutableList.of(
                    createStop(2, STOP),
                    createStop(3, STOP),
                    createBoardingArea(22, 2),
                    createBoardingArea(33, 3))))
        .containsExactly(
            new PathwayToPlatformWithBoardingAreasNotice(
                pathway, "from_stop_id", pathway.fromStopId()),
            new PathwayToPlatformWithBoardingAreasNotice(
                pathway, "to_stop_id", pathway.toStopId()));
  }
}
