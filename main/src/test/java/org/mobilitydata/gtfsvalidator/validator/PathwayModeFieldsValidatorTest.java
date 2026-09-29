package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;

@RunWith(JUnit4.class)
public class PathwayModeFieldsValidatorTest {

  // stair_count is recommended for stairs (pathway_mode=2).

  @Test
  public void stairsWithoutStairCount_yieldsNotice() {
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.STAIRS).build()))
        .containsExactly(new MissingRecommendedFieldNotice("pathways.txt", 2, "stair_count"));
  }

  @Test
  public void stairsWithStairCount_yieldsNoNotice() {
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.STAIRS).setStairCount(5).build()))
        .isEmpty();
  }

  @Test
  public void stairsWithNegativeStairCount_yieldsNoNotice() {
    // The spec says a negative stair_count describes walking down, so it is still defined.
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.STAIRS).setStairCount(-5).build()))
        .isEmpty();
  }

  @Test
  public void nonStairsWithoutStairCount_yieldsNoStairCountNotice() {
    for (GtfsPathwayMode mode : GtfsPathwayMode.values()) {
      if (mode == GtfsPathwayMode.STAIRS) {
        continue;
      }
      assertThat(validationNoticesFor(pathway(mode).setTraversalTime(30).build()))
          .doesNotContain(new MissingRecommendedFieldNotice("pathways.txt", 2, "stair_count"));
    }
  }

  // traversal_time is recommended for moving sidewalks, escalators and elevators.

  @Test
  public void mechanicalPathwaysWithoutTraversalTime_yieldNotice() {
    for (GtfsPathwayMode mode :
        List.of(
            GtfsPathwayMode.MOVING_SIDEWALK, GtfsPathwayMode.ESCALATOR, GtfsPathwayMode.ELEVATOR)) {
      assertThat(validationNoticesFor(pathway(mode).build()))
          .containsExactly(new MissingRecommendedFieldNotice("pathways.txt", 2, "traversal_time"));
    }
  }

  @Test
  public void mechanicalPathwaysWithTraversalTime_yieldNoNotice() {
    for (GtfsPathwayMode mode :
        List.of(
            GtfsPathwayMode.MOVING_SIDEWALK, GtfsPathwayMode.ESCALATOR, GtfsPathwayMode.ELEVATOR)) {
      assertThat(validationNoticesFor(pathway(mode).setTraversalTime(45).build())).isEmpty();
    }
  }

  @Test
  public void walkwayWithoutTraversalTime_yieldsNoNotice() {
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.WALKWAY).setLength(12.0).build()))
        .isEmpty();
  }

  // length is recommended for walkways, fare gates and exit gates.

  @Test
  public void pathwaysRecommendingLengthWithoutLength_yieldNotice() {
    for (GtfsPathwayMode mode :
        List.of(GtfsPathwayMode.WALKWAY, GtfsPathwayMode.FARE_GATE, GtfsPathwayMode.EXIT_GATE)) {
      assertThat(validationNoticesFor(pathway(mode).build()))
          .containsExactly(new MissingRecommendedFieldNotice("pathways.txt", 2, "length"));
    }
  }

  @Test
  public void pathwaysRecommendingLengthWithLength_yieldNoNotice() {
    for (GtfsPathwayMode mode :
        List.of(GtfsPathwayMode.WALKWAY, GtfsPathwayMode.FARE_GATE, GtfsPathwayMode.EXIT_GATE)) {
      assertThat(validationNoticesFor(pathway(mode).setLength(12.0).build())).isEmpty();
    }
  }

  @Test
  public void zeroLength_yieldsNoNotice() {
    // Unlike max_slope, the spec gives no special meaning to a length of 0, so it counts as
    // defined.
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.WALKWAY).setLength(0.0).build()))
        .isEmpty();
  }

  @Test
  public void stairsWithoutLength_yieldsNoLengthNotice() {
    assertThat(validationNoticesFor(pathway(GtfsPathwayMode.STAIRS).setStairCount(5).build()))
        .doesNotContain(new MissingRecommendedFieldNotice("pathways.txt", 2, "length"));
  }

  // max_slope should only be used with walkways and moving sidewalks.

  @Test
  public void maxSlopeOnDisallowedMode_yieldsNotice() {
    GtfsPathway entity =
        pathway(GtfsPathwayMode.ELEVATOR).setMaxSlope(0.083).setTraversalTime(30).build();
    assertThat(validationNoticesFor(entity))
        .containsExactly(
            new PathwayModeFieldsValidator.IrrelevantMaxSlopeSetForPathwayModeNotice(entity));
  }

  @Test
  public void maxSlopeOnWalkwayOrMovingSidewalk_yieldsNoNotice() {
    assertThat(
            validationNoticesFor(
                pathway(GtfsPathwayMode.WALKWAY).setMaxSlope(0.083).setLength(12.0).build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                pathway(GtfsPathwayMode.MOVING_SIDEWALK)
                    .setMaxSlope(0.083)
                    .setTraversalTime(30)
                    .build()))
        .isEmpty();
  }

  @Test
  public void zeroMaxSlopeOnDisallowedMode_yieldsNoNotice() {
    // The spec treats an empty max_slope and a max_slope of 0 alike, both meaning no slope.
    GtfsPathway entity =
        pathway(GtfsPathwayMode.ELEVATOR).setMaxSlope(0.0).setTraversalTime(30).build();
    assertThat(validationNoticesFor(entity)).isEmpty();
  }

  @Test
  public void negativeMaxSlopeOnDisallowedMode_yieldsNotice() {
    // A negative slope describes a downward pathway, so it is a real value.
    GtfsPathway entity =
        pathway(GtfsPathwayMode.EXIT_GATE).setMaxSlope(-0.05).setLength(12.0).build();
    assertThat(validationNoticesFor(entity))
        .containsExactly(
            new PathwayModeFieldsValidator.IrrelevantMaxSlopeSetForPathwayModeNotice(entity));
  }

  // An unusable pathway_mode is reported by the parser, so this validator stays quiet.

  @Test
  public void maxSlopeOnUnrecognizedMode_yieldsNoNotice() {
    GtfsPathway entity =
        new GtfsPathway.Builder()
            .setCsvRowNumber(2)
            .setPathwayId("pathway1")
            .setFromStopId("stop1")
            .setToStopId("stop2")
            .setPathwayMode(9)
            .setMaxSlope(0.083)
            .build();
    assertThat(validationNoticesFor(entity)).isEmpty();
  }

  @Test
  public void maxSlopeWithoutPathwayMode_yieldsNoNotice() {
    GtfsPathway entity =
        new GtfsPathway.Builder()
            .setCsvRowNumber(2)
            .setPathwayId("pathway1")
            .setFromStopId("stop1")
            .setToStopId("stop2")
            .setMaxSlope(0.083)
            .build();
    assertThat(validationNoticesFor(entity)).isEmpty();
  }

  private static GtfsPathway.Builder pathway(GtfsPathwayMode pathwayMode) {
    return new GtfsPathway.Builder()
        .setCsvRowNumber(2)
        .setPathwayId("pathway1")
        .setFromStopId("stop1")
        .setToStopId("stop2")
        .setPathwayMode(pathwayMode);
  }

  private static List<ValidationNotice> validationNoticesFor(GtfsPathway entity) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayModeFieldsValidator().validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
