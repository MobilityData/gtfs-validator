package org.mobilitydata.gtfsvalidator.validator;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsTransfer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTransferTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;

@GtfsValidator
public class TransfersRelationValidator extends FileValidator {

  private final GtfsTransferTableContainer transfersContainer;
  private final GtfsTripTableContainer tripsContainer;

  @Inject
  public TransfersRelationValidator(
      GtfsTransferTableContainer transfersContainer, GtfsTripTableContainer tripsContainer) {
    this.transfersContainer = transfersContainer;
    this.tripsContainer = tripsContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTransfer transfer : transfersContainer.getEntities()) {
      validateEntity(transfer, noticeContainer);
    }
  }

  public void validateEntity(GtfsTransfer entity, NoticeContainer noticeContainer) {
    if (entity.hasFromTripId()) {
      Optional<GtfsTrip> optTrip = tripsContainer.byTripId(entity.fromTripId());
      if (optTrip.isPresent()) {
        GtfsTrip trip = optTrip.get();
        if (entity.hasFromRouteId()) {
          if (!trip.routeId().equals(entity.fromRouteId())) {
            noticeContainer.addValidationNotice(
                new TransferTripReferenceNotice(
                    entity.csvRowNumber(),
                    GtfsTransferTableLoader.FROM_TRIP_ID_FIELD_NAME,
                    entity.fromTripId(),
                    GtfsTransferTableLoader.FROM_ROUTE_ID_FIELD_NAME,
                    entity.fromRouteId()));
          }
        }
      }
    }

    if (entity.hasToTripId()) {
      Optional<GtfsTrip> optTrip = tripsContainer.byTripId(entity.toTripId());
      if (optTrip.isPresent()) {
        GtfsTrip trip = optTrip.get();
        if (entity.hasToRouteId()) {
          if (!trip.routeId().equals(entity.toRouteId())) {
            noticeContainer.addValidationNotice(
                new TransferTripReferenceNotice(
                    entity.csvRowNumber(),
                    GtfsTransferTableLoader.TO_TRIP_ID_FIELD_NAME,
                    entity.toTripId(),
                    GtfsTransferTableLoader.TO_ROUTE_ID_FIELD_NAME,
                    entity.toRouteId()));
          }
        }
      }
    }
  }

  public static class TransferTripReferenceNotice extends ValidationNotice {

    private final long csvRowNumber;

    private final String tripFieldName;
    private final String tripFieldValue;
    private final String referenceFieldName;
    private final String referenceFieldValue;

    public TransferTripReferenceNotice(
        long csvRowNumber,
        String tripFieldName,
        String tripFieldValue,
        String referenceFieldName,
        String referenceFieldValue) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = csvRowNumber;
      this.tripFieldName = tripFieldName;
      this.tripFieldValue = tripFieldValue;
      this.referenceFieldName = referenceFieldName;
      this.referenceFieldValue = referenceFieldValue;
    }
  }
}
