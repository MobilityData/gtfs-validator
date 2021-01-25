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
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsFrequencyTableLoader on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsFrequencyTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  @Test
  public void validFileShouldNotGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "trip_id,start_time,end_time,headway_secs"
                + System.lineSeparator()
                + "trip id value,14:30:45,16:30:45,2");
    GtfsFrequencyTableLoader loader = new GtfsFrequencyTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFrequencyTableContainer tableContainer =
        (GtfsFrequencyTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsFrequency frequency = tableContainer.getEntities().get(0);
    assertThat(frequency).isNotNull();
    assertThat(frequency.tripId()).matches("trip id value");
    assertThat(frequency.startTime()).isEqualTo(GtfsTime.fromString("14:30:45"));
    assertThat(frequency.endTime()).isEqualTo(GtfsTime.fromString("16:30:45"));
    assertThat(frequency.headwaySecs()).isEqualTo(2);

    reader.close();
  }

  @Test
  public void missingRequiredFieldShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "trip_id,start_time,end_time,headway_secs"
                + System.lineSeparator()
                + ",14:30:45,16:30:45,2");
    GtfsFrequencyTableLoader loader = new GtfsFrequencyTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFrequencyTableContainer tableContainer =
        (GtfsFrequencyTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldError("frequencies.txt", 2, "trip_id"));
    assertThat(tableContainer.entityCount()).isEqualTo(0);
    reader.close();
  }

  @Test
  public void emptyFileShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("");
    GtfsFrequencyTableLoader loader = new GtfsFrequencyTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();

    loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new EmptyFileNotice("frequencies.txt"));
    reader.close();
  }
}
