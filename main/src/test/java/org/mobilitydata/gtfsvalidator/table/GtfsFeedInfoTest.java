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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

import java.util.Locale;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_PUBLISHER_NAME;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_PUBLISHER_URL;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_LANG;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_DEFAULT_LANG;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_START_DATE;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_END_DATE;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_VERSION;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_CONTACT_EMAIL;
import static org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo.DEFAULT_FEED_CONTACT_URL;

@RunWith(JUnit4.class)
public class GtfsFeedInfoTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsFeedInfo.Builder builder = new GtfsFeedInfo.Builder();
        GtfsDate startDate = GtfsDate.fromEpochDay(356);
        GtfsDate endDate = GtfsDate.fromEpochDay(450);
        GtfsFeedInfo underTest = builder
                .setFeedPublisherName("feed publisher name")
                .setFeedPublisherUrl("https://www.github.com/MobilityData")
                .setFeedLang(Locale.forLanguageTag("fr-CA"))
                .setDefaultLang(Locale.forLanguageTag("fr-CA"))
                .setFeedStartDate(startDate)
                .setFeedEndDate(endDate)
                .setFeedVersion("feed version")
                .setFeedContactEmail("hello@mobilitydata.org")
                .setFeedContactUrl("https://www.github.com/MobilityData")
                .build();

        assertThat(underTest.feedPublisherName()).isEqualTo("feed publisher name");
        assertThat(underTest.feedPublisherUrl()).isEqualTo("https://www.github.com/MobilityData");
        assertThat(underTest.feedLang()).isEqualTo(Locale.forLanguageTag("fr-CA"));
        assertThat(underTest.defaultLang()).isEqualTo(Locale.forLanguageTag("fr-CA"));
        assertThat(underTest.feedStartDate()).isEqualTo(startDate);
        assertThat(underTest.feedEndDate()).isEqualTo(endDate);
        assertThat(underTest.feedVersion()).isEqualTo("feed version");
        assertThat(underTest.feedContactEmail()).isEqualTo("hello@mobilitydata.org");
        assertThat(underTest.feedContactUrl()).isEqualTo("https://www.github.com/MobilityData");

        assertThat(underTest.hasFeedPublisherName()).isTrue();
        assertThat(underTest.hasFeedPublisherUrl()).isTrue();
        assertThat(underTest.hasFeedLang()).isTrue();
        assertThat(underTest.hasDefaultLang()).isTrue();
        assertThat(underTest.hasFeedStartDate()).isTrue();
        assertThat(underTest.hasFeedEndDate()).isTrue();
        assertThat(underTest.hasFeedVersion()).isTrue();
        assertThat(underTest.hasFeedContactEmail()).isTrue();
        assertThat(underTest.hasFeedContactUrl()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsFeedInfo.Builder builder = new GtfsFeedInfo.Builder();
        GtfsFeedInfo underTest = builder
                .setFeedPublisherName(null)
                .setFeedPublisherUrl(null)
                .setFeedLang(null)
                .setDefaultLang(null)
                .setFeedStartDate(null)
                .setFeedEndDate(null)
                .setFeedVersion(null)
                .setFeedContactEmail(null)
                .setFeedContactUrl(null)
                .build();

        assertThat(underTest.feedPublisherName()).isEqualTo(DEFAULT_FEED_PUBLISHER_NAME);
        assertThat(underTest.feedPublisherUrl()).isEqualTo(DEFAULT_FEED_PUBLISHER_URL);
        assertThat(underTest.feedLang()).isEqualTo(DEFAULT_FEED_LANG);
        assertThat(underTest.defaultLang()).isEqualTo(DEFAULT_DEFAULT_LANG);
        assertThat(underTest.feedStartDate()).isEqualTo(DEFAULT_FEED_START_DATE);
        assertThat(underTest.feedEndDate()).isEqualTo(DEFAULT_FEED_END_DATE);
        assertThat(underTest.feedVersion()).isEqualTo(DEFAULT_FEED_VERSION);
        assertThat(underTest.feedContactEmail()).isEqualTo(DEFAULT_FEED_CONTACT_EMAIL);
        assertThat(underTest.feedContactUrl()).isEqualTo(DEFAULT_FEED_CONTACT_URL);

        assertThat(underTest.hasFeedPublisherName()).isFalse();
        assertThat(underTest.hasFeedPublisherUrl()).isFalse();
        assertThat(underTest.hasFeedLang()).isFalse();
        assertThat(underTest.hasDefaultLang()).isFalse();
        assertThat(underTest.hasFeedStartDate()).isFalse();
        assertThat(underTest.hasFeedEndDate()).isFalse();
        assertThat(underTest.hasFeedVersion()).isFalse();
        assertThat(underTest.hasFeedContactEmail()).isFalse();
        assertThat(underTest.hasFeedContactUrl()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsFeedInfo.Builder builder = new GtfsFeedInfo.Builder();
        GtfsDate startDate = GtfsDate.fromEpochDay(356);
        GtfsDate endDate = GtfsDate.fromEpochDay(450);
        builder.setFeedPublisherName("feed publisher name")
                .setFeedPublisherUrl("https://www.github.com/MobilityData")
                .setFeedLang(Locale.forLanguageTag("fr-CA"))
                .setDefaultLang(Locale.forLanguageTag("fr-CA"))
                .setFeedStartDate(startDate)
                .setFeedEndDate(endDate)
                .setFeedVersion("feed version")
                .setFeedContactEmail("hello@mobilitydata.org")
                .setFeedContactUrl("https://www.github.com/MobilityData");

        builder.clear();
        GtfsFeedInfo underTest = builder.build();

        assertThat(underTest.feedPublisherName()).isEqualTo(DEFAULT_FEED_PUBLISHER_NAME);
        assertThat(underTest.feedPublisherUrl()).isEqualTo(DEFAULT_FEED_PUBLISHER_URL);
        assertThat(underTest.feedLang()).isEqualTo(DEFAULT_FEED_LANG);
        assertThat(underTest.defaultLang()).isEqualTo(DEFAULT_DEFAULT_LANG);
        assertThat(underTest.feedStartDate()).isEqualTo(DEFAULT_FEED_START_DATE);
        assertThat(underTest.feedEndDate()).isEqualTo(DEFAULT_FEED_END_DATE);
        assertThat(underTest.feedVersion()).isEqualTo(DEFAULT_FEED_VERSION);
        assertThat(underTest.feedContactEmail()).isEqualTo(DEFAULT_FEED_CONTACT_EMAIL);
        assertThat(underTest.feedContactUrl()).isEqualTo(DEFAULT_FEED_CONTACT_URL);

        assertThat(underTest.hasFeedPublisherName()).isFalse();
        assertThat(underTest.hasFeedPublisherUrl()).isFalse();
        assertThat(underTest.hasFeedLang()).isFalse();
        assertThat(underTest.hasDefaultLang()).isFalse();
        assertThat(underTest.hasFeedStartDate()).isFalse();
        assertThat(underTest.hasFeedEndDate()).isFalse();
        assertThat(underTest.hasFeedVersion()).isFalse();
        assertThat(underTest.hasFeedContactEmail()).isFalse();
        assertThat(underTest.hasFeedContactUrl()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsFeedInfo.Builder builder = new GtfsFeedInfo.Builder();
        GtfsFeedInfo underTest = builder.build();

        assertThat(underTest.feedPublisherName()).isNull();
        assertThat(underTest.feedPublisherUrl()).isNull();
        assertThat(underTest.feedLang()).isNull();
        assertThat(underTest.defaultLang()).isNull();
        assertThat(underTest.feedStartDate()).isNull();
        assertThat(underTest.feedEndDate()).isNull();
        assertThat(underTest.feedVersion()).isNull();
        assertThat(underTest.feedContactEmail()).isNull();
        assertThat(underTest.feedContactUrl()).isNull();

        assertThat(underTest.hasFeedPublisherName()).isFalse();
        assertThat(underTest.hasFeedPublisherUrl()).isFalse();
        assertThat(underTest.hasFeedLang()).isFalse();
        assertThat(underTest.hasDefaultLang()).isFalse();
        assertThat(underTest.hasFeedStartDate()).isFalse();
        assertThat(underTest.hasFeedEndDate()).isFalse();
        assertThat(underTest.hasFeedVersion()).isFalse();
        assertThat(underTest.hasFeedContactEmail()).isFalse();
        assertThat(underTest.hasFeedContactUrl()).isFalse();
    }
}
