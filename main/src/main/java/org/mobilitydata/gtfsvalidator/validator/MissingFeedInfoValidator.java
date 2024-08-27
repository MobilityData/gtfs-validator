package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;

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
        noticeContainer.addValidationNotice(new MissingRecommendedFileNotice(GtfsFeedInfo.FILENAME));
      } else {
        noticeContainer.addValidationNotice(new MissingRequiredFileNotice(GtfsFeedInfo.FILENAME));
      }
    }
  }
}
