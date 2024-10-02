package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

public class MissingFeedInfoValidatorTest {

  private static List<ValidationNotice> generateNotices(
      GtfsFeedInfoTableContainer feedInfoTableContainer,
      GtfsTranslationTableContainer translationTableContainer) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new MissingFeedInfoValidator(feedInfoTableContainer, translationTableContainer)
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void missingFeedInfoTranslationTableNotPresent() {
    assertThat(
            generateNotices(
                GtfsFeedInfoTableContainer.forStatus(TableStatus.MISSING_FILE),
                GtfsTranslationTableContainer.forStatus(TableStatus.MISSING_FILE)))
        .containsExactly(new MissingRecommendedFileNotice(GtfsFeedInfo.FILENAME));
  }

  @Test
  public void missingFeedInfoWhenTranslationTableIsPresent() {
    assertThat(
            generateNotices(
                GtfsFeedInfoTableContainer.forStatus(TableStatus.MISSING_FILE),
                GtfsTranslationTableContainer.forStatus(TableStatus.PARSABLE_HEADERS_AND_ROWS)))
        .contains(new MissingRequiredFileNotice(GtfsFeedInfo.FILENAME));
  }

  @Test
  public void feedInfoPresentShouldGenerateNoNotice() {
    assertThat(
            generateNotices(
                GtfsFeedInfoTableContainer.forStatus(TableStatus.PARSABLE_HEADERS_AND_ROWS),
                GtfsTranslationTableContainer.forStatus(TableStatus.PARSABLE_HEADERS_AND_ROWS)))
        .isEmpty();
  }
}
