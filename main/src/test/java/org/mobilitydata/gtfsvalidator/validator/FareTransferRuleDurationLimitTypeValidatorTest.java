package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsDurationLimitType;
import org.mobilitydata.gtfsvalidator.table.GtfsFareTransferRule;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleDurationLimitTypeValidator.FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice;
import org.mobilitydata.gtfsvalidator.validator.FareTransferRuleDurationLimitTypeValidator.FareTransferRuleDurationLimitWithoutTypeNotice;

@RunWith(JUnit4.class)
public class FareTransferRuleDurationLimitTypeValidatorTest {
  private static List<ValidationNotice> generateNotices(GtfsFareTransferRule rule) {
    NoticeContainer noticeContainer = new NoticeContainer();
    FareTransferRuleDurationLimitTypeValidator validator =
        new FareTransferRuleDurationLimitTypeValidator();
    validator.validate(rule, noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void durationWithoutTypeShouldGenerateNotice() {
    assertThat(
            generateNotices(
                GtfsFareTransferRule.builder().setCsvRowNumber(2).setDurationLimit(120).build()))
        .containsExactly(new FareTransferRuleDurationLimitWithoutTypeNotice(2));
  }

  @Test
  public void durationLimitTypeWithoutDurationLimitShouldGenerateNotice() {
    assertThat(
            generateNotices(
                GtfsFareTransferRule.builder()
                    .setCsvRowNumber(2)
                    .setDurationLimitType(GtfsDurationLimitType.ARRIVAL_TO_ARRIVAL)
                    .build()))
        .containsExactly(new FareTransferRuleDurationLimitTypeWithoutDurationLimitNotice(2));
  }

  @Test
  public void durationWithTypeShouldNotGenerateNotice() {
    assertThat(
            generateNotices(
                GtfsFareTransferRule.builder()
                    .setCsvRowNumber(2)
                    .setDurationLimit(120)
                    .setDurationLimitType(GtfsDurationLimitType.ARRIVAL_TO_ARRIVAL)
                    .build()))
        .isEmpty();
  }
}
