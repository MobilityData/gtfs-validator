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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoSchema;

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
   * One of `feed_start_date` or `feed_end_date` is specified, but not both.
   *
   * <p>Even though `feed_info.start_date` and `feed_info.end_date` are optional, if one field is
   * provided the second one should also be provided.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs(GtfsFeedInfoSchema.class),
      bestPractices = @FileRefs(GtfsFeedInfoSchema.class))
  static class MissingFeedInfoDateNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** Either `feed_end_date` or `feed_start_date`. */
    private final String fieldName;

    MissingFeedInfoDateNotice(int csvRowNumber, String fieldName) {
      super();
      this.csvRowNumber = csvRowNumber;
      this.fieldName = fieldName;
    }
  }
}
