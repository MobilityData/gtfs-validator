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
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Locale;
import java.util.TimeZone;

import static com.google.common.truth.Truth.assertThat;

/**
 * Runs GtfsAgencyTableContainer on test CSV data.
 */
@RunWith(JUnit4.class)
public class GtfsAgencyTableLoaderTest {
    private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

    @Test
    public void validFile() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        // agencyId is not null
        Reader reader =
                new StringReader(
                        "agency_id,agency_name,agency_url,agency_timezone,agency_lang," +
                                "agency_phone,agency_fare_url,agency_email" + System.lineSeparator() +
                        "agency id value,agency name value,https://www.mobilitydata.org,America/Montreal,fr,514-234-7894," +
                                "https://www.mobilitydata.org,hello@mobilitydata.org");
        GtfsAgencyTableLoader loader = new GtfsAgencyTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsAgencyTableContainer tableContainer =
                (GtfsAgencyTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(1);
        GtfsAgency agency = tableContainer.byAgencyId("agency id value");
        assertThat(agency).isNotNull();
        assertThat(agency.agencyId()).isEqualTo("agency id value");
        assertThat(agency.agencyName()).isEqualTo("agency name value");
        assertThat(agency.agencyUrl()).isEqualTo("https://www.mobilitydata.org");
        assertThat(agency.agencyTimezone()).isEqualTo(TimeZone.getTimeZone("America/Montreal"));
        assertThat(agency.agencyLang()).isEqualTo(Locale.forLanguageTag("fr"));
        assertThat(agency.agencyPhone()).matches("514-234-7894");
        assertThat(agency.agencyFareUrl()).matches("https://www.mobilitydata.org");
        assertThat(agency.agencyEmail()).matches("hello@mobilitydata.org");

        reader.close();

        // agencyId is null
        reader =
                new StringReader(
                        "agency_id,agency_name,agency_url,agency_timezone" + System.lineSeparator() +
                        ",agency name value,https://www.mobilitydata.org,America/Montreal");
        loader = new GtfsAgencyTableLoader();
        tableContainer =
                (GtfsAgencyTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(1);
        agency = tableContainer.byAgencyId("");
        assertThat(agency).isNotNull();
        assertThat(agency.agencyId()).isEmpty();
        assertThat(agency.agencyName()).isEqualTo("agency name value");
        assertThat(agency.agencyUrl()).isEqualTo("https://www.mobilitydata.org");
        assertThat(agency.agencyTimezone()).isEqualTo(TimeZone.getTimeZone("America/Montreal"));

        reader.close();
    }

    @Test
    public void missingRequiredField() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        // agencyId is not null
        Reader reader =
                new StringReader(
                        "agency_id,agency_name,agency_url,agency_timezone" + System.lineSeparator() +
                                "agency id value,,https://www.mobilitydata.org,America/Montreal");
        GtfsAgencyTableLoader loader = new GtfsAgencyTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsAgencyTableContainer tableContainer =
                (GtfsAgencyTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getCode()).matches("missing_required_field");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("filename", "agency.txt");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("fieldName", "agency_name");
        assertThat(tableContainer.entityCount()).isEqualTo(0);
        reader.close();
    }

    @Test
    public void emptyFile() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("");
        GtfsAgencyTableLoader loader = new GtfsAgencyTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();

        loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getClass().getSimpleName()).isEqualTo("EmptyFileNotice");
        reader.close();
    }
}
