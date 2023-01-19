/*
 * Copyright 2023 Google LLC
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

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.validator.FeedHasLanguageValidator.FeedHasNoLanguageNotice;

@RunWith(JUnit4.class)
public final class FeedHasLanguageValidatorTest {
  private static List<ValidationNotice> generateNotices(
      List<GtfsAgency> agencies, List<GtfsFeedInfo> feedInfos) {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FeedHasLanguageValidator(GtfsAgencyTableContainer.forEntities(agencies, noticeContainer),
        GtfsFeedInfoTableContainer.forEntities(feedInfos, noticeContainer))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void feedLang_yieldsNoNotice() {
    assertThat(
        generateNotices(ImmutableList.of(),
            ImmutableList.of(new GtfsFeedInfo.Builder().setFeedLang(Locale.ENGLISH).build())))
        .isEmpty();
  }

  @Test
  public void agencyLang_yieldsNoNotice() {
    assertThat(generateNotices(ImmutableList.of(
                                   // No language for the first agency.
                                   new GtfsAgency.Builder().build(),
                                   new GtfsAgency.Builder().setAgencyLang(Locale.ENGLISH).build()),
                   ImmutableList.of()))
        .isEmpty();
  }

  @Test
  public void noAgencyLang_yieldsNotice() {
    assertThat(
        generateNotices(ImmutableList.of(new GtfsAgency.Builder().build()), ImmutableList.of()))
        .containsExactly(new FeedHasNoLanguageNotice());
  }

  @Test
  public void noEntities_yieldsNotice() {
    assertThat(generateNotices(ImmutableList.of(), ImmutableList.of()))
        .containsExactly(new FeedHasNoLanguageNotice());
  }
}
