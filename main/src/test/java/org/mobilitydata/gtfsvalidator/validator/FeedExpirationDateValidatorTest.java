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

import static com.google.common.truth.Truth.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.FeedExpirationDateValidator.FeedExpirationDateNotice;

public class FeedExpirationDateValidatorTest {
  private static final GtfsFeedName TEST_FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC);
  private static final CurrentDateTime TEST_CURRENT_DATE_TIME = CurrentDateTime.setNow(TEST_NOW);

  private List<ValidationNotice> validateFeedInfo(GtfsFeedInfo feedInfo) {
    NoticeContainer container = new NoticeContainer();
    new FeedExpirationDateValidator(TEST_CURRENT_DATE_TIME).validate(feedInfo, container);
    return container.getValidationNotices();
  }

  private GtfsFeedInfo createFeedInfo(GtfsDate feedEndDate) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(1)
        .setFeedPublisherName("feed publisher name value")
        .setFeedPublisherUrl("https://www.mobilitydata.org")
        .setFeedLang(Locale.CANADA)
        .setFeedEndDate(feedEndDate)
        .build();
  }

  @Test
  public void feedExpiringInFiveDaysFromNowShouldGenerateNotice() {
    assertThat(
            validateFeedInfo(
                createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(3)))))
        .containsExactly(
            new FeedExpirationDateNotice(
                1,
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(3)),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7))));
  }

  @Test
  public void feedExpiringInSevenDaysFromNowShouldGenerateNotice() {
    assertThat(
            validateFeedInfo(
                createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7)))))
        .containsExactly(
            new FeedExpirationDateNotice(
                1,
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(7)),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30))));
  }

  @Test
  public void feedExpiring7to30DaysFromNowShouldGenerateNotice() {
    assertThat(
            validateFeedInfo(
                createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(23)))))
        .containsExactly(
            new FeedExpirationDateNotice(
                1,
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(23)),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30))));
  }

  @Test
  public void feedExpiring30DaysFromNowShouldGenerateNotice() {
    assertThat(
            validateFeedInfo(
                createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30)))))
        .containsExactly(
            new FeedExpirationDateNotice(
                1,
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30)),
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(30))));
  }

  @Test
  public void feedExpiringInMoreThan30DaysFromNowShouldNotGenerateNotice() {
    assertThat(
            validateFeedInfo(
                createFeedInfo(GtfsDate.fromLocalDate(TEST_NOW.toLocalDate().plusDays(45)))))
        .isEmpty();
  }
}
