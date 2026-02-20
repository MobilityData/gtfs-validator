package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.PLATFORM_CODE_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsStop.STOP_ACCESS_FIELD_NAME;

import java.util.*;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Validates that no pathway links to a stop with stop_access == 1, which should be accessible
 * outside of the station’s pathways.
 *
 * <p>Stops with stop_access=1 should not have pathways that link to them since they can be accessed
 * from the street and are independent of the pathways inside the station.
 *
 * <p>Generated notice: {@link PathwayToStopWithAccessOutsideOfStationPathwaysNotice}.
 */
@GtfsValidator
public class PathwayStopAccessValidator extends FileValidator {
  private final GtfsPathwayTableContainer pathwayTable;

  private final GtfsStopTableContainer stopTable;

  @Inject
  PathwayStopAccessValidator(
      GtfsPathwayTableContainer pathwayTable, GtfsStopTableContainer stopTable) {
    this.pathwayTable = pathwayTable;
    this.stopTable = stopTable;
  }

  @Override
  public boolean shouldCallValidate() {
    return stopTable.hasColumn(STOP_ACCESS_FIELD_NAME) && stopTable.hasColumn(PLATFORM_CODE_FIELD_NAME);
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<String, String> externalAccessStopIdToPlatformCode = new HashMap<>();
    stopTable
        .getEntities()
        .forEach(
            stop -> {
              if (stop.stopAccess() != null
                  && stop.stopAccess() == GtfsStopAccess.NOT_ACCESSIBLE_VIA_PATHWAYS) {
                externalAccessStopIdToPlatformCode.put(stop.stopId(), stop.platformCode());
              }
            });

    pathwayTable
        .getEntities()
        .forEach(
            pathway -> {
              Set<String> emittedStopIds = new HashSet<>(2);

              String fromStopId = pathway.fromStopId();
              if (fromStopId != null
                  && !fromStopId.isBlank()
                  && externalAccessStopIdToPlatformCode.containsKey(fromStopId)
                  && emittedStopIds.add(fromStopId)) {
                noticeContainer.addValidationNotice(
                    new PathwayToStopWithAccessOutsideOfStationPathwaysNotice(
                        pathway.csvRowNumber(),
                        externalAccessStopIdToPlatformCode.get(fromStopId),
                        pathway.pathwayId(),
                        fromStopId));
              }

              String toStopId = pathway.toStopId();
              if (toStopId != null
                  && !toStopId.isBlank()
                  && externalAccessStopIdToPlatformCode.containsKey(toStopId)
                  && emittedStopIds.add(toStopId)) {
                noticeContainer.addValidationNotice(
                    new PathwayToStopWithAccessOutsideOfStationPathwaysNotice(
                        pathway.csvRowNumber(),
                        externalAccessStopIdToPlatformCode.get(toStopId),
                        pathway.pathwayId(),
                        toStopId));
              }
            });
  }

  /**
   * A pathway has an endpoint that is a stop with stop_access=1, which should be accessible outside
   * of the station’s pathways.
   *
   * <p>Stops with stop_access=1 should not have pathways that link to them since they can be
   * accessed from the street and are independent of the pathways inside the station.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs({GtfsPathwaySchema.class, GtfsStopSchema.class}))
  static class PathwayToStopWithAccessOutsideOfStationPathwaysNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The platform code (if available) associated with the stop referenced by this pathway. */
    private String platformCode;

    /** The pathway_id of the pathway record that triggered this notice. */
    private final String pathwayId;

    /** The stop_id (either from_stop_id or to_stop_id) that has stop_access == 1. */
    private final String stopId;

    public PathwayToStopWithAccessOutsideOfStationPathwaysNotice(
        int csvRowNumber, String platformCode, String pathwayId, String stopId) {
      this.csvRowNumber = csvRowNumber;
      this.platformCode = platformCode;
      this.pathwayId = pathwayId;
      this.stopId = stopId;
    }
  }
}
