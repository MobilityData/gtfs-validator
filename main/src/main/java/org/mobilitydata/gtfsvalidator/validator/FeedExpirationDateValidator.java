/*
 * Copyright 2021 MobilityData IO
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

import java.time.LocalDate;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.UrlRef;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Validates a feed's expiration date: 1) At any time, the published GTFS dataset should be valid
 * for at least the next 7 days 2) If possible, the GTFS dataset should cover at least the next 30
 * days of service.
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link FeedExpirationDate7DaysNotice}
 *   <li>{@link FeedExpirationDate30DaysNotice}
 * </ul>
 */
@GtfsValidator
public class FeedExpirationDateValidator extends SingleEntityValidator<GtfsFeedInfo> {

  private final CurrentDateTime currentDateTime;

  @Inject
  FeedExpirationDateValidator(CurrentDateTime currentDateTime) {
    this.currentDateTime = currentDateTime;
  }

  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
    if (entity.hasFeedEndDate()) {
      LocalDate now = currentDateTime.getNow().toLocalDate();
      GtfsDate currentDate = GtfsDate.fromLocalDate(now);
      GtfsDate currentDatePlusSevenDays = GtfsDate.fromLocalDate(now.plusDays(7));
      GtfsDate currentDatePlusThirtyDays = GtfsDate.fromLocalDate(now.plusDays(30));
      if (entity.feedEndDate().compareTo(currentDatePlusSevenDays) <= 0) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDate7DaysNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusSevenDays));
        return;
      }
      if (entity.feedEndDate().compareTo(currentDatePlusThirtyDays) <= 0) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDate30DaysNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusThirtyDays));
      }
    }
  }

  @GtfsValidationNotice(severity = WARNING)
  static class FeedExpirationDate7DaysNotice extends ValidationNotice {

    // The row number of the faulty record.
    private final int csvRowNumber;

    // Current date (YYYYMMDD format).
    private final GtfsDate currentDate;

    // Feed end date (YYYYMMDD format).
    private final GtfsDate feedEndDate;

    // Suggested expiration date (YYYYMMDD format).
    private final GtfsDate suggestedExpirationDate;

    FeedExpirationDate7DaysNotice(
        int csvRowNumber,
        GtfsDate currentDate,
        GtfsDate feedEndDate,
        GtfsDate suggestedExpirationDate) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.currentDate = currentDate;
      this.feedEndDate = feedEndDate;
      this.suggestedExpirationDate = suggestedExpirationDate;
    }
  }

  @GtfsValidationNotice(
      severity = WARNING,
      urls = {
        @UrlRef(
            label = "General Publishing & General Practices",
            url = "https://gtfs.org/best-practices/#dataset-publishing--general-practices")
      })
  static class FeedExpirationDate30DaysNotice extends ValidationNotice {

    // The row number of the faulty record.
    private final int csvRowNumber;

    // Current date (YYYYMMDD format).
    private final GtfsDate currentDate;

    // Feed end date (YYYYMMDD format).
    private final GtfsDate feedEndDate;

    // Suggested expiration date (YYYYMMDD format).
    private final GtfsDate suggestedExpirationDate;

    FeedExpirationDate30DaysNotice(
        int csvRowNumber,
        GtfsDate currentDate,
        GtfsDate feedEndDate,
        GtfsDate suggestedExpirationDate) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.currentDate = currentDate;
      this.feedEndDate = feedEndDate;
      this.suggestedExpirationDate = suggestedExpirationDate;
    }
  }
}
