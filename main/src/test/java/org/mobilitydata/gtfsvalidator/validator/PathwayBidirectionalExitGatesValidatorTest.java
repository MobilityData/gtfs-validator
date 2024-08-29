package org.mobilitydata.gtfsvalidator.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;


import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class PathwayBidirectionalExitGatesValidatorTest {
  public static GtfsPathway createPathway(int csvRowNumber,  Integer pathwayMode, GtfsPathwayIsBidirectional gtfsPathwayIsBidirectional) {
    return new GtfsPathway.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setPathwayMode(pathwayMode)
            .setIsBidirectional(gtfsPathwayIsBidirectional)
            .build();
  }

  /**
   * Tests that a pathway with bidirectional exit gates generates a notice.
   */
  @Test
  public void isBidirectionalExitGatesShouldGenerateNotice() {
    GtfsPathway entity = createPathway(1, 7, GtfsPathwayIsBidirectional.BIDIRECTIONAL);
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayBidirectionalExitGatesValidator()
            .validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).containsExactly(
            new PathwayBidirectionalExitGatesValidator.PathwayBidirectionalExitGatesNotice(entity));
  }

  @Test
  public void isNotBidirectionalExitGatesShouldNotGenerateNotice() {
    GtfsPathway entity = createPathway(1, 7, GtfsPathwayIsBidirectional.UNIDIRECTIONAL);
    NoticeContainer noticeContainer = new NoticeContainer();
    new PathwayBidirectionalExitGatesValidator()
            .validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
