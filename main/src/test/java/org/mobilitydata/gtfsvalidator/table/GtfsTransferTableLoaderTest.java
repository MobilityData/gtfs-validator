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
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsTransferTableLoaderTest on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsTransferTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  @Test
  public void validFileShouldNotGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "from_stop_id,to_stop_id,transfer_type\n"
                + "origin stop id value,arrival stop id value,2");
    GtfsTransferTableLoader loader = new GtfsTransferTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTransferTableContainer tableContainer =
        (GtfsTransferTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsTransfer transfer = tableContainer.getEntities().get(0);
    assertThat(transfer).isNotNull();
    assertThat(transfer.fromStopId()).matches("origin stop id value");
    assertThat(transfer.toStopId()).matches("arrival stop id value");
    assertThat(transfer.transferType()).isEqualTo(GtfsTransferType.MINIMUM_TIME);
  }

  @Test
  public void missingRequiredFieldShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "from_stop_id,to_stop_id,transfer_type\n"
                + "origin stop id value,,2");
    GtfsTransferTableLoader loader = new GtfsTransferTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTransferTableContainer tableContainer =
        (GtfsTransferTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new MissingRequiredFieldError("transfers.txt", 2, "to_stop_id"));
    assertThat(tableContainer.entityCount()).isEqualTo(0);
  }

  @Test
  public void emptyFileShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("");
    GtfsTransferTableLoader loader = new GtfsTransferTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();

    loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);
    reader.close();

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new EmptyFileNotice("transfers.txt"));
  }
}
