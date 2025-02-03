/*
 * Copyright 2020 Google LLC
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;
import static org.mobilitydata.gtfsvalidator.util.S2Earth.getDistanceMeters;

import com.google.common.geometry.S2Point;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

/**
 * Checks that agency_id field in "routes.txt" is defined for every row if there is more than 1
 * agency in the feed, recommended if only 1 agency.
 *
 * <p>Generated notice: {@link MissingRequiredFieldNotice}.
 *
 * <p>Generated notice: {@link MissingRecommendedFieldNotice}.
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
              new TransferDistanceAboveWarningThresholdNotice(transfer, distanceMeters));

        } else if (distanceMeters > 2_000) {
          noticeContainer.addValidationNotice(
              new TransferDistanceTooLarge(transfer, distanceMeters));
        }
      }
    }
  }

  @Override
  public boolean shouldCallValidate() {
    return transferTableContainer.hasColumn(GtfsTransfer.FROM_STOP_ID_FIELD_NAME)
        && transferTableContainer.hasColumn(GtfsTransfer.TO_STOP_ID_FIELD_NAME);
  }

  /** The transfer distance from stop to stop in `transfers.txt` is larger than 2 km. */
  @GtfsValidationNotice(severity = WARNING)
  public static class TransferDistanceAboveWarningThresholdNotice extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The ID of the stop in `from_stop_id`. */
    private final String fromStopId;

    /** The ID of the stop in `to_stop_id`. */
    private final String toStopId;

    /** The distance between the two stops in meters. */
    private final double distanceMeters;

    public TransferDistanceAboveWarningThresholdNotice(
        GtfsTransfer transfer, double distanceMeters) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.fromStopId = transfer.fromStopId();
      this.toStopId = transfer.toStopId();
      this.distanceMeters = distanceMeters;
    }
  }

  /** The transfer distance from stop to stop in `transfers.txt` is larger than 10 km. */
  @GtfsValidationNotice(severity = ERROR)
  public static class TransferDistanceTooLarge extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The ID of the stop in `from_stop_id`. */
    private final String fromStopId;

    /** The ID of the stop in `to_stop_id`. */
    private final String toStopId;

    /** The distance between the two stops in meters. */
    private final double distanceMeters;

    public TransferDistanceTooLarge(GtfsTransfer transfer, double distanceMeters) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.fromStopId = transfer.fromStopId();
      this.toStopId = transfer.toStopId();
      this.distanceMeters = distanceMeters;
    }
  }
}
