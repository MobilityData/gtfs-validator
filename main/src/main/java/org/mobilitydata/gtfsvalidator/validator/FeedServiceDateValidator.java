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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;

/**
 * Validates that if one of {@code (start_date, end_date)} fields is provided for {@code
 * feed_info.txt}, then the second field is also provided.
 *
 * <p>Generated notice: {@link MissingFeedInfoDateNotice}.
 */
@GtfsValidator
public class FeedServiceDateValidator extends SingleEntityValidator<GtfsFeedInfo> {


  @Override
  public void validate(GtfsFeedInfo feedInfo, NoticeContainer noticeContainer) {
    if (feedInfo.hasFeedStartDate() && !feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_end_date"));
    } else if (!feedInfo.hasFeedStartDate() && feedInfo.hasFeedEndDate()) {
      noticeContainer.addValidationNotice(
          new MissingFeedInfoDateNotice(feedInfo.csvRowNumber(), "feed_start_date"));
    }
  }

  /**
   * Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is
   * provided the second one should also be provided.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}
   */
  static class MissingFeedInfoDateNotice extends ValidationNotice {
    MissingFeedInfoDateNotice(long csvRowNumber, String fieldName) {
      super(
          ImmutableMap.of("csvRowNumber", csvRowNumber, "fieldName", fieldName),
          SeverityLevel.WARNING);
    }
  }
}
