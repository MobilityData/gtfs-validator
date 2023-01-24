package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOption;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOption.Builder;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFarePaymentOptionType;
import org.mobilitydata.gtfsvalidator.validator.DuplicateFarePaymentOptionValidator.DuplicateFarePaymentOptionNotice;

@RunWith(JUnit4.class)
public class DuplicateFarePaymentOptionValidatorTest {

  private static List<ValidationNotice> generateNotices(List<GtfsFarePaymentOption> fpos) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new DuplicateFarePaymentOptionValidator(
            GtfsFarePaymentOptionTableContainer.forEntities(fpos, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void testUniqueEntries() {
    ImmutableList<GtfsFarePaymentOption> farePaymentOptions =
        ImmutableList.of(
            new Builder()
                .setCsvRowNumber(1)
                .setFarePaymentOptionId("a")
                .setFarePaymentOptionName("Cash")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.CASH)
                .build(),
            new Builder()
                .setCsvRowNumber(2)
                .setFarePaymentOptionId("b")
                .setFarePaymentOptionName("Transit Card")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(farePaymentOptions)).isEmpty();
  }

  @Test
  public void testDuplicateEntriesCash() {
    ImmutableList<GtfsFarePaymentOption> farePaymentOptions =
        ImmutableList.of(
            new Builder()
                .setCsvRowNumber(1)
                .setFarePaymentOptionId("a")
                .setFarePaymentOptionName("Cash")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.CASH)
                .build(),
            new Builder()
                .setCsvRowNumber(2)
                .setFarePaymentOptionId("b")
                .setFarePaymentOptionName("Cash")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.CASH)
                .build());

    assertThat(generateNotices(farePaymentOptions))
        .containsExactly(
            new DuplicateFarePaymentOptionNotice(
                farePaymentOptions.get(0), farePaymentOptions.get(1)));
  }

  @Test
  public void testDuplicateEntriesTransitCard() {
    ImmutableList<GtfsFarePaymentOption> farePaymentOptions =
        ImmutableList.of(
            new Builder()
                .setCsvRowNumber(1)
                .setFarePaymentOptionId("a")
                .setFarePaymentOptionName("Transit Card")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                .build(),
            new Builder()
                .setCsvRowNumber(2)
                .setFarePaymentOptionId("b")
                .setFarePaymentOptionName("Transit Card")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(farePaymentOptions))
        .containsExactly(
            new DuplicateFarePaymentOptionNotice(
                farePaymentOptions.get(0), farePaymentOptions.get(1)));
  }

  @Test
  public void testTransitCardsWithDifferentNames() {
    ImmutableList<GtfsFarePaymentOption> farePaymentOptions =
        ImmutableList.of(
            new Builder()
                .setCsvRowNumber(1)
                .setFarePaymentOptionId("a")
                .setFarePaymentOptionName("Alpha Card")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                .build(),
            new Builder()
                .setCsvRowNumber(2)
                .setFarePaymentOptionId("b")
                .setFarePaymentOptionName("Beta Card")
                .setFarePaymentOptionType(GtfsFarePaymentOptionType.TRANSIT_CARD)
                .build());

    assertThat(generateNotices(farePaymentOptions)).isEmpty();
  }
}
