/*
 * Copyright 2021 Google LLC
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
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.validator.PathwayLoopValidator.PathwayLoopNotice;

@RunWith(JUnit4.class)
public final class PathwayLoopValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsPathway pathway) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayLoopValidator().validate(pathway, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void loop_yieldsNotice() {
    GtfsPathway pathway =
        new GtfsPathway.Builder()
            .setCsvRowNumber(2)
            .setPathwayId("pw1")
            .setFromStopId("platform1")
            .setToStopId("platform1")
            .build();
    assertThat(generateNotices(pathway)).containsExactly(new PathwayLoopNotice(pathway));
  }

  @Test
  public void noLoop_yieldsNoNotice() {
    assertThat(
            generateNotices(
                new GtfsPathway.Builder()
                    .setCsvRowNumber(2)
                    .setPathwayId("pw1")
                    .setFromStopId("platform1")
                    .setToStopId("entrance2")
                    .build()))
        .isEmpty();
  }

  @Test
  public void noEndpoints_yieldsNoNotice() {
    assertThat(
            generateNotices(
                new GtfsPathway.Builder().setCsvRowNumber(2).setPathwayId("pw1").build()))
        .isEmpty();
  }
}
