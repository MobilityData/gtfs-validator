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

import com.google.common.collect.ImmutableMap;
import java.time.LocalDate;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
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
 *   <li>{@link FeedExpirationDateNotice}
 * </ul>
 */
@GtfsValidator
public class FeedExpirationDateValidator extends SingleEntityValidator<GtfsFeedInfo> {
  private final ValidationContext context;

  @Inject
  FeedExpirationDateValidator(ValidationContext context) {
    this.context = context;
  }

  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
    if (entity.hasFeedEndDate()) {
      LocalDate now = context.now().toLocalDate();
      GtfsDate currentDate = GtfsDate.fromLocalDate(now);
      GtfsDate currentDatePlusSevenDays = GtfsDate.fromLocalDate(now.plusDays(7));
      GtfsDate currentDatePlusThirtyDays = GtfsDate.fromLocalDate(now.plusDays(30));
      if (entity.feedEndDate().isBefore(currentDatePlusSevenDays)) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDateNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusSevenDays));
        return;
      }
      if (entity.feedEndDate().compareTo(currentDatePlusThirtyDays) <= 0) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDateNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusThirtyDays));
      }
    }
  }

  static class FeedExpirationDateNotice extends ValidationNotice {
    FeedExpirationDateNotice(
        long csvRowNumber,
        GtfsDate currentDate,
        GtfsDate feedEndDate,
        GtfsDate suggestedExpirationDate) {
      super(
          ImmutableMap.of(
              "csvRowNumber",
              csvRowNumber,
              "currentDate",
              currentDate.toYYYYMMDD(),
              "feedEndDate",
              feedEndDate.toYYYYMMDD(),
              "suggestedExpirationDate",
              suggestedExpirationDate),
          SeverityLevel.WARNING);
    }
  }
}
