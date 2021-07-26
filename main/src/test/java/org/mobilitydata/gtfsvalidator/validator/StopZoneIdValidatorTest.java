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

  private static GtfsStop createStop(long csvRowNumber, GtfsLocationType locationType,
      String zoneId, String stopId) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setLocationType(locationType)
        .setZoneId(zoneId)
        .build();
  }

  private static GtfsFareRule createFareRule(long csvRowNumber, String fareId) {
    return new GtfsFareRule.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFareId(fareId)
        .build();
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
  public void emptyFareRuleNoZoneId_noNotice() {
    assertThat(
        generateNotices(
            ImmutableList.of(createStop(3, GtfsLocationType.BOARDING_AREA, null, "stop id value")),
            ImmutableList.of()
        )).isEmpty();
  }

  @Test
  public void emptyFareRuleZoneId_noNotice() {
    assertThat(
        generateNotices(
            ImmutableList.of(createStop(3, GtfsLocationType.BOARDING_AREA, "zone id value",
                "stop id value")),
            ImmutableList.of()
        )).isEmpty();
  }

  @Test
  public void fareRuleProvidedNoZoneId_generatesNotices() {
    StopWithoutZoneIdNotice[] validationNotices = {
        new StopWithoutZoneIdNotice("stop id value", 3),
        new StopWithoutZoneIdNotice("other stop id value", 5)
    };
    assertThat(
        generateNotices(
            ImmutableList.of(
                createStop(3, GtfsLocationType.BOARDING_AREA, null, "stop id value"),
                createStop(5, GtfsLocationType.GENERIC_NODE, null, "other stop id value")),
            ImmutableList.of(createFareRule(5, "fare id value"))
        )).containsExactlyElementsIn(validationNotices);
  }

  @Test
  public void fareRuleProvidedZoneId_noNotice() {
    assertThat(
        generateNotices(
            ImmutableList.of(
                createStop(3, GtfsLocationType.BOARDING_AREA, "zone id value", "stop id value"),
                createStop(5, GtfsLocationType.GENERIC_NODE, "other zone id value",
                    "other stop id value")),
            ImmutableList.of(createFareRule(5, "fare id value"))
        )).isEmpty();
  }

  @Test
  public void fareRuleProvided_stopLocationEqualsStop_noNotice() {
    assertThat(
        generateNotices(
            ImmutableList.of(
                createStop(3, GtfsLocationType.STOP, "zone id value", "stop id value"),
                createStop(5, GtfsLocationType.STOP, "other zone id value",
                    "other stop id value")),
            ImmutableList.of(createFareRule(5, "fare id value"))
        )).isEmpty();
  }

  @Test
  public void fareRuleProvided_stopLocationEqualsEntrance_noNotice() {
    assertThat(
        generateNotices(
            ImmutableList.of(
                createStop(3, GtfsLocationType.ENTRANCE, "zone id value", "stop id value"),
                createStop(5, GtfsLocationType.ENTRANCE, "other zone id value",
                    "other stop id value")),
            ImmutableList.of(createFareRule(5, "fare id value"))
        )).isEmpty();
  }
}
