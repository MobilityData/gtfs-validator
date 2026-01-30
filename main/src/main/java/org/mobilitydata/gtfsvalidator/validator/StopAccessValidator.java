package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopAccess;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

public class StopAccessValidator extends SingleEntityValidator<GtfsStop> {
  @Override
  public void validate(GtfsStop entity, NoticeContainer noticeContainer) {
    if (entity.stopAccess() != GtfsStopAccess.EMPTY && entity.locationType() == GtfsLocationType.STOP && !entity.hasParentStation()) {
      noticeContainer.addValidationNotice(
          new StopAccessSpecifiedForStopWithNoParentStation(
              entity.csvRowNumber(),
              entity.stopId(),
              entity.stopName(),
              entity.stopAccess(),
              entity.locationType()));
    }
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    return header.hasColumn(GtfsStop.STOP_ACCESS_FIELD_NAME);
  }

  /**
   * A stop without a value for parent station has stop_access specified.
   */
  @GtfsValidationNotice(severity = ERROR, files = @GtfsValidationNotice.FileRefs(GtfsStopSchema.class))
  static class StopAccessSpecifiedForStopWithNoParentStation extends ValidationNotice {
    /** The row of the faulty record. */
    private final long csvRowNumber;

    /** The `stops.stop_id` of the faulty record. */
    private final String stopId;

    /** The 'stops.stop_name' of the faulty record. */
    private final String stopName;

    private final GtfsStopAccess stopAccess;

    /** `stops.location_type` of the faulty record. */
    private GtfsLocationType locationType;

    public StopAccessSpecifiedForStopWithNoParentStation(long csvRowNumber, String stopId, String stopName, GtfsStopAccess stopAccess, GtfsLocationType locationType) {
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.stopName = stopName;
      this.stopAccess = stopAccess;
      this.locationType = locationType;
    }
  }
}
