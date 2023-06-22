package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

@GtfsValidator
public class FeedContactValidator extends SingleEntityValidator<GtfsFeedInfo>{

    @Override
    public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
        if (entity.feedContactEmail().isBlank() && entity.feedContactUrl().isBlank()) {
            noticeContainer.addValidationNotice(new FeedContactNotice(
                    entity.csvRowNumber(),
                    entity.feedContactEmail(),
                    entity.feedContactUrl()
            ));
        }
    }

    @GtfsValidationNotice(
            severity = WARNING,
            files = @GtfsValidationNotice.FileRefs(GtfsFeedInfo.class),
            urls = {
                    @GtfsValidationNotice.UrlRef(
                            label = "Original Python validator implementation",
                            url = "https://github.com/google/transitfeed")
            })
    static class FeedContactNotice extends ValidationNotice {
        /** The row number of the faulty record. */
        private final int rowNumber;

        private final String feedContactEmail;

        private final String feedContactUrl;


        FeedContactNotice(int rowNumber, String feedContactEmail, String feedContactUrl) {
            this.rowNumber = rowNumber;
            this.feedContactEmail = feedContactEmail;
            this.feedContactUrl = feedContactUrl;
        }
    }
}
