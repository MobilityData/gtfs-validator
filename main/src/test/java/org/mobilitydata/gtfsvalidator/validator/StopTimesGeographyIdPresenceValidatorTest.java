/*
 * Copyright 2020 Google LLC, MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.ForbiddenGeographyIdNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;

public class StopTimesGeographyIdPresenceValidatorTest {

  @Test
  public void NoGeographyIdShouldGenerateMissingRequiredFieldNotice() {
    assertThat(validationNoticesFor(new GtfsStopTime.Builder().setCsvRowNumber(2).build()))
        .containsExactly(new MissingRequiredFieldNotice("stop_times.txt", 2, "stop_id"));
  }

  @Test
  public void OneGeographyIdShouldGenerateNothing() {
    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder().setCsvRowNumber(2).setStopId("stop_id").build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder().setCsvRowNumber(2).setLocationGroupId("id").build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder().setCsvRowNumber(2).setLocationId("id").build()))
        .isEmpty();
  }

  @Test
  public void MultipleGeographyIdShouldGenerateNotice() {
    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder()
                    .setStopId("stop_id")
                    .setLocationGroupId("location_group_id")
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(new ForbiddenGeographyIdNotice(2, "stop_id", "location_group_id", ""));

    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder()
                    .setStopId("stop_id")
                    .setLocationId("location_id")
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(new ForbiddenGeographyIdNotice(2, "stop_id", "", "location_id"));

    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder()
                    .setLocationGroupId("location_group_id")
                    .setLocationId("location_id")
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(new ForbiddenGeographyIdNotice(2, "", "location_group_id", "location_id"));

    assertThat(
            validationNoticesFor(
                new GtfsStopTime.Builder()
                    .setStopId("stop_id")
                    .setLocationGroupId("location_group_id")
                    .setLocationId("location_id")
                    .setCsvRowNumber(2)
                    .build()))
        .containsExactly(
            new ForbiddenGeographyIdNotice(2, "stop_id", "location_group_id", "location_id"));
  }

  private List<ValidationNotice> validationNoticesFor(GtfsStopTime entity) {
    StopTimesGeographyIdPresenceValidator validator = new StopTimesGeographyIdPresenceValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
