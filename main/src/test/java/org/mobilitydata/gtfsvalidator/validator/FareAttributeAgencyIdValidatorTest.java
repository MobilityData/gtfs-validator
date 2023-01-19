/*
 * Copyright 2023 Google LLC
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
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;

@RunWith(JUnit4.class)
public final class FareAttributeAgencyIdValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsFareAttribute> fareAttributes, List<GtfsAgency> agencies) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FareAttributeAgencyIdValidator(
        GtfsFareAttributeTableContainer.forEntities(fareAttributes, noticeContainer),
        GtfsAgencyTableContainer.forEntities(agencies, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  private static String rowToAgencyId(int row) {
    return "agency" + row;
  }

  private static GtfsAgency createAgency(int row) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(row)
        .setAgencyId(rowToAgencyId(row))
        .setAgencyName("Agency " + row)
        .build();
  }

  @Test
  public void noAgency_yieldsNoNotice() {
    assertThat(generateNotices(
                   ImmutableList.of(new GtfsFareAttribute.Builder().setCsvRowNumber(2).build()),
                   ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void oneAgency_yieldsNoNotice() {
    assertThat(generateNotices(
                   ImmutableList.of(new GtfsFareAttribute.Builder().setCsvRowNumber(2).build()),
                   ImmutableList.of(createAgency(1))))
        .isEmpty();
  }

  @Test
  public void twoAgencies_yieldsNotice() {
    assertThat(generateNotices(
                   ImmutableList.of(new GtfsFareAttribute.Builder().setCsvRowNumber(2).build()),
                   ImmutableList.of(createAgency(1), createAgency(2))))
        .containsExactly(new MissingRequiredFieldNotice("fare_attributes.txt", 2, "agency_id"));
  }

  @Test
  public void twoAgenciesAndAgencyId_yieldsNoNotice() {
    assertThat(generateNotices(ImmutableList.of(new GtfsFareAttribute.Builder()
                                                    .setCsvRowNumber(2)
                                                    .setAgencyId(rowToAgencyId(1))
                                                    .build()),
                   ImmutableList.of(createAgency(1), createAgency(2))))
        .isEmpty();
  }
}
