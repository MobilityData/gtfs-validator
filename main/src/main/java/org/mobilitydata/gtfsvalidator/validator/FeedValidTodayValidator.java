/*
 * Copyright 2026 MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Validates that the feed is valid for today's date.
 *
 * <p>If the minimum feed_info.start_date is greater than today, a FutureFeedNotice is generated.
 *
 * <p>Generated notice: {@link FutureFeedNotice}
 */
@GtfsValidator
public class FeedValidTodayValidator extends FileValidator {

  private final DateForValidation dateForValidation;
  private final GtfsFeedInfoTableContainer feedInfoTable;

  @Inject
  FeedValidTodayValidator(
      DateForValidation dateForValidation, GtfsFeedInfoTableContainer feedInfoTable) {
    this.dateForValidation = dateForValidation;
    this.feedInfoTable = feedInfoTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    GtfsDate currentDate = GtfsDate.fromLocalDate(dateForValidation.getDate());

    GtfsDate minFeedStartDate = null;
    for (GtfsFeedInfo feedInfo : feedInfoTable.getEntities()) {
      if (feedInfo.hasFeedStartDate()) {
        if (minFeedStartDate == null || feedInfo.feedStartDate().compareTo(minFeedStartDate) < 0) {
          minFeedStartDate = feedInfo.feedStartDate();
        }
      }
    }

    if (minFeedStartDate != null && minFeedStartDate.compareTo(currentDate) > 0) {
      noticeContainer.addValidationNotice(new FutureFeedNotice(minFeedStartDate, currentDate));
    }
  }

  /**
   * The feed covers the future only.
   *
   * <p>The minimum `start_date` in `feed_info.txt` is greater than today's date, indicating the
   * feed covers the future only.
   */
  @GtfsValidationNotice(severity = INFO, files = @FileRefs(GtfsFeedInfoSchema.class))
  static class FutureFeedNotice extends ValidationNotice {

    /** Feed start date. */
    private final GtfsDate feedStartDate;

    /** Current date. */
    private final GtfsDate currentDate;

    FutureFeedNotice(GtfsDate feedStartDate, GtfsDate currentDate) {
      this.feedStartDate = feedStartDate;
      this.currentDate = currentDate;
    }
  }
}
