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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingFeedInfoDateNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

/**
 * Validates that if one of {@code (start_date, end_date)} fields is provided for {@code
 * feed_info.txt}, then the second field is also provided.
 *
 * <p>Generated notice: {@link MissingFeedInfoDateNotice}.
 */
@GtfsValidator
public class FeedServiceDateValidator extends FileValidator {

  @Inject GtfsFeedInfoTableContainer feedInfoTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsFeedInfo feedInfo : feedInfoTable.getEntities()) {
      if (feedInfo.hasFeedStartDate() && !feedInfo.hasFeedEndDate()) {
        noticeContainer.addValidationNotice(
            new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_end_date"));
      } else if (!feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate()) {
        noticeContainer.addValidationNotice(
            new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_start_date"));
      }
    }
  }
}
