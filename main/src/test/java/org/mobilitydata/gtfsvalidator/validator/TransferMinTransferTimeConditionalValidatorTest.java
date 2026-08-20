/*
 * Copyright 2026 MobilityData
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
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.IMPOSSIBLE;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.MINIMUM_TIME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.RECOMMENDED;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.TIMED;

import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;

public class TransferMinTransferTimeConditionalValidatorTest {

  @Test
  public void MinimumTimeWithoutMinTransferTimeShouldGenerateNotice() {
    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setTransferType(MINIMUM_TIME)
                    .build()))
        .containsExactly(new MissingRequiredFieldNotice("transfers.txt", 2, "min_transfer_time"));
  }

  @Test
  public void MinimumTimeWithZeroMinTransferTimeShouldGenerateNothing() {
    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setTransferType(MINIMUM_TIME)
                    .setMinTransferTime(0)
                    .build()))
        .isEmpty();
  }

  @Test
  public void MinimumTimeWithPositiveMinTransferTimeShouldGenerateNothing() {
    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder()
                    .setCsvRowNumber(2)
                    .setTransferType(MINIMUM_TIME)
                    .setMinTransferTime(60)
                    .build()))
        .isEmpty();
  }

  @Test
  public void OtherTransferTypesWithoutMinTransferTimeShouldGenerateNothing() {
    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder().setCsvRowNumber(2).setTransferType(RECOMMENDED).build()))
        .isEmpty();

    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder().setCsvRowNumber(2).setTransferType(TIMED).build()))
        .isEmpty();

    assertThat(
            validationNoticesFor(
                new GtfsTransfer.Builder().setCsvRowNumber(2).setTransferType(IMPOSSIBLE).build()))
        .isEmpty();
  }

  private List<ValidationNotice> validationNoticesFor(GtfsTransfer entity) {
    TransferMinTransferTimeConditionalValidator validator =
        new TransferMinTransferTimeConditionalValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
