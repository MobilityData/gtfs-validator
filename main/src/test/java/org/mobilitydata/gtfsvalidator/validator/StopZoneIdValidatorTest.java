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
import org.mobilitydata.gtfsvalidator.table.GtfsFareRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.validator.StopZoneIdValidator.StopWithoutZoneIdNotice;

@RunWith(JUnit4.class)
public class StopZoneIdValidatorTest {

  private static GtfsStop createStop(
      long csvRowNumber, GtfsLocationType locationType, String zoneId) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(toLocationId(locationType, csvRowNumber))
        .setLocationType(locationType)
        .setZoneId(zoneId)
        .build();
  }

  private static GtfsFareRule createFareRule(long csvRowNumber) {
    return new GtfsFareRule.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFareId(toFareRuleId(csvRowNumber))
        .build();
  }

  private static String toLocationId(GtfsLocationType locationType, long csvRowNumber) {
    return locationType.toString() + csvRowNumber;
  }

  private static String toFareRuleId(long csvRowNumber) {
    return String.format("fare rule id %s", csvRowNumber);
  }

  private static List<ValidationNotice> generateNotices(
      List<GtfsStop> stops, List<GtfsFareRule> fareRules) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopZoneIdValidator(
            GtfsStopTableContainer.forEntities(stops, noticeContainer),
            GtfsFareRuleTableContainer.forEntities(fareRules, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void stop_zoneIdNotProvided_yieldsNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.STOP, null)),
                ImmutableList.of(createFareRule(5))))
        .containsExactly(new StopWithoutZoneIdNotice(createStop(3, GtfsLocationType.STOP, null), 3));
  }

  @Test
  public void stop_zoneIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.STOP, "zone id value")),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void station_zoneIdNotProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.STATION, null)),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void station_zoneIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.STATION, "zone id value")),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void entrance_zoneIdNotProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.ENTRANCE, null)),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void entrance_zoneIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.ENTRANCE, "zone id value")),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void genericNode_zoneIdNotProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.GENERIC_NODE, null)),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void genericNode_zoneIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.GENERIC_NODE, "zone id value")),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void boardingArea_zoneIdNotProvided_yieldsNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.BOARDING_AREA, null)),
                ImmutableList.of(createFareRule(5))))
        .containsExactly(
            new StopWithoutZoneIdNotice(createStop(3, GtfsLocationType.BOARDING_AREA, null), 3));
  }

  @Test
  public void boardingArea_zoneIdProvided_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(createStop(3, GtfsLocationType.BOARDING_AREA, "zone id value")),
                ImmutableList.of(createFareRule(5))))
        .isEmpty();
  }

  @Test
  public void emptyFareRule_allStopLocation_noZoneId_noNotice() {
    assertThat(
            generateNotices(
                ImmutableList.of(
                    createStop(4, GtfsLocationType.STOP, null),
                    createStop(6, GtfsLocationType.STATION, null),
                    createStop(7, GtfsLocationType.ENTRANCE, null),
                    createStop(10, GtfsLocationType.GENERIC_NODE, null),
                    createStop(3, GtfsLocationType.BOARDING_AREA, null)),
                ImmutableList.of()))
        .isEmpty();
  }
}
