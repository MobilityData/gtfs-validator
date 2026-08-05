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
public class PathwayStairCountValidatorTest {

  @Test
  public void stairsWithoutStairCount_yieldsNotice() {
    assertThat(validationNoticesFor(pathwayBuilder(GtfsPathwayMode.STAIRS).build()))
        .containsExactly(new MissingRecommendedFieldNotice("pathways.txt", 2, "stair_count"));
  }

  @Test
  public void stairsWithStairCount_yieldsNoNotice() {
    assertThat(
            validationNoticesFor(pathwayBuilder(GtfsPathwayMode.STAIRS).setStairCount(5).build()))
        .isEmpty();
  }

  @Test
  public void stairsWithNegativeStairCount_yieldsNoNotice() {
    // stair_count is signed: a negative value describes a descending path, so it is still defined.
    assertThat(
            validationNoticesFor(pathwayBuilder(GtfsPathwayMode.STAIRS).setStairCount(-5).build()))
        .isEmpty();
  }

  @Test
  public void nonStairsWithoutStairCount_yieldsNoNotice() {
    for (GtfsPathwayMode mode : GtfsPathwayMode.values()) {
      if (mode == GtfsPathwayMode.STAIRS) {
        continue;
      }
      assertThat(validationNoticesFor(pathwayBuilder(mode).build())).isEmpty();
    }
  }

  private static GtfsPathway.Builder pathwayBuilder(GtfsPathwayMode pathwayMode) {
    return new GtfsPathway.Builder()
        .setCsvRowNumber(2)
        .setPathwayId("pathway1")
        .setFromStopId("stop1")
        .setToStopId("stop2")
        .setPathwayMode(pathwayMode);
  }

  private static List<ValidationNotice> validationNoticesFor(GtfsPathway entity) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayStairCountValidator().validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
