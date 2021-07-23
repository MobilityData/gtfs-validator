/*
 * Copyright 2020 MobilityData IO
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
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.validator.StopNameValidator.SameNameAndDescriptionForStopNotice;

@RunWith(JUnit4.class)
public class StopNameValidatorTest {

  private static GtfsStop createStop(
      int csvRowNumber,
      String stopId,
      @Nullable String stopName,
      @Nullable String stopDesc) {
    return new GtfsStop.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setStopId(stopId)
        .setStopName(stopName)
        .setStopDesc(stopDesc)
        .build();
  }

  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopNameValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void sameStopNameAndDesc_generatesNotice() {
    assertThat(
        generateNotices(createStop(4, "stop id value", "duplicate value", "duplicate value")))
        .containsExactly(
            new SameNameAndDescriptionForStopNotice(4, "stop id value", "duplicate value")
        );
  }

  @Test
  public void differentStopNameAndDesc_noNotice() {
    assertThat(
        generateNotices(createStop(4, "stop id value", "stop name value", "stop desc value")))
        .isEmpty();
  }

  @Test
  public void missingStopName_noNotice() {
    assertThat(
        generateNotices(createStop(4, "stop id value", null, "stop desc value")))
        .isEmpty();
  }

  @Test
  public void missingStopDesc_noNotice() {
    assertThat(
        generateNotices(createStop(4, "stop id value", "stop name value", null)))
        .isEmpty();
  }
}
