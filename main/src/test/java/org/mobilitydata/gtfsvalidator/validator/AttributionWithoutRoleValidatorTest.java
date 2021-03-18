/*
 * Copyright 2021 Google LLC, MobilityData IO
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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAttribution;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionRole;
import org.mobilitydata.gtfsvalidator.validator.AttributionWithoutRoleValidator.AttributionWithoutRoleNotice;

@RunWith(JUnit4.class)
public class AttributionWithoutRoleValidatorTest {

  private static final Integer ASSIGNED = GtfsAttributionRole.ASSIGNED.getNumber();

  private static List<ValidationNotice> generateNotices(GtfsAttribution attribution) {
    NoticeContainer noticeContainer = new NoticeContainer();
    AttributionWithoutRoleValidator validator = new AttributionWithoutRoleValidator();
    validator.validate(attribution, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void attributionWithoutRoleShouldGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder()
                    .setCsvRowNumber(2)
                    .setAttributionId("attr-1")
                    .build()))
        .containsExactly(new AttributionWithoutRoleNotice(2, "attr-1"));
  }

  @Test
  public void attributionWithoutRoleAndNoIdShouldGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder().setCsvRowNumber(2).setAttributionId("").build()))
        .containsExactly(new AttributionWithoutRoleNotice(2, ""));
  }

  @Test
  public void attributionWithRoleShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder().setCsvRowNumber(2).setIsAuthority(ASSIGNED).build()))
        .isEmpty();
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder().setCsvRowNumber(2).setIsOperator(ASSIGNED).build()))
        .isEmpty();
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder().setCsvRowNumber(2).setIsProducer(ASSIGNED).build()))
        .isEmpty();
  }

  @Test
  public void attributionWithAllRolesShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                new GtfsAttribution.Builder()
                    .setCsvRowNumber(2)
                    .setIsAuthority(ASSIGNED)
                    .setIsOperator(ASSIGNED)
                    .setIsProducer(ASSIGNED)
                    .build()))
        .isEmpty();
  }
}
