package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRule;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleTransferCountValidator.FareTransferRuleInvalidTransferCountNotice;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleTransferCountValidator.FareTransferRuleMissingTransferCountNotice;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleTransferCountValidator.FareTransferRuleWithForbiddenTransferCountNotice;

public class FareTransferRuleTransferCountValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsFareTransferRule rule) {
    NoticeContainer noticeContainer = new NoticeContainer();
    FareTransferRuleTransferCountValidator validator = new FareTransferRuleTransferCountValidator();
    validator.validate(rule, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void validTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("a")
                    .setTransferCount(1)
                    .build()))
        .isEmpty();

    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("a")
                    .setTransferCount(-1)
                    .build()))
        .isEmpty();
  }

  @Test
  public void invalidTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("a")
                    .setTransferCount(0)
                    .build()))
        .containsExactly(new FareTransferRuleInvalidTransferCountNotice(2, 0));
  }

  @Test
  public void invalidNegativeTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("a")
                    .setTransferCount(-2)
                    .build()))
        .containsExactly(new FareTransferRuleInvalidTransferCountNotice(2, -2));
  }

  @Test
  public void missingRequiredTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("a")
                    .build()))
        .containsExactly(new FareTransferRuleMissingTransferCountNotice(2));
  }

  @Test
  public void validUnspecifiedTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("b")
                    .build()))
        .isEmpty();
  }

  @Test
  public void forbiddenTransferCount() {
    assertThat(
            generateNotices(
                new GtfsFareTransferRule.Builder()
                    .setCsvRowNumber(2)
                    .setFromLegGroupId("a")
                    .setToLegGroupId("b")
                    .setTransferCount(1)
                    .build()))
        .containsExactly(new FareTransferRuleWithForbiddenTransferCountNotice(2));
  }
}
