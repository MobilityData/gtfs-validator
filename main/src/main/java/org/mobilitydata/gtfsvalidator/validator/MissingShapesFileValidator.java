package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import javax.inject.Inject;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShape;
import org.mobilitydata.gtfsvalidator.table.GtfsTripSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

  /**
 * Validates that the feed has either a `shapes.txt` file, or uses zone-based DRT or fixed-stops DRT.
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
      GtfsShapeTableContainer shapeTable, GtfsStopTimeTableContainer stopTimeTable, GtfsLocationGroupsTableContainer locationGroupsTable) {
    this.shapeTable = shapeTable;
    this.stopTimeTable = stopTimeTable;
    this.locationGroupsTable = locationGroupsTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsShape shape : shapeTable.getEntities()) {
      String shapeId = shape.shapeId();
      if (stopTimeTable.byTripId(shapeId).size() <= 1) {
        noticeContainer.addValidationNotice(new MissingRecommendedFileNotice(shape.csvRowNumber(), shapeId));
      }
    }
  }

    /**
   * A feed must have a `shapes.txt` file, and/or use zone-based or fixed-stops DRT to be usable.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsTripSchema.class})
      )

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
