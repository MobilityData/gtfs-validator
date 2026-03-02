package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopAccess;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;

/**
 * Validates {@code stops.stop_access} for a single {@code GtfsStop}.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopAccessSpecifiedForStopWithNoParentStationNotice}
 *   <li>{@link StopAccessSpecifiedForIncorrectLocationNotice}
 * </ul>
 */
@GtfsValidator
public class StopAccessValidator extends SingleEntityValidator<GtfsStop> {
  @Override
  public void validate(GtfsStop entity, NoticeContainer noticeContainer) {
    if (entity.stopAccess() == null) {
      return;
    }
    if (entity.locationType() == GtfsLocationType.STOP) {
      if (!entity.hasParentStation()) {
        noticeContainer.addValidationNotice(
            new StopAccessSpecifiedForStopWithNoParentStationNotice(
                entity.csvRowNumber(),
                entity.stopId(),
                entity.stopName(),
                entity.stopAccess(),
                entity.locationType()));
      }
    } else {
      noticeContainer.addValidationNotice(
          new StopAccessSpecifiedForIncorrectLocationNotice(
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
   *
   * <p>stops.stop_access is forbidden for stops that are not associated with a parent station.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs(GtfsStopSchema.class))
  static class StopAccessSpecifiedForStopWithNoParentStationNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final long csvRowNumber;

    /** The `stops.stop_id` of the faulty record. */
    private final String stopId;

    /** The 'stops.stop_name' of the faulty record. */
    private final String stopName;

    /** The `stops.stop_access` of the faulty record. */
    private final GtfsStopAccess stopAccess;

    /** `stops.location_type` of the faulty record. */
    private final GtfsLocationType locationType;

    public StopAccessSpecifiedForStopWithNoParentStationNotice(
        long csvRowNumber,
        String stopId,
        String stopName,
        GtfsStopAccess stopAccess,
        GtfsLocationType locationType) {
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.stopName = stopName;
      this.stopAccess = stopAccess;
      this.locationType = locationType;
    }
  }

  /**
   * A location that is not a stop has stop_access specified.
   *
   * <p>Stops.stop_access is forbidden for locations that are stations, entrances, generic nodes or
   * boarding areas. It can only be specific when a stop is associated with a parent station.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs(GtfsStopSchema.class))
  static class StopAccessSpecifiedForIncorrectLocationNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final long csvRowNumber;

    /** The `stops.stop_id` of the faulty record. */
    private final String stopId;

    /** The 'stops.stop_name' of the faulty record. */
    private final String stopName;

    /** The `stops.stop_access` of the faulty record. */
    private final GtfsStopAccess stopAccess;

    /** `stops.location_type` of the faulty record. */
    private final GtfsLocationType locationType;

    public StopAccessSpecifiedForIncorrectLocationNotice(
        long csvRowNumber,
        String stopId,
        String stopName,
        GtfsStopAccess stopAccess,
        GtfsLocationType locationType) {
      this.csvRowNumber = csvRowNumber;
      this.stopId = stopId;
      this.stopName = stopName;
      this.stopAccess = stopAccess;
      this.locationType = locationType;
    }
  }
}
