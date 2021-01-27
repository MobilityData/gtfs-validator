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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.FeedInfoLangAndAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;

@RunWith(JUnit4.class)
public class MatchingFeedAndAgencyLangValidatorTest {
  private static GtfsAgencyTableContainer createAgencyTable(
      NoticeContainer noticeContainer, List<GtfsAgency> entities) {
    return GtfsAgencyTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsAgency createAgency(long csvRowNumber, String agencyId, Locale agencyLang) {
    return new GtfsAgency.Builder()
        .setAgencyId(agencyId)
        .setCsvRowNumber(csvRowNumber)
        .setAgencyLang(agencyLang)
        .build();
  }

  private static GtfsFeedInfoTableContainer createFeedInfoTable(
      NoticeContainer noticeContainer, List<GtfsFeedInfo> entities) {
    return GtfsFeedInfoTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsFeedInfo createFeedInfo(long csvRowNumber, Locale feedLang) {
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
    validator.agencyTable = GtfsAgencyTableContainer.forEmptyFile();
    validator.feedInfoTable = GtfsFeedInfoTableContainer.forEmptyFile();
    validator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void mulFeedLangAndNoMoreThanOneAgencyLangShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer, ImmutableList.of(createAgency(2, "agency id value", Locale.CANADA)));
    underTest.feedInfoTable =
        createFeedInfoTable(
            noticeContainer, ImmutableList.of(createFeedInfo(2, Locale.forLanguageTag("mul"))));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                "mul", new HashSet<>(Collections.singletonList("eng"))));
  }

  @Test
  public void feedLangNotMulAndMoreThanOneAgencyLangShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "1st agency id", Locale.CANADA),
                createAgency(3, "2nd agency agency", Locale.FRANCE)));
    underTest.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.FRANCE)));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                "fra", new HashSet<>(Arrays.asList("fra", "eng"))));
  }

  @Test
  public void feedLangNotMulAndOnlyOneMatchingAgencyLangShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer, ImmutableList.of(createAgency(3, "2nd agency agency", Locale.FRANCE)));
    underTest.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.FRANCE)));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void feedLangNotMulAndOnlyOneMismatchingAgencyLangShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer, ImmutableList.of(createAgency(2, "1st agency id", Locale.US)));
    underTest.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(1, Locale.FRANCE)));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                "fra", new HashSet<>(Collections.singletonList("eng"))));
  }

  @Test
  public void matchingFeedInfoFeedLangShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agency id value", Locale.CANADA),
                createAgency(3, "other agency id value", Locale.CANADA)));
    underTest.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(2, Locale.CANADA)));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void feedLangNotMulAndMultipleNonMatchingAgencyLangShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agency id value", Locale.ITALIAN),
                createAgency(3, "other agency id value", Locale.FRANCE)));
    underTest.feedInfoTable =
        createFeedInfoTable(noticeContainer, ImmutableList.of(createFeedInfo(2, Locale.CANADA)));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new FeedInfoLangAndAgencyLangMismatchNotice(
                "eng", new HashSet<>(Arrays.asList("ita", "fra"))));
  }

  @Test
  public void mulFeedLandAndMoreThanOneAgencyShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    MatchingFeedAndAgencyLangValidator underTest = new MatchingFeedAndAgencyLangValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(2, "agency id value", Locale.ITALIAN),
                createAgency(3, "other agency id value", Locale.FRANCE)));
    underTest.feedInfoTable =
        createFeedInfoTable(
            noticeContainer, ImmutableList.of(createFeedInfo(2, Locale.forLanguageTag("mul"))));
    underTest.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
