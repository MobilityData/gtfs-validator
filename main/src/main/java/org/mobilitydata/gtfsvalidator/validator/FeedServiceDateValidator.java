package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

/**
 * Validates that start_date <= end_date for all rows in "feed_info.txt".
 *
 * Generated notices:
 * * StartAndEndDateOutOfOrderNotice
 */
@GtfsValidator
public class FeedServiceDateValidator extends FileValidator {
    @Inject
    GtfsFeedInfoTableContainer feedInfoTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        for (GtfsFeedInfo feedInfo : feedInfoTable.getEntities()) {
            if (feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate() && feedInfo.feedStartDate().isAfter(feedInfo.feedEndDate())) {
                noticeContainer.addNotice(new StartAndEndDateOutOfOrderNotice(feedInfoTable.gtfsFilename(),
                        feedInfo.csvRowNumber(),
                        feedInfo.feedStartDate(), feedInfo.feedEndDate()));
            }
        }
    }
}
