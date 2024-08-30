package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayIsBidirectional;

@RunWith(JUnit4.class)
public class BidirectionalExitGateValidatorTest {
  public static GtfsPathway createPathway(
      int csvRowNumber,
      Integer pathwayMode,
      GtfsPathwayIsBidirectional gtfsPathwayIsBidirectional) {
    return new GtfsPathway.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setPathwayMode(pathwayMode)
        .setIsBidirectional(gtfsPathwayIsBidirectional)
        .build();
  }

  /** Tests that a pathway with bidirectional exit gates generates a notice. */
  @Test
  public void isBidirectionalExitGateShouldGenerateNotice() {
    GtfsPathway entity = createPathway(1, 7, GtfsPathwayIsBidirectional.BIDIRECTIONAL);
    NoticeContainer noticeContainer = new NoticeContainer();
    new BidirectionalExitGateValidator().validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new BidirectionalExitGateValidator.BidirectionalExitGatesNotice(entity));
  }

  @Test
  public void isNotBidirectionalExitGateShouldNotGenerateNotice() {
    GtfsPathway entity = createPathway(1, 7, GtfsPathwayIsBidirectional.UNIDIRECTIONAL);
    NoticeContainer noticeContainer = new NoticeContainer();
    new BidirectionalExitGateValidator().validate(entity, noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
