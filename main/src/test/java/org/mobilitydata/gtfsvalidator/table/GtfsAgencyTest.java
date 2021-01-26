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

package org.mobilitydata.gtfsvalidator.table;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_EMAIL;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_FARE_URL;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_LANG;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_PHONE;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_TIMEZONE;
import static org.mobilitydata.gtfsvalidator.table.GtfsAgency.DEFAULT_AGENCY_URL;

import java.time.ZoneId;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GtfsAgencyTest {
  @Test
  public void shouldReturnFieldValues() {
    GtfsAgency.Builder builder = new GtfsAgency.Builder();
    GtfsAgency underTest =
        builder
            .setAgencyId("agency id value")
            .setAgencyName("agency name")
            .setAgencyUrl("https://www.github.com/MobilityData")
            .setAgencyTimezone(ZoneId.of("America/Montreal"))
            .setAgencyLang(Locale.forLanguageTag("fr-CA"))
            .setAgencyPhone("123-456-7890")
            .setAgencyFareUrl("https://www.github.com/MobilityData")
            .setAgencyEmail("hello@mobilitydata.org")
            .build();

    assertThat(underTest.agencyId()).matches("agency id value");
    assertThat(underTest.agencyName()).matches("agency name");
    assertThat(underTest.agencyUrl()).matches("https://www.github.com/MobilityData");
    assertThat(underTest.agencyTimezone()).isEqualTo(ZoneId.of("America/Montreal"));
    assertThat(underTest.agencyLang()).isEqualTo(Locale.forLanguageTag("fr-CA"));
    assertThat(underTest.agencyPhone()).matches("123-456-7890");
    assertThat(underTest.agencyFareUrl()).matches("https://www.github.com/MobilityData");
    assertThat(underTest.agencyEmail()).matches("hello@mobilitydata.org");

    assertThat(underTest.hasAgencyId()).isTrue();
    assertThat(underTest.hasAgencyName()).isTrue();
    assertThat(underTest.hasAgencyUrl()).isTrue();
    assertThat(underTest.hasAgencyTimezone()).isTrue();
    assertThat(underTest.hasAgencyLang()).isTrue();
    assertThat(underTest.hasAgencyPhone()).isTrue();
    assertThat(underTest.hasAgencyFareUrl()).isTrue();
    assertThat(underTest.hasAgencyEmail()).isTrue();
  }

  @Test
  public void shouldReturnDefaultValuesForMissingValues() {
    GtfsAgency.Builder builder = new GtfsAgency.Builder();
    GtfsAgency underTest =
        builder
            .setAgencyId(null)
            .setAgencyName(null)
            .setAgencyUrl(null)
            .setAgencyTimezone(null)
            .setAgencyLang(null)
            .setAgencyPhone(null)
            .setAgencyFareUrl(null)
            .setAgencyEmail(null)
            .build();

    assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);
    assertThat(underTest.agencyName()).matches(DEFAULT_AGENCY_NAME);
    assertThat(underTest.agencyUrl()).matches(DEFAULT_AGENCY_URL);
    assertThat(underTest.agencyTimezone()).isEqualTo(DEFAULT_AGENCY_TIMEZONE);
    assertThat(underTest.agencyLang()).isEqualTo(DEFAULT_AGENCY_LANG);
    assertThat(underTest.agencyPhone()).matches(DEFAULT_AGENCY_PHONE);
    assertThat(underTest.agencyFareUrl()).matches(DEFAULT_AGENCY_FARE_URL);
    assertThat(underTest.agencyEmail()).matches(DEFAULT_AGENCY_EMAIL);

    assertThat(underTest.hasAgencyId()).isFalse();
    assertThat(underTest.hasAgencyName()).isFalse();
    assertThat(underTest.hasAgencyUrl()).isFalse();
    assertThat(underTest.hasAgencyTimezone()).isFalse();
    assertThat(underTest.hasAgencyLang()).isFalse();
    assertThat(underTest.hasAgencyPhone()).isFalse();
    assertThat(underTest.hasAgencyFareUrl()).isFalse();
    assertThat(underTest.hasAgencyEmail()).isFalse();
  }

  @Test
  public void shouldResetFieldToDefaultValues() {
    GtfsAgency.Builder builder = new GtfsAgency.Builder();
    builder
        .setAgencyId("agency id value")
        .setAgencyName("agency name")
        .setAgencyUrl("https://www.github.com/MobilityData")
        .setAgencyTimezone(ZoneId.of("America/Montreal"))
        .setAgencyLang(Locale.forLanguageTag("fr-CA"))
        .setAgencyPhone("123-456-7890")
        .setAgencyFareUrl("https://www.github.com/MobilityData")
        .setAgencyEmail("hello@mobilitydata.org");

    builder.clear();
    GtfsAgency underTest = builder.build();

    assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);
    assertThat(underTest.agencyName()).matches(DEFAULT_AGENCY_NAME);
    assertThat(underTest.agencyUrl()).matches(DEFAULT_AGENCY_URL);
    assertThat(underTest.agencyTimezone()).isEqualTo(DEFAULT_AGENCY_TIMEZONE);
    assertThat(underTest.agencyLang()).isEqualTo(DEFAULT_AGENCY_LANG);
    assertThat(underTest.agencyPhone()).matches(DEFAULT_AGENCY_PHONE);
    assertThat(underTest.agencyFareUrl()).matches(DEFAULT_AGENCY_FARE_URL);
    assertThat(underTest.agencyEmail()).matches(DEFAULT_AGENCY_EMAIL);

    assertThat(underTest.hasAgencyId()).isFalse();
    assertThat(underTest.hasAgencyName()).isFalse();
    assertThat(underTest.hasAgencyUrl()).isFalse();
    assertThat(underTest.hasAgencyTimezone()).isFalse();
    assertThat(underTest.hasAgencyLang()).isFalse();
    assertThat(underTest.hasAgencyPhone()).isFalse();
    assertThat(underTest.hasAgencyFareUrl()).isFalse();
    assertThat(underTest.hasAgencyEmail()).isFalse();
  }

  @Test
  public void fieldValuesNotSetShouldBeNull() {
    GtfsAgency.Builder builder = new GtfsAgency.Builder();
    GtfsAgency underTest = builder.build();

    assertThat(underTest.agencyId()).isNull();
    assertThat(underTest.agencyName()).isNull();
    assertThat(underTest.agencyUrl()).isNull();
    assertThat(underTest.agencyTimezone()).isNull();
    assertThat(underTest.agencyLang()).isNull();
    assertThat(underTest.agencyPhone()).isNull();
    assertThat(underTest.agencyFareUrl()).isNull();
    assertThat(underTest.agencyEmail()).isNull();

    assertThat(underTest.hasAgencyId()).isFalse();
    assertThat(underTest.hasAgencyName()).isFalse();
    assertThat(underTest.hasAgencyUrl()).isFalse();
    assertThat(underTest.hasAgencyTimezone()).isFalse();
    assertThat(underTest.hasAgencyLang()).isFalse();
    assertThat(underTest.hasAgencyPhone()).isFalse();
    assertThat(underTest.hasAgencyFareUrl()).isFalse();
    assertThat(underTest.hasAgencyEmail()).isFalse();
  }
}
