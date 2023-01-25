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

import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.FROM_TRIP_ID_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsTransfer.TO_TRIP_ID_FIELD_NAME;

import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

/**
 * Validates that trip-to-trip transfers do not involve frequency-based trips.
 *
 * <p> Generated notice: {@link TransferForFrequencyBasedTripNotice}.
 */
@GtfsValidator
public class TransferFrequencyBasedTripValidator extends FileValidator {
  private final GtfsTransferTableContainer transferTable;
  private final GtfsFrequencyTableContainer frequencyTable;

  @Inject
  TransferFrequencyBasedTripValidator(
      GtfsTransferTableContainer transferTable, GtfsFrequencyTableContainer frequencyTable) {
    this.transferTable = transferTable;
    this.frequencyTable = frequencyTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transferTable.getEntities()) {
      validateTripEndpoint(
          transfer, FROM_TRIP_ID_FIELD_NAME, transfer.fromTripId(), noticeContainer);
      validateTripEndpoint(transfer, TO_TRIP_ID_FIELD_NAME, transfer.toTripId(), noticeContainer);
    }
  }

  private void validateTripEndpoint(GtfsTransfer transfer, String tripIdFieldName, String tripId,
      NoticeContainer noticeContainer) {
    if (tripId.isEmpty()) {
      return;
    }
    List<GtfsFrequency> frequencies = frequencyTable.byTripId(tripId);
    if (!frequencies.isEmpty()) {
      noticeContainer.addValidationNotice(
          new TransferForFrequencyBasedTripNotice(transfer, tripIdFieldName, frequencies.get(0)));
    }
  }

  /** Describes a transfer for a frequency-based trip. */
  static class TransferForFrequencyBasedTripNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tripIdFieldName;
    private final String tripId;
    private final long frequencyCsvRow;

    TransferForFrequencyBasedTripNotice(
        GtfsTransfer transfer, String tripIdFieldName, GtfsFrequency frequency) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = transfer.csvRowNumber();
      this.tripIdFieldName = tripIdFieldName;
      this.tripId = frequency.tripId();
      this.frequencyCsvRow = frequency.csvRowNumber();
    }
  }
}
