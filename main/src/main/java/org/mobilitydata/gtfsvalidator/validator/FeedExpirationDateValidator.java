/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import java.time.LocalDate;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.FeedExpirationDateNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

/**
 * Validates a feed's expiration date
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link FeedExpirationDateNotice}
 * </ul>
 */
@GtfsValidator
public class FeedExpirationDateValidator extends SingleEntityValidator<GtfsFeedInfo> {

  @Override
  public void validate(GtfsFeedInfo entity, NoticeContainer noticeContainer) {
    if (entity.hasFeedEndDate()) {
      GtfsDate currentDate = GtfsDate.fromLocalDate(LocalDate.now());
      GtfsDate currentDatePlusSevenDays = GtfsDate.fromLocalDate(LocalDate.now().plusDays(7));
      GtfsDate currentDatePlusThirtyDays = GtfsDate.fromLocalDate(LocalDate.now().plusDays(30));
      if (entity.feedEndDate().isBefore(currentDatePlusSevenDays)) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDateNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusSevenDays));
      }
      if (entity.feedEndDate().equals(currentDatePlusSevenDays)
          || entity.feedEndDate().isAfter(currentDatePlusSevenDays)
              && (entity.feedEndDate().equals(currentDatePlusThirtyDays)
                  || entity.feedEndDate().isBefore(currentDatePlusThirtyDays))) {
        noticeContainer.addValidationNotice(
            new FeedExpirationDateNotice(
                entity.csvRowNumber(),
                currentDate,
                entity.feedEndDate(),
                currentDatePlusThirtyDays));
      }
    }
  }
}
