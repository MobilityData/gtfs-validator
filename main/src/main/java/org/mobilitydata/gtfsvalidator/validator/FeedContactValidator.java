package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;

@GtfsValidator
public class FeedContactValidator extends SingleEntityValidator<GtfsFeedInfo> {

  /**
   * Best Practices https://gtfs.org/schedule/best-practices/#feed_infotxt
   * suggest providing at least one of feed_contact_email and feed_contact_url
   * @param entity
   * @param noticeContainer
   */
  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
    if (!entity.hasFeedContactEmail() && !entity.hasFeedContactUrl()
        || entity.feedContactEmail().isBlank() && entity.feedContactUrl().isBlank()) {
      noticeContainer.addValidationNotice(
          new FeedContactNotice(
              entity.csvRowNumber(), entity.feedContactEmail(), entity.feedContactUrl()));
    }
  }

  /**
   * Only generates a warning when both feed_contact_email and feed_contact_url are unset
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @GtfsValidationNotice.FileRefs(GtfsFeedInfo.class),
      urls = {
        @GtfsValidationNotice.UrlRef(
            label = "Original Python validator implementation",
            url = "https://github.com/google/transitfeed")
      })
  static class FeedContactNotice extends ValidationNotice {
    /** The row number of the validated record. */
    private final int rowNumber;

    /** The email contact information of a feed. */
    private final String feedContactEmail;

    /** The url contact information of a feed. */
    private final String feedContactUrl;

    FeedContactNotice(int rowNumber, String feedContactEmail, String feedContactUrl) {
      this.rowNumber = rowNumber;
      this.feedContactEmail = feedContactEmail;
      this.feedContactUrl = feedContactUrl;
    }
  }
}
