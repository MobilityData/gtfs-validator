package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwaySchema;

/**
 * Validates the pathway fields whose expectations depend on {@code pathway_mode}.
 *
 * <ul>
 *   <li>{@code length} is recommended for walkways ({@code pathway_mode=1}), fare gates ({@code
 *       pathway_mode=6}) and exit gates ({@code pathway_mode=7}).
 *   <li>{@code stair_count} is recommended for stairs ({@code pathway_mode=2}).
 *   <li>{@code traversal_time} is recommended for moving sidewalks ({@code pathway_mode=3}),
 *       escalators ({@code pathway_mode=4}) and elevators ({@code pathway_mode=5}).
 *   <li>{@code max_slope} should only be used with walkways ({@code pathway_mode=1}) and moving
 *       sidewalks ({@code pathway_mode=3}).
 * </ul>
 *
 * <p>Generated notices: {@link MissingRecommendedFieldNotice}, {@link
 * IrrelevantMaxSlopeSetForPathwayModeNotice}.
 */
@GtfsValidator
public class PathwayModeFieldsValidator extends SingleEntityValidator<GtfsPathway> {

  @Override
  public void validate(GtfsPathway entity, NoticeContainer noticeContainer) {
    GtfsPathwayMode pathwayMode = entity.pathwayMode();

    // A missing or unusable pathway_mode is already reported by the parser. Without a usable mode
    // there is nothing to check these fields against, so reporting on them here would only add
    // noise.
    if (pathwayMode == GtfsPathwayMode.UNRECOGNIZED) {
      return;
    }

    if (recommendsLength(pathwayMode) && !entity.hasLength()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsPathway.FILENAME, entity.csvRowNumber(), GtfsPathway.LENGTH_FIELD_NAME));
    }

    if (pathwayMode == GtfsPathwayMode.STAIRS && !entity.hasStairCount()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsPathway.FILENAME, entity.csvRowNumber(), GtfsPathway.STAIR_COUNT_FIELD_NAME));
    }

    if (recommendsTraversalTime(pathwayMode) && !entity.hasTraversalTime()) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              GtfsPathway.FILENAME, entity.csvRowNumber(), GtfsPathway.TRAVERSAL_TIME_FIELD_NAME));
    }

    // The spec defines both an empty max_slope and a max_slope of 0 as "no slope", so a zero value
    // carries no more meaning than leaving the field out and is not worth reporting.
    if (!allowsMaxSlope(pathwayMode) && entity.hasMaxSlope() && entity.maxSlope() != 0) {
      noticeContainer.addValidationNotice(new IrrelevantMaxSlopeSetForPathwayModeNotice(entity));
    }
  }

  private static boolean recommendsLength(GtfsPathwayMode pathwayMode) {
    switch (pathwayMode) {
      case WALKWAY:
      case FARE_GATE:
      case EXIT_GATE:
        return true;
      default:
        return false;
    }
  }

  private static boolean recommendsTraversalTime(GtfsPathwayMode pathwayMode) {
    switch (pathwayMode) {
      case MOVING_SIDEWALK:
      case ESCALATOR:
      case ELEVATOR:
        return true;
      default:
        return false;
    }
  }

  private static boolean allowsMaxSlope(GtfsPathwayMode pathwayMode) {
    switch (pathwayMode) {
      case WALKWAY:
      case MOVING_SIDEWALK:
        return true;
      default:
        return false;
    }
  }

  /**
   * A pathway that is not a walkway or a moving sidewalk defines `max_slope`.
   *
   * <p>The GTFS specification states that `max_slope` should only be used with walkways
   * (`pathway_mode=1`) and moving sidewalks (`pathway_mode=3`). A `max_slope` of `0` means no slope
   * and is not reported.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs({GtfsPathwaySchema.class}))
  static class IrrelevantMaxSlopeSetForPathwayModeNotice extends ValidationNotice {
    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The `pathway_id` of the faulty record. */
    private final String pathwayId;

    /** The `pathway_mode` of the faulty record. */
    private final int pathwayMode;

    /** The `max_slope` defined on the faulty record. */
    private final double maxSlope;

    IrrelevantMaxSlopeSetForPathwayModeNotice(GtfsPathway pathway) {
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayId = pathway.pathwayId();
      this.pathwayMode = pathway.pathwayMode().getNumber();
      this.maxSlope = pathway.maxSlope();
    }
  }
}
