package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMedia;
import org.mobilitydata.gtfsvalidator.table.GtfsFareMediaType;

@RunWith(JUnit4.class)
public class FareMediaNameValidatorTest {

  @Test
  public void testTransitCard() {
    assertThat(
            validationNoticesFor(
                new GtfsFareMedia.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediaName("Go! Pass")
                    .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFareMedia.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediaType(GtfsFareMediaType.TRANSIT_CARD)
                    .build()))
        .containsExactly(new MissingRecommendedFieldNotice("fare_media.txt", 2, "fare_media_name"));
  }

  @Test
  public void testPaperTicket() {
    assertThat(
            validationNoticesFor(
                new GtfsFareMedia.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediaName("Some Ticket")
                    .setFareMediaType(GtfsFareMediaType.PAPER_TICKET)
                    .build()))
        .isEmpty();
    assertThat(
            validationNoticesFor(
                new GtfsFareMedia.Builder()
                    .setCsvRowNumber(2)
                    .setFareMediaType(GtfsFareMediaType.PAPER_TICKET)
                    .build()))
        .isEmpty();
  }

  private List<ValidationNotice> validationNoticesFor(GtfsFareMedia entity) {
    FareMediaNameValidator validator = new FareMediaNameValidator();
    NoticeContainer noticeContainer = new NoticeContainer();
    validator.validate(entity, noticeContainer);
    return noticeContainer.getValidationNotices();
  }
}
