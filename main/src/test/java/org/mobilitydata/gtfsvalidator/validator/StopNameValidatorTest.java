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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.validator.StopNameValidator.SameNameAndDescriptionForStopNotice;
import org.mobilitydata.gtfsvalidator.validator.StopNameValidator.StopNameInvalidCharacterNotice;

@RunWith(JUnit4.class)
public class StopNameValidatorTest {

  private static List<ValidationNotice> generateNotices(GtfsStop stop) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new StopNameValidator().validate(stop, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void sameStopNameAndDesc_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STOP)
                    .setStopName("duplicate value")
                    .setStopDesc("duplicate value")
                    .build()))
        .containsExactly(
            new SameNameAndDescriptionForStopNotice(4, "stop id value", "duplicate value"));
  }

  @Test
  public void differentStopNameAndDesc_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STOP)
                    .setStopName("stop name value")
                    .setStopDesc("stop desc value")
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingStopNameForStop_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STOP)
                    .setStopName(null)
                    .build()))
        .containsExactly(
            new StopNameValidator.MissingStopNameNotice(4, "stop id value", GtfsLocationType.STOP));
  }

  @Test
  public void missingStopNameForStation_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STATION)
                    .setStopName(null)
                    .build()))
        .containsExactly(
            new StopNameValidator.MissingStopNameNotice(
                4, "stop id value", GtfsLocationType.STATION));
  }

  @Test
  public void missingStopNameForEntrance_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.ENTRANCE)
                    .setStopName(null)
                    .build()))
        .containsExactly(
            new StopNameValidator.MissingStopNameNotice(
                4, "stop id value", GtfsLocationType.ENTRANCE));
  }

  @Test
  public void missingStopNameForGenericNode_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.GENERIC_NODE)
                    .setStopName(null)
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingStopNameForBoardingArea_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.BOARDING_AREA)
                    .setStopName(null)
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingStopNameForUnrecognizedLocationType_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.UNRECOGNIZED)
                    .setStopName(null)
                    .build()))
        .isEmpty();
  }

  @Test
  public void missingStopDesc_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder()
                    .setCsvRowNumber(4)
                    .setStopId("stop id value")
                    .setLocationType(GtfsLocationType.STOP)
                    .setStopName("stop name value")
                    .setStopDesc(null)
                    .build()))
        .isEmpty();
  }

  @Test
  public void stopNameWithInvalidCharacter_generatesNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder().setCsvRowNumber(4).setStopName("\uFFFD").build()))
        .contains(new StopNameInvalidCharacterNotice(4, "\uFFFD"));
  }

  @Test
  public void stopNameWithoutInvalidCharacter_noNotice() {
    assertThat(
            generateNotices(
                new GtfsStop.Builder().setCsvRowNumber(4).setStopName("stop A").build()))
        .isEmpty();
  }
}
