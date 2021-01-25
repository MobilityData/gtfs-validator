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
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyFileNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsFareAttributeContainer on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsFareAttributeTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  @Test
  public void validFileShouldNotGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "fare_id,price,currency_type,payment_method,transfers"
                + System.lineSeparator()
                + "fare id value,2.5,CAD,0,0");
    GtfsFareAttributeTableLoader loader = new GtfsFareAttributeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFareAttributeTableContainer tableContainer =
        (GtfsFareAttributeTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsFareAttribute calendarDate = tableContainer.byFareId("fare id value");
    assertThat(calendarDate).isNotNull();
    assertThat(calendarDate.fareId()).matches("fare id value");
    assertThat(calendarDate.price()).isEqualToIgnoringScale(new BigDecimal("2.5"));
    assertThat(calendarDate.currencyType()).isEqualTo(Currency.getInstance("CAD"));
    assertThat(calendarDate.paymentMethod()).isEqualTo(GtfsFareAttributePaymentMethod.ON_BOARD);
    assertThat(calendarDate.transfers()).isEqualTo(GtfsFareAttributeTransfers.NO_TRANSFER);

    reader.close();
  }

  @Test
  public void missingRequiredFieldShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader =
        new StringReader(
            "fare_id,price,currency_type,payment_method,transfers"
                + System.lineSeparator()
                + "fare id value,2.5,,0,0");
    GtfsFareAttributeTableLoader loader = new GtfsFareAttributeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFareAttributeTableContainer tableContainer =
        (GtfsFareAttributeTableContainer)
            loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
    assertThat(noticeContainer.getValidationNotices().get(0).getCode())
        .matches("missing_required_field");
    assertThat(noticeContainer.getValidationNotices().get(0).getContext())
        .containsEntry("filename", "fare_attributes.txt");
    assertThat(noticeContainer.getValidationNotices().get(0).getContext())
        .containsEntry("csvRowNumber", 2L);
    assertThat(noticeContainer.getValidationNotices().get(0).getContext())
        .containsEntry("fieldName", "currency_type");
    assertThat(tableContainer.entityCount()).isEqualTo(0);
    reader.close();
  }

  @Test
  public void emptyFileShouldGenerateNotice() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    Reader reader = new StringReader("");
    GtfsFareAttributeTableLoader loader = new GtfsFareAttributeTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();

    loader.load(reader, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new EmptyFileNotice("fare_attributes.txt"));
    reader.close();
  }
}
