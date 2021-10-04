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
import org.mobilitydata.gtfsvalidator.validator.PathwayDanglingGenericNodeValidator.PathwayDanglingGenericNodeNotice;

@RunWith(JUnit4.class)
public final class PathwayDanglingGenericNodeValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsPathway> pathways, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayDanglingGenericNodeValidator(
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

  static GtfsStop createLocation(long csvRowNumber, GtfsLocationType locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(locationType.getNumber())
        .build();
  }

  static GtfsStop createChildLocation(
      long csvRowNumber, GtfsLocationType locationType, long parentRow) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(locationType.getNumber())
        .setParentStation(rowToStopId(parentRow))
        .build();
  }

  @Test
  public void danglingGenericNode_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, GENERIC_NODE, 10),
            createChildLocation(13, ENTRANCE, 10));
    assertThat(
            generateNotices(ImmutableList.of(createPathway(11, 13), createPathway(11, 12)), stops))
        .containsExactly(new PathwayDanglingGenericNodeNotice(stops.get(2)));
  }

  @Test
  public void danglingGenericNodeWithTwoPathways_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, GENERIC_NODE, 10),
            createChildLocation(13, ENTRANCE, 10));
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createPathway(11, 13), createPathway(11, 12), createPathway(12, 11)),
                stops))
        .containsExactly(new PathwayDanglingGenericNodeNotice(stops.get(2)));
  }

  @Test
  public void isolatedGenericNode_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, GENERIC_NODE, 10),
            createChildLocation(13, ENTRANCE, 10));
    assertThat(generateNotices(ImmutableList.of(createPathway(11, 13)), stops)).isEmpty();
  }

  @Test
  public void passThroughGenericNode_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, GENERIC_NODE, 10),
            createChildLocation(13, ENTRANCE, 10));
    assertThat(
            generateNotices(ImmutableList.of(createPathway(11, 12), createPathway(12, 13)), stops))
        .isEmpty();
  }

  @Test
  public void danglingPlatformAndEntrance_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, ENTRANCE, 10));
    assertThat(generateNotices(ImmutableList.of(createPathway(11, 12)), stops)).isEmpty();
  }
}
