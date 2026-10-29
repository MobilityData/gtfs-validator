/*
 * Copyright 2026 MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import com.google.common.geometry.S2LatLng;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.util.S2Earth;
import org.mobilitydata.gtfsvalidator.util.StopUtil;

/** Checks whether a transfer requires walking faster than 2 m/s. */
@GtfsValidator
public class TransferWalkingSpeedValidator extends FileValidator {
  private static final double FAST_WALKING_SPEED_METERS_PER_SECOND = 2.0;

  private final GtfsTransferTableContainer transfers;
  private final GtfsStopTableContainer stops;

  @Inject
  TransferWalkingSpeedValidator(
      GtfsTransferTableContainer transfers, GtfsStopTableContainer stops) {
    this.transfers = transfers;
    this.stops = stops;
  }

  @Override
  public boolean shouldCallValidate() {
    return transfers != null
        && stops != null
        && !transfers.isMissingFile()
        && !stops.isMissingFile()
        && transfers.isParsedSuccessfully()
        && stops.isParsedSuccessfully()
        && transfers.hasColumn(GtfsTransfer.FROM_STOP_ID_FIELD_NAME)
        && transfers.hasColumn(GtfsTransfer.TO_STOP_ID_FIELD_NAME)
        && transfers.hasColumn(GtfsTransfer.MIN_TRANSFER_TIME_FIELD_NAME)
        && stops.hasColumn(GtfsStop.STOP_ID_FIELD_NAME);
  }

  @Override
  public void validate(NoticeContainer notices) {
    if (!shouldCallValidate()) {
      return;
    }
    for (GtfsTransfer transfer : transfers.getEntities()) {
      if (!transfer.hasFromStopId()
          || transfer.fromStopId().isEmpty()
          || !transfer.hasToStopId()
          || transfer.toStopId().isEmpty()
          || !transfer.hasMinTransferTime()
          || transfer.minTransferTime() < 0) {
        continue;
      }

      // The optional helper distinguishes missing coordinates from a real (0, 0).
      Optional<S2LatLng> fromCoordinates =
          StopUtil.getOptionalStopOrParentLatLng(stops, transfer.fromStopId())
              .filter(TransferWalkingSpeedValidator::hasValidCoordinates);
      Optional<S2LatLng> toCoordinates =
          StopUtil.getOptionalStopOrParentLatLng(stops, transfer.toStopId())
              .filter(TransferWalkingSpeedValidator::hasValidCoordinates);
      if (fromCoordinates.isEmpty() || toCoordinates.isEmpty()) {
        continue;
      }

      double distanceMeters = S2Earth.getDistanceMeters(fromCoordinates.get(), toCoordinates.get());
      if (Double.isFinite(distanceMeters)
          && distanceMeters >= 0
          && distanceMeters / FAST_WALKING_SPEED_METERS_PER_SECOND > transfer.minTransferTime()) {
        notices.addValidationNotice(
            new TransferWalkingSpeedTooFastNotice(transfer, distanceMeters));
      }
    }
  }

  private static boolean hasValidCoordinates(S2LatLng coordinates) {
    return Double.isFinite(coordinates.latDegrees())
        && Double.isFinite(coordinates.lngDegrees())
        && coordinates.isValid();
  }

  /**
   * A transfer requires walking faster than 2 m/s.
   *
   * <p>The straight-line distance divided by 2 m/s exceeds `min_transfer_time`. This is a
   * data-quality heuristic rather than a required GTFS walking speed.
   *
   * <p>Straight-line distance ignores barriers, crossings, and platform-to-platform routing.
   */
  @GtfsValidationNotice(
      severity = INFO,
      files = @GtfsValidationNotice.FileRefs({GtfsTransferSchema.class, GtfsStopSchema.class}))
  public static class TransferWalkingSpeedTooFastNotice extends ValidationNotice {
    /** The row number of the transfer in `transfers.txt`. */
    private final int csvRowNumber;

    /** The original stop ID in `from_stop_id`. */
    private final String fromStopId;

    /** The original stop ID in `to_stop_id`. */
    private final String toStopId;

    /** The provided `min_transfer_time` in seconds. */
    private final int minTransferTime;

    /** The distance between the resolved stop coordinates in meters. */
    private final double stopDistance;

    public TransferWalkingSpeedTooFastNotice(GtfsTransfer transfer, double stopDistance) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.fromStopId = transfer.fromStopId();
      this.toStopId = transfer.toStopId();
      this.minTransferTime = transfer.minTransferTime();
      this.stopDistance = stopDistance;
    }
  }
}
