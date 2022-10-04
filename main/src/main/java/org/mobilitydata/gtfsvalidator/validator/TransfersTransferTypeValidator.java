package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.ForbiddenFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferType;

@GtfsValidator
public class TransfersTransferTypeValidator extends SingleEntityValidator<GtfsTransfer> {

  private enum LinkedTripFieldRequirement {
    /**
     * Indicates a particular field *MUST* have a value if a linked-trip transfer type is specified.
     */
    REQUIRED,
    /**
     * Indicates a particular field must *NOT* have a value if a linked-trip transfer type is
     * specified.
     */
    FORBIDDEN
  }

  @Override
  public void validate(GtfsTransfer transfer, NoticeContainer noticeContainer) {
    if (!transfer.hasTransferType()) {
      return;
    }
    GtfsTransferType transferType = transfer.transferType();
    if (isLinkedTripTransferType(transferType)) {
      // Conditionally forbidden for linked-trip transfer types: from_stop_id, to_stop_id,
      // from_route_id, to_route_id
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.FROM_STOP_ID_FIELD_NAME,
          transfer.hasFromStopId(),
          LinkedTripFieldRequirement.FORBIDDEN,
          noticeContainer,
          transfer);
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.TO_STOP_ID_FIELD_NAME,
          transfer.hasToStopId(),
          LinkedTripFieldRequirement.FORBIDDEN,
          noticeContainer,
          transfer);
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.FROM_ROUTE_ID_FIELD_NAME,
          transfer.hasFromRouteId(),
          LinkedTripFieldRequirement.FORBIDDEN,
          noticeContainer,
          transfer);
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.TO_ROUTE_ID_FIELD_NAME,
          transfer.hasToRouteId(),
          LinkedTripFieldRequirement.FORBIDDEN,
          noticeContainer,
          transfer);
      // Conditionally required for linked-trip transfer types: from_trip_id, to_trip_id.
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.FROM_TRIP_ID_FIELD_NAME,
          transfer.hasFromTripId(),
          LinkedTripFieldRequirement.REQUIRED,
          noticeContainer,
          transfer);
      validateLinkedTripFieldRequirement(
          GtfsTransferTableLoader.TO_TRIP_ID_FIELD_NAME,
          transfer.hasToTripId(),
          LinkedTripFieldRequirement.REQUIRED,
          noticeContainer,
          transfer);
    } else {
      if (!transfer.hasFromStopId()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTransferTableLoader.FILENAME,
                transfer.csvRowNumber(),
                GtfsTransferTableLoader.FROM_STOP_ID_FIELD_NAME));
      }
      if (!transfer.hasToStopId()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTransferTableLoader.FILENAME,
                transfer.csvRowNumber(),
                GtfsTransferTableLoader.TO_STOP_ID_FIELD_NAME));
      }
    }
  }

  private void validateLinkedTripFieldRequirement(
      String fieldName,
      boolean fieldPresent,
      LinkedTripFieldRequirement fieldRequirement,
      NoticeContainer noticeContainer,
      GtfsTransfer transfer) {
    if (fieldRequirement == LinkedTripFieldRequirement.REQUIRED && !fieldPresent) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsTransferTableLoader.FILENAME, transfer.csvRowNumber(), fieldName));
    } else if (fieldRequirement == LinkedTripFieldRequirement.FORBIDDEN && fieldPresent) {
      noticeContainer.addValidationNotice(
          new ForbiddenFieldNotice(
              GtfsTransferTableLoader.FILENAME, transfer.csvRowNumber(), fieldName));
    }
  }

  private boolean isLinkedTripTransferType(GtfsTransferType transferType) {
    return transferType == GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED
        || transferType == GtfsTransferType.IN_SEAT_TRANSFER_NOT_ALLOWED;
  }
}
