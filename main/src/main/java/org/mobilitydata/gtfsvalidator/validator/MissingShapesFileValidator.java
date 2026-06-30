package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.reportsummary.model.FeedMetadata;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationGroupsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsShapeTableContainer;

/**
 * Validates that the feed has either a `shapes.txt` file, or uses zone-based DRT or fixed-stops
 * DRT.
 *
 * <p>Generated notice: {@link MissingRecommendedFileNotice}.
 */
@GtfsValidator
public class MissingShapesFileValidator extends FileValidator {
  private final GtfsShapeTableContainer shapeTable;
  private final GtfsLocationGroupsTableContainer locationGroups;
  private final GtfsFeedContainer feedContainer;

  @Inject
  MissingShapesFileValidator(
      GtfsShapeTableContainer shapeTable,
      GtfsLocationGroupsTableContainer locationGroups,
      GtfsFeedContainer feedContainer) {
    this.shapeTable = shapeTable;
    this.locationGroups = locationGroups;
    this.feedContainer = feedContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {

    Boolean missingShapes = shapeTable.isMissingFile() || shapeTable.isEmpty();
    boolean hasZoneBasedDrt = FeedMetadata.hasAtLeastOneTripWithOnlyLocationId(feedContainer);
    boolean hasFixedStopsDrt =
        FeedMetadata.hasAtLeastOneRecordInFile(feedContainer, "location_groups.txt")
            && FeedMetadata.hasAtLeastOneTripWithOnlyLocationGroupId(feedContainer);

    // Do we NOT have: a shapes.txt file and the required fields for Zone-Based DRT,
    // and also the required fields for Fixed-Stop DRT?
    if (missingShapes && !hasZoneBasedDrt && !hasFixedStopsDrt) {
      noticeContainer.addValidationNotice(new MissingRecommendedFileNotice("shapes.txt"));
      // This is a feed-level warning; emit it at most once.
      return;
    }
  }
}
