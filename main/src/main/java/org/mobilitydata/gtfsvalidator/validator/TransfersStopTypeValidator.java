package org.mobilitydata.gtfsvalidator.validator;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableLoader;

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
    validateStopType(
        entity,
        GtfsTransferTableLoader.FROM_STOP_ID_FIELD_NAME,
        entity.fromStopId(),
        noticeContainer);
    validateStopType(
        entity, GtfsTransferTableLoader.TO_STOP_ID_FIELD_NAME, entity.toStopId(), noticeContainer);
  }

  private void validateStopType(
      GtfsTransfer entity, String stopIdFieldName, String stopId, NoticeContainer noticeContainer) {
    Optional<GtfsStop> optStop = stopsContainer.byStopId(stopId);
    if (optStop.isEmpty()) {
      // Foreign key reference is validated elsewhere.
      return;
    }

    GtfsLocationType locationType = optStop.get().locationType();
    if (!isValidTransferStopType(locationType)) {
      noticeContainer.addValidationNotice(
          new TransfersStopLocationTypeNotice(
              entity.csvRowNumber(), stopIdFieldName, locationType));
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

  public static final class TransfersStopLocationTypeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopIdFieldName;
    private final int locationType;

    public TransfersStopLocationTypeNotice(
        long csvRowNumber, String stopIdFieldName, GtfsLocationType locationType) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.stopIdFieldName = stopIdFieldName;
      this.locationType = locationType.getNumber();
    }
  }
}
