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
import org.mobilitydata.gtfsvalidator.notice.DuplicateFareRuleZoneIdFieldsNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRule;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;

public class DuplicateFareRuleZoneIdFieldsValidatorTest {
  private static GtfsFareRuleTableContainer createFareRuleTable(
      NoticeContainer noticeContainer, List<GtfsFareRule> entities) {
    return GtfsFareRuleTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsFareRule createFareRule(
      long csvRowNumber,
      String fareId,
      String routeId,
      String originId,
      String containsId,
      String destinationId) {
    return new GtfsFareRule.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setRouteId(routeId)
        .setFareId(fareId)
        .setOriginId(originId)
        .setContainsId(containsId)
        .setDestinationId(destinationId)
        .build();
  }

  @Test
  public void duplicateFareRuleZoneIdValuesShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateFareRuleZoneIdFieldsValidator underTest = new DuplicateFareRuleZoneIdFieldsValidator();
    underTest.fareRuleTable =
        createFareRuleTable(
            noticeContainer,
            ImmutableList.of(
                createFareRule(3, "fare id value", "route id value", "from id", "by id", "to id"),
                createFareRule(
                    99, "other fare id value", "route id value", "from id", "by id", "to id")));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new DuplicateFareRuleZoneIdFieldsNotice(99, "other fare id value", 3, "fare id value", SeverityLevel.ERROR));
  }

  @Test
  public void noDuplicateRowValueShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateFareRuleZoneIdFieldsValidator underTest = new DuplicateFareRuleZoneIdFieldsValidator();
    underTest.fareRuleTable =
        createFareRuleTable(
            noticeContainer,
            ImmutableList.of(
                createFareRule(3, "fare id value", "route id", "from id", "by id", "to id"),
                createFareRule(
                    99, "other fare id value", "route id", "other from id", "by id", "to id")));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void noDuplicateRowWithEmptyValuesValueShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateFareRuleZoneIdFieldsValidator underTest = new DuplicateFareRuleZoneIdFieldsValidator();
    underTest.fareRuleTable =
        createFareRuleTable(
            noticeContainer,
            ImmutableList.of(
                createFareRule(3, "fare id value", "route id", null, "by id", "to id"),
                createFareRule(
                    99, "other fare id value", "route id", "other from id", null, "to id")));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void duplicateFareRuleWithSomeEmptyValuesShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    DuplicateFareRuleZoneIdFieldsValidator underTest = new DuplicateFareRuleZoneIdFieldsValidator();
    underTest.fareRuleTable =
        createFareRuleTable(
            noticeContainer,
            ImmutableList.of(
                createFareRule(3, "fare id value", "route id", null, "by id", "to id"),
                createFareRule(99, "other fare id value", "route id", null, "by id", "to id")));
    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new DuplicateFareRuleZoneIdFieldsNotice(99, "other fare id value", 3, "fare id value", SeverityLevel.ERROR));
  }
}
