/*
 * Copyright 2024 MobilityData
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

import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.IN_SEAT_TRANSFER_NOT_ALLOWED;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

/**
 * Validates the conditional requirement of {@code transfers.from_stop_id} and {@code to_stop_id}.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link MissingRequiredFieldNotice} - {@code from_stop_id} is missing or {@code to_stop_id}
 *       is missing for all transfer types except for in-seat transfer types
 * </ul>
 */
@GtfsValidator
public class TransferStopIdsConditionalValidator extends FileValidator {

  private final GtfsTransferTableContainer transfersContainer;

  @Inject
  public TransferStopIdsConditionalValidator(GtfsTransferTableContainer transfersContainer) {
    this.transfersContainer = transfersContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transfersContainer.getEntities()) {
      if (transfer.hasTransferType()) {
        validateTransferEntity(transfer, noticeContainer);
      }
    }
  }

  private boolean isTransferTypeInSeat(GtfsTransfer transfer) {
    return IN_SEAT_TRANSFER_ALLOWED.equals(transfer.transferType())
        || IN_SEAT_TRANSFER_NOT_ALLOWED.equals(transfer.transferType());
  }

  private void validateTransferEntity(GtfsTransfer transfer, NoticeContainer noticeContainer) {
    if (!isTransferTypeInSeat(transfer)) {
      if (!transfer.hasFromStopId()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                transfersContainer.gtfsFilename(), transfer.csvRowNumber(), "from_stop_id"));
      }
      if (!transfer.hasToStopId()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                transfersContainer.gtfsFilename(), transfer.csvRowNumber(), "to_stop_id"));
      }
    }
  }
}
