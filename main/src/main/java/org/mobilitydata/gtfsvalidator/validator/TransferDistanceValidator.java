/*
 * Copyright 2025 Google LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.*;
import static org.mobilitydata.gtfsvalidator.util.S2Earth.getDistanceMeters;

import com.google.common.geometry.S2Point;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

/**
 * Validates that the transfer distance between two stops is not too large.
 *
 * <p>Generated notice: {@link TransferDistanceTooLargeNotice}.
 *
 * <p>Generated notice: {@link TransferDistanceAbove_2KmNotice}.
 */
@GtfsValidator
public class TransferDistanceValidator extends FileValidator {
  private final GtfsTransferTableContainer transferTableContainer;
  private final GtfsStopTableContainer stopTableContainer;

  @Inject
  TransferDistanceValidator(
      GtfsTransferTableContainer transferTableContainer,
      GtfsStopTableContainer stopTableContainer) {
    this.transferTableContainer = transferTableContainer;
    this.stopTableContainer = stopTableContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transferTableContainer.getEntities()) {
      if (transfer.hasFromStopId() && transfer.hasToStopId()) {
        S2Point fromCoordinates =
            StopUtil.getStopOrParentLatLng(stopTableContainer, transfer.fromStopId()).toPoint();
        S2Point toCoordinates =
            StopUtil.getStopOrParentLatLng(stopTableContainer, transfer.toStopId()).toPoint();
        double distanceMeters = getDistanceMeters(fromCoordinates, toCoordinates);
        if (distanceMeters > 10_000) {
          noticeContainer.addValidationNotice(
              new TransferDistanceTooLargeNotice(transfer, distanceMeters / 1_000));

        } else if (distanceMeters > 2_000) {
          noticeContainer.addValidationNotice(
              new TransferDistanceAbove_2KmNotice(transfer, distanceMeters / 1_000));
        }
      }
    }
  }

  @Override
  public boolean shouldCallValidate() {
    return transferTableContainer != null
        && stopTableContainer != null
        && transferTableContainer.hasColumn(GtfsTransfer.FROM_STOP_ID_FIELD_NAME)
        && transferTableContainer.hasColumn(GtfsTransfer.TO_STOP_ID_FIELD_NAME);
  }

  /** The transfer distance from stop to stop in `transfers.txt` is larger than 2 km. */
  @GtfsValidationNotice(severity = INFO)
  public static class TransferDistanceAbove_2KmNotice extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The ID of the stop in `from_stop_id`. */
    private final String fromStopId;

    /** The ID of the stop in `to_stop_id`. */
    private final String toStopId;

    /** The distance between the two stops in km. */
    private final double distanceKm;

    public TransferDistanceAbove_2KmNotice(GtfsTransfer transfer, double distanceKm) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.fromStopId = transfer.fromStopId();
      this.toStopId = transfer.toStopId();
      this.distanceKm = distanceKm;
    }
  }

  /** The transfer distance from stop to stop in `transfers.txt` is larger than 10 km. */
  @GtfsValidationNotice(severity = WARNING)
  public static class TransferDistanceTooLargeNotice extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The ID of the stop in `from_stop_id`. */
    private final String fromStopId;

    /** The ID of the stop in `to_stop_id`. */
    private final String toStopId;

    /** The distance between the two stops in km. */
    private final double distanceKm;

    public TransferDistanceTooLargeNotice(GtfsTransfer transfer, double distanceKm) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.fromStopId = transfer.fromStopId();
      this.toStopId = transfer.toStopId();
      this.distanceKm = distanceKm;
    }
  }
}
