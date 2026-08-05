package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;

/**
 * Validates that {@code stair_count} is provided for pathways that are stairs.
 *
 * <p>The GTFS specification recommends {@code pathways.stair_count} for stairs ({@code
 * pathway_mode=2}). Without it, a consumer cannot tell how many steps a path involves, which is
 * information riders with limited mobility rely on when choosing a route through a station.
 *
 * <p>Generated notice: {@link MissingRecommendedFieldNotice}.
 */
@GtfsValidator
public class PathwayStairCountValidator extends SingleEntityValidator<GtfsPathway> {

  @Override
  public void validate(GtfsPathway entity, NoticeContainer noticeContainer) {
    if (entity.pathwayMode() == GtfsPathwayMode.STAIRS && !entity.hasStairCount()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsPathway.FILENAME, entity.csvRowNumber(), GtfsPathway.STAIR_COUNT_FIELD_NAME));
    }
  }
}
