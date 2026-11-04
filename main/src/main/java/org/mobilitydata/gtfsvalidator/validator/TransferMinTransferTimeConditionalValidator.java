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

import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.MINIMUM_TIME;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;

/**
 * Validates the conditional requirement of {@code transfers.min_transfer_time}.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 */
@GtfsValidator
public class TransferMinTransferTimeConditionalValidator
    extends SingleEntityValidator<GtfsTransfer> {

  @Override
  public void validate(GtfsTransfer transfer, NoticeContainer noticeContainer) {
    if (MINIMUM_TIME.equals(transfer.transferType()) && !transfer.hasMinTransferTime()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsTransfer.FILENAME,
              transfer.csvRowNumber(),
              GtfsTransfer.MIN_TRANSFER_TIME_FIELD_NAME));
    }
  }
}
