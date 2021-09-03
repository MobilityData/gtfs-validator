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
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.MissingLevelIdValidator.MissingLevelIdNotice;

@RunWith(JUnit4.class)
public class MissingLevelIdValidatorTest {

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops, List<GtfsPathway> pathways) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingLevelIdValidator(
            GtfsPathwayTableContainer.forEntities(pathways, noticeContainer),
            GtfsStopTableContainer.forEntities(stops, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static GtfsStop createStop(long csvRowNumber, String levelId) {
    return new GtfsStop.Builder()
        .setStopId(toStopId(csvRowNumber))
        .setCsvRowNumber(csvRowNumber)
        .setLevelId(levelId)
        .build();
  }

  private static String toStopId(long csvRowNumber) {
    return String.format("stop %s", 2 * csvRowNumber + 100);
  }

  private static GtfsPathway createPathway(long csvRowNumber, GtfsPathwayMode pathwayMode) {
    return new GtfsPathway.Builder()
        .setPathwayId(toPathwayId(pathwayMode, csvRowNumber))
        .setCsvRowNumber(csvRowNumber)
        .setFromStopId(toStopId(csvRowNumber - 1))
        .setToStopId(toStopId(csvRowNumber + 1))
        .setPathwayMode(pathwayMode)
        .build();
  }

  private static String toPathwayId(GtfsPathwayMode pathwayMode, long csvRowNumber) {
    return pathwayMode.toString() + csvRowNumber;
  }

  @Test
  public void elevator_noLevelId_reportStopOnlyOnce() {
    ImmutableList<GtfsStop> stops =
        ImmutableList.of(createStop(2, null), createStop(4, null), createStop(6, null));
    assertThat(
            generateNotices(
                stops,
                ImmutableList.of(
                    createPathway(5, GtfsPathwayMode.ELEVATOR),
                    createPathway(3, GtfsPathwayMode.ELEVATOR))))
        .containsExactly(
            new MissingLevelIdNotice(stops.get(0)),
            new MissingLevelIdNotice(stops.get(1)),
            new MissingLevelIdNotice(stops.get(2)));
  }

  @Test
  public void elevator_noLevelId_yieldsNotice() {
    ImmutableList<GtfsStop> stops = ImmutableList.of(createStop(4, null), createStop(6, null));
    assertThat(generateNotices(stops, ImmutableList.of(createPathway(5, GtfsPathwayMode.ELEVATOR))))
        .containsExactly(
            new MissingLevelIdNotice(stops.get(0)), new MissingLevelIdNotice(stops.get(1)));
  }

  @Test
  public void elevator_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(3, "level id value"), createStop(4, "other level id value")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.ELEVATOR))))
        .isEmpty();
  }

  @Test
  public void walkway_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.WALKWAY))))
        .isEmpty();
  }

  @Test
  public void walkway_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.WALKWAY))))
        .isEmpty();
  }

  @Test
  public void stairs_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.STAIRS))))
        .isEmpty();
  }

  @Test
  public void stairs_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.STAIRS))))
        .isEmpty();
  }

  @Test
  public void movingSidewalks_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.MOVING_SIDEWALK))))
        .isEmpty();
  }

  @Test
  public void movingSidewalks_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.MOVING_SIDEWALK))))
        .isEmpty();
  }

  @Test
  public void escalator_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.ESCALATOR))))
        .isEmpty();
  }

  @Test
  public void escalator_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.ESCALATOR))))
        .isEmpty();
  }

  @Test
  public void fareGate_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.FARE_GATE))))
        .isEmpty();
  }

  @Test
  public void fareGate_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.FARE_GATE))))
        .isEmpty();
  }

  @Test
  public void exitGate_noLevelId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, null), createStop(6, null)),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.EXIT_GATE))))
        .isEmpty();
  }

  @Test
  public void exitGate_levelIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(4, "level id"), createStop(6, "level id")),
                ImmutableList.of(createPathway(5, GtfsPathwayMode.EXIT_GATE))))
        .isEmpty();
  }
}
