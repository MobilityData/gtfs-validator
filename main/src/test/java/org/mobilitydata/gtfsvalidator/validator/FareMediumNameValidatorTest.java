package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedium;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediumType;

@RunWith(JUnit4.class)
public class FareMediumNameValidatorTest {

  @Test
  public void testTransitCard() {
    assertThat(
            validationNoticesFor(
                new GtfsFareMedium.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediumName("Go! Pass")
                    .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFareMedium.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediumType(GtfsFareMediumType.TRANSIT_CARD)
                    .build()))
        .containsExactly(
            new MissingRecommendedFieldNotice("fare_media.txt", 2, "fare_medium_name"));
  }

  @Test
  public void testPaperTicket() {
    assertThat(
            validationNoticesFor(
                new GtfsFareMedium.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediumName("Some Ticket")
                    .setFareMediumType(GtfsFareMediumType.PAPER_TICKET)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFareMedium.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediumType(GtfsFareMediumType.PAPER_TICKET)
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validationNoticesFor(GtfsFareMedium entity) {
    FareMediumNameValidator validator = new FareMediumNameValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
