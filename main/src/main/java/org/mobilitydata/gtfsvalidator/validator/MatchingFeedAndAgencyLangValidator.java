package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

@GtfsValidator
public class MatchingFeedAndAgencyLangValidator extends FileValidator{
    @Inject
    GtfsFeedInfoTableContainer feedIntoTable;
    @Inject
    GtfsAgencyTableContainer agencyTable;

    @Override
    public void validate(NoticeContainer noticeContainer){}
}
