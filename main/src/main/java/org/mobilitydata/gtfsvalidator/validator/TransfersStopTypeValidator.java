package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;

/**
 * Validates that {@code transfers.from_stop_id} and {@code to_stop_id} reference stops or stations.
 */
@GtfsValidator
public class TransfersStopTypeValidator extends FileValidator {

  private final GtfsTransferTableContainer transfersContainer;

  private final GtfsStopTableContainer stopsContainer;

  @Inject
  public TransfersStopTypeValidator(
      GtfsTransferTableContainer transfersContainer, GtfsStopTableContainer stopsContainer) {
    this.transfersContainer = transfersContainer;
    this.stopsContainer = stopsContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer entity : transfersContainer.getEntities()) {
      validateEntity(entity, noticeContainer);
    }
  }

  public void validateEntity(GtfsTransfer entity, NoticeContainer noticeContainer) {
    validateStopType(entity, TransferDirection.TRANSFER_FROM, noticeContainer);
    validateStopType(entity, TransferDirection.TRANSFER_TO, noticeContainer);
  }

  private void validateStopType(
      GtfsTransfer entity, TransferDirection transferDirection, NoticeContainer noticeContainer) {
    Optional<GtfsStop> optStop = stopsContainer.byStopId(transferDirection.stopId(entity));
    if (optStop.isEmpty()) {
      // Foreign key reference is validated elsewhere.
      return;
    }
    GtfsLocationType locationType = optStop.get().locationType();
    if (!isValidTransferStopType(locationType)) {
      noticeContainer.addValidationNotice(
          new TransferWithInvalidStopLocationTypeNotice(entity, transferDirection, locationType));
    }
  }

  private static boolean isValidTransferStopType(GtfsLocationType locationType) {
    switch (locationType) {
      case STOP:
      case STATION:
        return true;
      default:
        return false;
    }
  }

  /**
   * A stop id field from GTFS file `transfers.txt` references a stop that has a `location_type`
   * other than 0 or 1 (aka Stop/Platform or Station).
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTransferSchema.class))
  public static final class TransferWithInvalidStopLocationTypeNotice extends ValidationNotice {

    /** The row number from `transfers.txt` for the faulty entry. */
    private final int csvRowNumber;

    /** The name of the stop id field (e.g. `from_stop_id`) referencing the stop. */
    private final String stopIdFieldName;

    /** The referenced stop id. */
    private final String stopId;

    /** The numeric value of the invalid location type. */
    private final int locationTypeValue;

    /** The name of the invalid location type. */
    private String locationTypeName;

    public TransferWithInvalidStopLocationTypeNotice(
        GtfsTransfer transfer, TransferDirection transferDirection, GtfsLocationType locationType) {
      this.csvRowNumber = transfer.csvRowNumber();
      this.stopIdFieldName = transferDirection.stopIdFieldName();
      this.stopId = transferDirection.stopId(transfer);
      this.locationTypeValue = locationType.getNumber();
      this.locationTypeName = locationType.toString();
    }
  }
}
