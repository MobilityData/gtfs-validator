/*
 * Copyright 2020 Google LLC
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
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyLangNotice;
import org.mobilitydata.gtfsvalidator.notice.InconsistentAgencyTimezoneNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;

public class AgencyConsistencyValidatorTest {

  private static GtfsAgencyTableContainer createAgencyTable(
      NoticeContainer noticeContainer, List<GtfsAgency> entities) {
    return GtfsAgencyTableContainer.forEntities(entities, noticeContainer);
  }

  public static GtfsAgency createAgency(
      long csvRowNumber,
      String agencyId,
      String agencyName,
      String agencyUrl,
      ZoneId agencyTimezone,
      @Nullable Locale agencyLang) {
    return new GtfsAgency.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setAgencyId(agencyId)
        .setAgencyName(agencyName)
        .setAgencyUrl(agencyUrl)
        .setAgencyTimezone(agencyTimezone)
        .setAgencyLang(agencyLang)
        .build();
  }

  @Test
  public void multipleAgenciesPresentButNoAgencyIdSetShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    0,
                    "first agency",
                    "agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    1,
                    null,
                    "agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new MissingRequiredFieldError("agency.txt", 1, "agency_id", SeverityLevel.ERROR));
  }

  @Test
  public void agenciesWithDifferentTimezoneShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    0,
                    "first agency",
                    "first agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Bogota"),
                    Locale.CANADA),
                createAgency(
                    1,
                    "second agency",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(
            new InconsistentAgencyTimezoneNotice(
                1, "America/Bogota", "America/Montreal", SeverityLevel.ERROR));
  }

  @Test
  public void agenciesWithSameTimezoneShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    0,
                    "first agency",
                    "first agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    1,
                    "second agency",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void agenciesWithDifferentLanguagesShouldGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    0,
                    "first agency",
                    "first agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    1,
                    "second agency",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.FRANCE)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new InconsistentAgencyLangNotice(1, "en", "fr", SeverityLevel.WARNING));
  }

  @Test
  public void agenciesWithSameLanguagesShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    0,
                    "first agency",
                    "first agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA),
                createAgency(
                    1,
                    "second agency",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void agenciesWithOmittedLanguageShouldNotGenerateNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    AgencyConsistencyValidator underTest = new AgencyConsistencyValidator();
    underTest.agencyTable =
        createAgencyTable(
            noticeContainer,
            ImmutableList.of(
                createAgency(
                    1,
                    "first agency",
                    "first agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    null),
                createAgency(
                    2,
                    "second agency",
                    "second agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA_FRENCH),
                createAgency(
                    3,
                    "third agency",
                    "third agency name",
                    "www.mobilitydata.org",
                    ZoneId.of("America/Montreal"),
                    Locale.CANADA_FRENCH)));

    underTest.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
