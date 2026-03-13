package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;

/**
 * Validates that the feed has either a `shapes.txt` file, or uses zone-based DRT or fixed-stops
 * DRT.
 *
 * <p>Generated notice: {@link MissingRecommendedFileNotice}.
 */
@GtfsValidator
public class MissingShapesFileValidator extends FileValidator {
  private final GtfsShapeTableContainer shapeTable;
  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsLocationGroupsTableContainer locationGroupsTable;

  @Inject
  MissingShapesFileValidator(
      GtfsShapeTableContainer shapeTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsLocationGroupsTableContainer locationGroupsTable) {
    this.shapeTable = shapeTable;
    this.stopTimeTable = stopTimeTable;
    this.locationGroupsTable = locationGroupsTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      String stopId = stopTime.toString();
      Boolean missingShapes = shapeTable.isMissingFile();
      Boolean hasLocationId = stopTimeTable.hasColumn("location_id");
      Boolean hasLocationGroupId = stopTimeTable.hasColumn("location_group_id");
      Boolean hasLocationGroupsRecord = locationGroupsTable.isParsedSuccessfully();
      // Do we not have a shapes.txt file and not have a location_id (required for Zone-Based DRT)?
      if (missingShapes && !hasLocationId) {
        // Do we not have a record in location_groups.txt and not have a trip in stop_times.txt that
        // references location_group_id (required for Fixed-Stop DRT)?
        if (!hasLocationGroupsRecord && !hasLocationGroupId) {
          noticeContainer.addValidationNotice(
              new MissingRecommendedFileNotice(stopTime.csvRowNumber(), stopId));
        }
      }
    }
  }

  /**
   * A feed must have a `shapes.txt` file, and/or use zone-based or fixed-stops DRT to be usable.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsTripSchema.class}))
  static class MissingRecommendedFileNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The faulty record's id. */
    private final String tripId;

    MissingRecommendedFileNotice(int csvRowNumber, String tripId) {
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
    }
  }
}
