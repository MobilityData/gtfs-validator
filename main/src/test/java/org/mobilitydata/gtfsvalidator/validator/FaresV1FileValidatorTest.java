package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import junit.framework.TestCase;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.ConditionallyForbiddenFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareAttributeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFareRuleTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;

public class FaresV1FileValidatorTest extends TestCase {

  private NoticeContainer noticeContainer = new NoticeContainer();

  @Test
  public void testFareAttributesPresentAndFareRulesPresent() {
    new FaresV1FileValidator(
            new GtfsFareAttributeTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS),
            new GtfsFareRuleTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testFareAttributesPresentAndFareRulesPresentByEmpty() {
    // We still consider an empty file to be "present".
    new FaresV1FileValidator(
            new GtfsFareAttributeTableContainer(TableStatus.EMPTY_FILE),
            new GtfsFareRuleTableContainer(TableStatus.EMPTY_FILE))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testFareAttributesMissingAndFareRulesMissing() {
    new FaresV1FileValidator(
            new GtfsFareAttributeTableContainer(TableStatus.MISSING_FILE),
            new GtfsFareRuleTableContainer(TableStatus.MISSING_FILE))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void testFareAttributesPresentButFareRulesMissing() {
    new FaresV1FileValidator(
            new GtfsFareAttributeTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS),
            new GtfsFareRuleTableContainer(TableStatus.MISSING_FILE))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFileNotice("fare_rules.txt"));
  }

  @Test
  public void testFareAttributesMissingButFareRulesPresent() {
    new FaresV1FileValidator(
            new GtfsFareAttributeTableContainer(TableStatus.MISSING_FILE),
            new GtfsFareRuleTableContainer(TableStatus.PARSABLE_HEADERS_AND_ROWS))
        .validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new ConditionallyForbiddenFileNotice("fare_rules.txt"));
  }
}
