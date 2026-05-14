package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

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
    Boolean missingShapes = shapeTable.isMissingFile();
    if (!missingShapes) {
      return;
    }

    Boolean hasLocationId = stopTimeTable.hasColumn("location_id");
    Boolean hasLocationGroupId = stopTimeTable.hasColumn("location_group_id");
    Boolean hasLocationGroupsRecord =
        !locationGroupsTable.isMissingFile() && locationGroupsTable.entityCount() > 0;
    // Detect DRT usage from the data, not just from column presence.
    boolean hasLocationIdInData = false;
    boolean hasLocationGroupIdInData = false;
    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      if (stopTime.hasLocationId()) {
        hasLocationIdInData = true;
      }
      if (stopTime.hasLocationGroupId()) {
        hasLocationGroupIdInData = true;
      }
      if (hasLocationIdInData && hasLocationGroupIdInData) {
        break;
      }
    }

    // Do we not have: a shapes.txt file and not have a location_id (required for Zone-Based DRT),
    // and also not have a record in location_groups.txt and not have a trip in stop_times.txt that
    // references location_group_id (required for Fixed-Stop DRT)?
    if (missingShapes && !hasLocationId && !hasLocationGroupsRecord && !hasLocationGroupId) {
      noticeContainer.addValidationNotice(new MissingRecommendedFileNotice("shapes.txt"));
      // This is a feed-level warning; emit it at most once.
      return;
    }
  }
}
