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

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@RunWith(JUnit4.class)
public class TimeframeTimeValidatorTest {

  @Test
  public void test() {
    assertThat(
            validate(
                new GtfsTimeframe.Builder()
                    .setStartTime(GtfsTime.fromString("00:00:00"))
                    .setEndTime(GtfsTime.fromString("24:00:00"))
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validate(GtfsTimeframe timeframe) {
    NoticeContainer container = new NoticeContainer();
    new TimeframeTimeValidator().validate(timeframe, container);
    return container.getValidationNotices();
  }
}
