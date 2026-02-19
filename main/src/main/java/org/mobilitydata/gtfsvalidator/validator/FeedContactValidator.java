package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;

/**
 * Validate that if the dataset provides at least one of feed_contact_email and feed_contact_url in
 * "feed_info.txt". *
 *
 * <p>Generated notice: {@link MissingFeedContactEmailAndUrlNotice}.
 */
@GtfsValidator
public class FeedContactValidator extends SingleEntityValidator<GtfsFeedInfo> {

  /**
   * Only generates a warning when both feed_contact_email and feed_contact_url are unset
   *
   * <p>There should be no warning generated when the dataset has one of feed_contact_email and
   * feed_contact_url.
   *
   * @param entity
   * @param noticeContainer
   */
  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
    if ((!entity.hasFeedContactEmail() || entity.feedContactEmail().isBlank())
        && (!entity.hasFeedContactUrl() || entity.feedContactUrl().isBlank())) {
      noticeContainer.addValidationNotice(
          new MissingFeedContactEmailAndUrlNotice(entity.csvRowNumber()));
    }
  }

  /**
   * Best Practices for `feed_info.txt` suggest providing at least one of `feed_contact_email` and
   * `feed_contact_url`.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @GtfsValidationNotice.FileRefs(GtfsFeedInfo.class))

  static class MissingFeedContactEmailAndUrlNotice extends ValidationNotice {
    /** The row number of the validated record. */
    private final int csvRowNumber;

    MissingFeedContactEmailAndUrlNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
    }
  }
}
