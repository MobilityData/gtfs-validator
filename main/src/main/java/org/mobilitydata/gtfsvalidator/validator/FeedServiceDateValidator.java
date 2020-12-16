/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Validates that start_date <= end_date for all rows in "feed_info.txt".
 * <p>
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
            if (feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate()) {
                GtfsDate startDate = feedInfo.feedStartDate();
                GtfsDate endDate = feedInfo.feedEndDate();
                if (startDate.equals(endDate)) {
                    return;
                }
                if (feedInfo.feedStartDate().isAfter(feedInfo.feedEndDate())) {
                    noticeContainer.addNotice(
                            new StartAndEndDateOutOfOrderNotice(
                                    feedInfoTable.gtfsFilename(),
                                    feedInfo.csvRowNumber(),
                                    feedInfo.feedStartDate(),
                                    feedInfo.feedEndDate()));
                }
            }
        }
    }
}
