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
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static com.google.common.truth.Truth.assertThat;

/**
 * Runs GtfsCalendarTableContainer on test CSV data.
 */
@RunWith(JUnit4.class)
public class GtfsCalendarTableLoaderTest {
    private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

    @Test
    public void validFileShouldNotGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader =
                new StringReader(
                        "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date"
                                + System.lineSeparator() +
                                "service id value,1,0,0,0,0,0,0,20201122,20201210");
        GtfsCalendarTableLoader loader = new GtfsCalendarTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsCalendarTableContainer tableContainer =
                (GtfsCalendarTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isEmpty();
        assertThat(tableContainer.entityCount()).isEqualTo(1);
        GtfsCalendar calendar = tableContainer.byServiceId("service id value");
        assertThat(calendar).isNotNull();
        assertThat(calendar.monday()).isEqualTo(GtfsCalendarService.AVAILABLE);
        assertThat(calendar.tuesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.wednesday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.thursday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.friday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.saturday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.sunday()).isEqualTo(GtfsCalendarService.NOT_AVAILABLE);
        assertThat(calendar.startDate()).isEqualTo(GtfsDate.fromString("20201122"));
        assertThat(calendar.endDate()).isEqualTo(GtfsDate.fromString("20201210"));

        reader.close();
    }

    @Test
    public void missingRequiredFieldShouldGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader =
                new StringReader(
                        "service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date"
                                + System.lineSeparator() +
                                "service id value,,0,0,0,0,0,0,20201122,20201210");
        GtfsCalendarTableLoader loader = new GtfsCalendarTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsCalendarTableContainer tableContainer =
                (GtfsCalendarTableContainer) loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getCode()).matches("missing_required_field");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("filename", "calendar.txt");
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("csvRowNumber", 2L);
        assertThat(noticeContainer.getNotices().get(0).getContext()).containsEntry("fieldName", "monday");
        assertThat(tableContainer.entityCount()).isEqualTo(0);
        reader.close();
    }

    @Test
    public void emptyFileShouldGenerateNotice() throws IOException {
        ValidatorLoader validatorLoader = new ValidatorLoader();
        Reader reader = new StringReader("");
        GtfsCalendarTableLoader loader = new GtfsCalendarTableLoader();
        NoticeContainer noticeContainer = new NoticeContainer();
        loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

        assertThat(noticeContainer.getNotices()).isNotEmpty();
        assertThat(noticeContainer.getNotices().get(0).getClass().getSimpleName()).isEqualTo("EmptyFileNotice");
        reader.close();
    }
}
