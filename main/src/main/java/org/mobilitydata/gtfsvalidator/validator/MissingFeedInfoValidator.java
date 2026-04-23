package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;

/**
 * The `MissingFeedInfoValidator` class is responsible for validating the presence of the
 * `feed_info.txt` file in a GTFS feed. If the `feed_info.txt` file is missing, it generates
 * appropriate validation notices.
 *
 * <p>The validation logic is as follows: - If the `feed_info.txt` file is missing and the
 * `translations.txt` file is also missing, a `MissingRecommendedFileNotice` is generated. - If the
 * `feed_info.txt` file is missing but the `translations.txt` file is present, a
 * `MissingRequiredFileNotice` is generated.
 *
 * <p>This validator is part of the GTFS validation framework and is annotated with `@GtfsValidator`
 * to indicate its role.
 *
 * <p>Dependencies: - `GtfsFeedInfoTableContainer`: Provides access to the `feed_info.txt` file
 * data. - `GtfsTranslationTableContainer`: Provides access to the `translations.txt` file data. -
 * `NoticeContainer`: Collects validation notices generated during the validation process.
 */
@GtfsValidator
public class MissingFeedInfoValidator extends FileValidator {

  private final GtfsFeedInfoTableContainer feedInfoTable;
  private final GtfsTranslationTableContainer translationTable;

  @Inject
  public MissingFeedInfoValidator(
      GtfsFeedInfoTableContainer feedInfoTable, GtfsTranslationTableContainer translationTable) {
    this.feedInfoTable = feedInfoTable;
    this.translationTable = translationTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (feedInfoTable.isMissingFile()) {
      if (translationTable.isMissingFile()) {
        noticeContainer.addValidationNotice(
            new MissingRecommendedFileNotice(GtfsFeedInfo.FILENAME));
      } else {
        noticeContainer.addValidationNotice(new MissingRequiredFileNotice(GtfsFeedInfo.FILENAME));
      }
    }
  }
}
