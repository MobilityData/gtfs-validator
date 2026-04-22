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

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.FeedExpirationDateValidator.FeedExpirationDate30DaysNotice;
import org.mobilitydata.gtfsvalidator.validator.FeedExpirationDateValidator.FeedExpirationDate7DaysNotice;

public class FeedExpirationDateValidatorTest {
  // Use a date in later in the month to test the rollover from month to month also.
  private static final GtfsDate TEST_NOW = GtfsDate.fromLocalDate(LocalDate.of(2021, 1, 25));

  private List<ValidationNotice> validateFeedInfo(GtfsFeedInfo feedInfo) {
    NoticeContainer container = new NoticeContainer();
    new FeedExpirationDateValidator(new DateForValidation(TEST_NOW.getLocalDate()))
        .validate(feedInfo, container);
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
  public void feedExpiringInSixDaysFromNowShouldGenerate7DaysNotice() {
    List<ValidationNotice> notices = validateFeedInfo(createFeedInfo(TEST_NOW.plusDays(6)));
    assertThat(notices)
        .containsExactly(
            new FeedExpirationDate7DaysNotice(
                1, TEST_NOW, TEST_NOW.plusDays(6), TEST_NOW.plusDays(7)));
  }

  @Test
  public void feedExpiringInSevenDaysFromNowShouldGenerate30DaysNotice() {
    List<ValidationNotice> notices = validateFeedInfo(createFeedInfo(TEST_NOW.plusDays(7)));
    assertThat(notices)
        .containsExactly(
            new FeedExpirationDate30DaysNotice(
                1, TEST_NOW, TEST_NOW.plusDays(7), TEST_NOW.plusDays(30)));
  }

  @Test
  public void feedExpiring7to30DaysFromNowShouldGenerate30DaysNotice() {
    assertThat(validateFeedInfo(createFeedInfo(TEST_NOW.plusDays(29))))
        .containsExactly(
            new FeedExpirationDate30DaysNotice(
                1, TEST_NOW, TEST_NOW.plusDays(29), TEST_NOW.plusDays(30)));
  }

  @Test
  public void feedExpiring30DaysFromNowShouldNotGenerateNotice() {
    assertThat(validateFeedInfo(createFeedInfo(TEST_NOW.plusDays(30)))).isEmpty();
  }

  @Test
  public void feedExpiringInThePastShouldGenerate7DaysNotice() {
    List<ValidationNotice> notices = validateFeedInfo(createFeedInfo(TEST_NOW.plusDays(-1)));
    assertThat(notices)
        .containsExactly(
            new FeedExpirationDate7DaysNotice(
                1, TEST_NOW, TEST_NOW.plusDays(-1), TEST_NOW.plusDays(7)));
  }
}
