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
import org.mobilitydata.gtfsvalidator.notice.MissingFeedInfoDateNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableLoader;

/**
 * Validates 3 rules: 1) start_date &lt;= end_date 2) feed_info.start_date is provided if
 * feed_info.end_date is provided 3) feed_info.end_date is provided if feed_info.start_date is
 * provided.
 *
 * <p>Generated notice: {@link StartAndEndDateOutOfOrderNotice}.
 */
@GtfsValidator
public class FeedServiceDateValidator extends SingleEntityValidator<GtfsFeedInfo> {

  @Override
  public void validate(GtfsFeedInfo feedInfo, NoticeContainer noticeContainer) {
    if (feedInfo.hasFeedStartDate() && !feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(
              feedInfo.csvRowNumber(), GtfsFeedInfoTableLoader.FEED_END_DATE_FIELD_NAME));
    } else if (!feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(
              feedInfo.csvRowNumber(), GtfsFeedInfoTableLoader.FEED_START_DATE_FIELD_NAME));
    }
    if (feedInfo.hasFeedStartDate()
        && feedInfo.hasFeedEndDate()
        && feedInfo.feedStartDate().isAfter(feedInfo.feedEndDate())) {
      noticeContainer.addValidationNotice(
          new StartAndEndDateOutOfOrderNotice(
              GtfsFeedInfoTableLoader.FILENAME,
              feedInfo.csvRowNumber(),
              feedInfo.feedStartDate(),
              feedInfo.feedEndDate()));
    }
  }
}
