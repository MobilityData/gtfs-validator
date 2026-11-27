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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransferType.MINIMUM_TIME;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferSchema;

/**
 * Validates the presence and duration of {@code transfers.min_transfer_time}.
 *
 * <p>Generated notices: {@link MissingRequiredFieldNotice}, {@link MinTransferTimeTooLongNotice}.
 */
@GtfsValidator
public class TransferMinTransferTimeConditionalValidator
    extends SingleEntityValidator<GtfsTransfer> {
  private static final int LONG_TRANSFER_THRESHOLD_SECONDS = 3 * 60 * 60;

  @Override
  public void validate(GtfsTransfer transfer, NoticeContainer noticeContainer) {
    if (MINIMUM_TIME.equals(transfer.transferType()) && !transfer.hasMinTransferTime()) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsTransfer.FILENAME,
              transfer.csvRowNumber(),
              GtfsTransfer.MIN_TRANSFER_TIME_FIELD_NAME));
    }
    if (transfer.hasMinTransferTime()
        && transfer.minTransferTime() > LONG_TRANSFER_THRESHOLD_SECONDS) {
      noticeContainer.addValidationNotice(
          new MinTransferTimeTooLongNotice(transfer.csvRowNumber(), transfer.minTransferTime()));
    }
  }

  /**
   * The minimum transfer time exceeds three hours.
   *
   * <p>Check that the value in `min_transfer_time` is the intended minimum time in seconds.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsTransferSchema.class))
  static class MinTransferTimeTooLongNotice extends ValidationNotice {
    /** The name of the GTFS file containing the transfer. */
    private final String filename = GtfsTransfer.FILENAME;

    /** The row number of the transfer in `transfers.txt`. */
    private final int csvRowNumber;

    /** The name of the field containing the minimum transfer time. */
    private final String fieldName = GtfsTransfer.MIN_TRANSFER_TIME_FIELD_NAME;

    /** The minimum transfer time specified in the feed, in seconds. */
    private final int value;

    /** The warning threshold, in seconds. */
    private final int threshold = LONG_TRANSFER_THRESHOLD_SECONDS;

    MinTransferTimeTooLongNotice(int csvRowNumber, int value) {
      this.csvRowNumber = csvRowNumber;
      this.value = value;
    }
  }
}
