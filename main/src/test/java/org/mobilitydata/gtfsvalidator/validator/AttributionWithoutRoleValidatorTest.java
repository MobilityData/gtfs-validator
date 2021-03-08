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
import org.mobilitydata.gtfsvalidator.notice.AttributionWithoutRoleNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAttribution;
import org.mobilitydata.gtfsvalidator.table.GtfsAttributionTableContainer;

public class AttributionWithoutRoleValidatorTest {

  private static GtfsAttributionTableContainer createAttributionTable(
      NoticeContainer noticeContainer, List<GtfsAttribution> entities) {
    return GtfsAttributionTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsAttribution createAttribution(
      long csvRowNumber, Integer isProducer, Integer isAuthority, Integer isOperator) {
    return new GtfsAttribution.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setOrganizationName("organization name value")
        .setIsProducer(isProducer)
        .setIsAuthority(isAuthority)
        .setIsOperator(isOperator)
        .build();
  }

  @Test
  public void attributionWithoutRoleShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AttributionWithoutRoleValidator underTest = new AttributionWithoutRoleValidator();

    underTest.attributionTable =
        createAttributionTable(
            noticeContainer,
            ImmutableList.of(
                createAttribution(3, 0, 0, 0), createAttribution(8, null, null, null), createAttribution(13, 5, 0, 0)));
    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new AttributionWithoutRoleNotice[] {
              new AttributionWithoutRoleNotice(3), new AttributionWithoutRoleNotice(8),  new AttributionWithoutRoleNotice(13)
            });
  }

  @Test
  public void attributionWithRoleShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AttributionWithoutRoleValidator underTest = new AttributionWithoutRoleValidator();

    underTest.attributionTable =
        createAttributionTable(
            noticeContainer,
            ImmutableList.of(createAttribution(3, 1, 0, 0), createAttribution(8, null, 1, null)));
    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
