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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsCalendarTableContainer on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsCalendarDateTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  @Test
  public void validFileShouldNotGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader("service_id,date,exception_type\n" + "service id value,20201122,2");
    GtfsCalendarDateTableLoader loader = new GtfsCalendarDateTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsCalendarDateTableContainer tableContainer =
        (GtfsCalendarDateTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsCalendarDate calendarDate = tableContainer.byServiceId("service id value").get(0);
    assertThat(calendarDate).isNotNull();
    assertThat(calendarDate.serviceId()).matches("service id value");
    assertThat(calendarDate.date()).isEqualTo(GtfsDate.fromString("20201122"));
    assertThat(calendarDate.exceptionType())
        .isEqualTo(GtfsCalendarDateExceptionType.SERVICE_REMOVED);
  }

  @Test
  public void missingRequiredFieldShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("service_id,date,exception_type\n" + "service id value,,2");
    GtfsCalendarDateTableLoader loader = new GtfsCalendarDateTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsCalendarDateTableContainer tableContainer =
        (GtfsCalendarDateTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldError("calendar_dates.txt", 2, "date"));
    assertThat(tableContainer.entityCount()).isEqualTo(0);
  }

  @Test
  public void emptyFileShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("");
    GtfsCalendarDateTableLoader loader = new GtfsCalendarDateTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();

    loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new EmptyFileNotice("calendar_dates.txt"));
  }
}
