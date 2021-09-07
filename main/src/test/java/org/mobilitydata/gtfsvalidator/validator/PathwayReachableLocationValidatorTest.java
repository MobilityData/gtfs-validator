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
import static org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional.BIDIRECTIONAL;
import static org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional.UNIDIRECTIONAL;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.PathwayReachableLocationValidator.PathwayUnreachableLocationNotice;

@RunWith(JUnit4.class)
public final class PathwayReachableLocationValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsPathway> pathways, List<GtfsStop> stops) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayReachableLocationValidator(
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

  static GtfsPathway createPathway(
      long fromStopRow, long toStopRow, GtfsPathwayIsBidirectional isBidirectional) {
    long pathwayRow = fromStopRow * 1000 + toStopRow;
    return new GtfsPathway.Builder()
        .setCsvRowNumber(pathwayRow)
        .setPathwayId(rowToPathwayId(pathwayRow))
        .setFromStopId(rowToStopId(fromStopRow))
        .setToStopId(rowToStopId(toStopRow))
        .setIsBidirectional(isBidirectional)
        .build();
  }

  static GtfsStop createLocation(long csvRowNumber, GtfsLocationType locationType) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(locationType)
        .setStopName(locationType.name() + csvRowNumber)
        .build();
  }

  static GtfsStop createChildLocation(
      long csvRowNumber, GtfsLocationType locationType, long parentRow) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(rowToStopId(csvRowNumber))
        .setLocationType(locationType)
        .setParentStation(rowToStopId(parentRow))
        .setStopName(locationType.name() + csvRowNumber)
        .build();
  }

  @Test
  public void unreachablePlatforms_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, STOP, 10),
            createChildLocation(13, STOP, 10),
            createChildLocation(14, ENTRANCE, 10));
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createPathway(11, 14, UNIDIRECTIONAL), createPathway(14, 12, UNIDIRECTIONAL)),
                stops))
        .containsExactly(
            new PathwayUnreachableLocationNotice(stops.get(1), false, true),
            new PathwayUnreachableLocationNotice(stops.get(2), true, false),
            new PathwayUnreachableLocationNotice(stops.get(3), false, false));
  }

  @Test
  public void unreachableGenericNodes_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, GENERIC_NODE, 10),
            createChildLocation(12, GENERIC_NODE, 10),
            createChildLocation(13, GENERIC_NODE, 10),
            createChildLocation(14, ENTRANCE, 10));
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createPathway(11, 14, UNIDIRECTIONAL), createPathway(14, 12, UNIDIRECTIONAL)),
                stops))
        .containsExactly(
            new PathwayUnreachableLocationNotice(stops.get(1), false, true),
            new PathwayUnreachableLocationNotice(stops.get(2), true, false),
            new PathwayUnreachableLocationNotice(stops.get(3), false, false));
  }

  @Test
  public void reachablePlatform_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, ENTRANCE, 10));
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createPathway(11, 12, UNIDIRECTIONAL), createPathway(12, 11, UNIDIRECTIONAL)),
                stops))
        .isEmpty();
  }

  @Test
  public void bidirectionalPathway_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, ENTRANCE, 10));
    assertThat(generateNotices(ImmutableList.of(createPathway(11, 12, BIDIRECTIONAL)), stops))
        .isEmpty();
  }

  @Test
  public void unidirectionalPathway_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, ENTRANCE, 10));
    assertThat(generateNotices(ImmutableList.of(createPathway(11, 12, UNIDIRECTIONAL)), stops))
        .containsExactly(new PathwayUnreachableLocationNotice(stops.get(1), false, true));
  }

  @Test
  public void reachableBoardingAreas_yieldsNoNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, BOARDING_AREA, 11),
            createChildLocation(13, BOARDING_AREA, 11),
            createChildLocation(14, ENTRANCE, 10));
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createPathway(12, 14, BIDIRECTIONAL), createPathway(13, 14, BIDIRECTIONAL)),
                stops))
        .isEmpty();
  }

  @Test
  public void unreachableBoardingArea_yieldsNotice() {
    List<GtfsStop> stops =
        ImmutableList.of(
            createLocation(10, STATION),
            createChildLocation(11, STOP, 10),
            createChildLocation(12, BOARDING_AREA, 11),
            createChildLocation(13, BOARDING_AREA, 11),
            createChildLocation(14, ENTRANCE, 10));
    assertThat(generateNotices(ImmutableList.of(createPathway(12, 14, BIDIRECTIONAL)), stops))
        .containsExactly(new PathwayUnreachableLocationNotice(stops.get(3), false, false));
  }
}
