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

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.FeedInfoLangAndAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer.TableStatus;

@RunWith(JUnit4.class)
public class MatchingFeedAndAgencyLangValidatorTest {

  private static GtfsAgencyTableContainer createAgencyTable(
      NoticeContainer noticeContainer, List<GtfsAgency> entities) {
    return GtfsAgencyTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsAgency createAgency(
      long csvRowNumber, String agencyId, @Nullable Locale agencyLang) {
    return new GtfsAgency.Builder()
        .setAgencyId(agencyId)
        .setCsvRowNumber(csvRowNumber)
        .setAgencyLang(agencyLang)
        .setAgencyName(agencyId + " name")
        .build();
  }

  private static GtfsFeedInfoTableContainer createFeedInfoTable(
      NoticeContainer noticeContainer, List<GtfsFeedInfo> entities) {
    return GtfsFeedInfoTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsFeedInfo createFeedInfo(long csvRowNumber, @Nullable Locale feedLang) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setFeedPublisherName("feed publisher name")
        .setFeedPublisherUrl("www.mobilitydata.org")
        .setFeedLang(feedLang)
        .build();
  }

  @Test
  public void noFeedInfoShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator validator = new MatchingFeedAndAgencyLangValidator();
    validator.agencyTable = new GtfsAgencyTableContainer(TableStatus.EMPTY_FILE);
    validator.feedInfoTable = new GtfsFeedInfoTableContainer(TableStatus.EMPTY_FILE);
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void noFeedLangShouldNotGenerateNotice() {
    // If feed_lang is not set, then it is not compared to agency_lang.
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator validator = new MatchingFeedAndAgencyLangValidator();
    validator.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agencyEn", Locale.ENGLISH),
                createAgency(3, "agencyFr", Locale.FRANCE)));
    validator.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(1, null)));
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void languageMismatch() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator validator = new MatchingFeedAndAgencyLangValidator();
    validator.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agencyCa", Locale.CANADA_FRENCH),
                createAgency(3, "agencyFr", Locale.FRANCE),
                createAgency(4, "agencyEmpty", null)));
    validator.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.FRANCE)));
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                2, "agencyCa", "agencyCa name", "fr-CA", "fr-FR"));
  }

  @Test
  public void multilanguageFeedDifferentAgencies() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator validator = new MatchingFeedAndAgencyLangValidator();
    validator.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agencyEn", Locale.ENGLISH),
                createAgency(3, "agencyFr", Locale.FRANCE)));
    validator.feedInfoTable =
        createFeedInfoTable(
            noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.forLanguageTag("mul"))));
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void multilanguageFeedSingleAgency() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator validator = new MatchingFeedAndAgencyLangValidator();
    validator.agencyTable =
        createAgencyTable(
            noticeContainer, ImmutableList.of(createAgency(2, "agencyEn", Locale.ENGLISH)));
    validator.feedInfoTable =
        createFeedInfoTable(
            noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.forLanguageTag("mul"))));
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
