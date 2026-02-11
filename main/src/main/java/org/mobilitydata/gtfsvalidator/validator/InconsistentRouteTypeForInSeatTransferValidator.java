package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

@GtfsValidator
public class InconsistentRouteTypeForInSeatTransferValidator extends FileValidator {
  private final GtfsTransferTableContainer transferTable;
  private final GtfsRouteTableContainer routeTable;

  @Inject
  InconsistentRouteTypeForInSeatTransferValidator(
      GtfsTransferTableContainer transferTable, GtfsRouteTableContainer routeTable) {
    this.transferTable = transferTable;
    this.routeTable = routeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    transferTable
        .byTransferType(GtfsTransferType.IN_SEAT_TRANSFER_ALLOWED)
        .forEach(
            transfer -> {
              String fromRouteId = transfer.fromRouteId();
              String toRouteId = transfer.toRouteId();
              routeTable
                  .byRouteId(fromRouteId)
                  .ifPresent(
                      fromRoute -> {
                        routeTable
                            .byRouteId(toRouteId)
                            .ifPresent(
                                toRoute -> {
                                  if (fromRoute.routeType() != toRoute.routeType()) {
                                    noticeContainer.addValidationNotice(
                                        new InconsistentRouteTypeForInSeatTransferNotice(
                                            transfer.csvRowNumber(),
                                            fromRouteId,
                                            toRouteId,
                                            fromRoute.routeType(),
                                            toRoute.routeType()));
                                  }
                                });
                      });
            });
  }

  /** An in-seat transfer should occur in the same route mode. */
  @GtfsValidationNotice(
      severity = SeverityLevel.WARNING,
      files = @GtfsValidationNotice.FileRefs({GtfsTransferSchema.class, GtfsRouteSchema.class}))
  static class InconsistentRouteTypeForInSeatTransferNotice extends ValidationNotice {
    /** The csv row number of the faulty transfer in tranfers.txt. */
    private final long csvRowNumber;

    /** The from_route_id value of the faulty transfer. */
    private final String fromRouteId;

    /** The to_route_id value of the faulty transfer. */
    private final String toRouteId;

    /** The route_type value of the from_route_id. */
    private final GtfsRouteType fromRouteType;

    /** The route_type value of the to_route_id. */
    private final GtfsRouteType toRouteType;

    public InconsistentRouteTypeForInSeatTransferNotice(
        long csvRowNumber,
        String fromRouteId,
        String toRouteId,
        GtfsRouteType fromRouteType,
        GtfsRouteType toRouteType) {
      this.csvRowNumber = csvRowNumber;
      this.fromRouteId = fromRouteId;
      this.toRouteId = toRouteId;
      this.fromRouteType = fromRouteType;
      this.toRouteType = toRouteType;
    }
  }
}
