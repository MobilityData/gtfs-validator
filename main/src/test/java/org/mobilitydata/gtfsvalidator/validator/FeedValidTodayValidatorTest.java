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

import static com.google.common.truth.Truth.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.FeedValidTodayValidator.FutureFeedNotice;

public class FeedValidTodayValidatorTest {

  private List<ValidationNotice> validateFeedInfo(GtfsFeedInfoTableContainer feedInfoTable) {
    NoticeContainer container = new NoticeContainer();
    new FeedValidTodayValidator(feedInfoTable).validate(container);
    return container.getValidationNotices();
  }

  private GtfsFeedInfo createFeedInfo(int csvRowNumber, GtfsDate feedStartDate) {
    GtfsFeedInfo.Builder builder =
        new GtfsFeedInfo.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setFeedPublisherName("feed publisher name value")
            .setFeedPublisherUrl("https://www.mobilitydata.org")
            .setFeedLang(Locale.CANADA);
    if (feedStartDate != null) {
      builder.setFeedStartDate(feedStartDate);
    }
    return builder.build();
  }

  private GtfsFeedInfoTableContainer createFeedInfoTable(GtfsFeedInfo... feedInfos) {
    return GtfsFeedInfoTableContainer.forEntities(List.of(feedInfos), null);
  }

  @Test
  public void feedStartDateTodayShouldNotGenerateNotice() {
    // Feed start date is today - should be valid
    GtfsFeedInfoTableContainer table =
        createFeedInfoTable(createFeedInfo(1, GtfsDate.fromLocalDate(LocalDate.now())));
    assertThat(validateFeedInfo(table)).isEmpty();
  }

  @Test
  public void feedStartDateInPastShouldNotGenerateNotice() {
    // Feed start date is in the past - should be valid
    GtfsFeedInfoTableContainer table =
        createFeedInfoTable(
            createFeedInfo(1, GtfsDate.fromLocalDate(LocalDate.now().minusDays(30))));
    assertThat(validateFeedInfo(table)).isEmpty();
  }

  @Test
  public void feedStartDateInFutureShouldGenerateNotice() {
    // Feed start date is in the future - should trigger notice
    GtfsDate futureDate = GtfsDate.fromLocalDate(LocalDate.now().plusDays(7));
    GtfsFeedInfoTableContainer table = createFeedInfoTable(createFeedInfo(1, futureDate));
    assertThat(validateFeedInfo(table))
        .containsExactly(new FutureFeedNotice(futureDate, GtfsDate.fromLocalDate(LocalDate.now())));
  }

  @Test
  public void noFeedStartDateShouldNotGenerateNotice() {
    // No feed start date specified - should not trigger notice
    GtfsFeedInfoTableContainer table = createFeedInfoTable(createFeedInfo(1, null));
    assertThat(validateFeedInfo(table)).isEmpty();
  }
}
