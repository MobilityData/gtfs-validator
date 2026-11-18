/*
 * Copyright 2021 Google LLC
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
import java.util.List;
import java.util.Locale;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.parsing.CsvHeader;
import org.mobilitydata.gtfsvalidator.table.GtfsAgency;
import org.mobilitydata.gtfsvalidator.table.GtfsAgencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslation;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableDescriptor;
import org.mobilitydata.gtfsvalidator.validator.TranslationFieldAndReferenceValidator.TranslationForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.validator.TranslationFieldAndReferenceValidator.TranslationUnexpectedValueNotice;
import org.mobilitydata.gtfsvalidator.validator.TranslationFieldAndReferenceValidator.TranslationUnknownFieldNameNotice;
import org.mobilitydata.gtfsvalidator.validator.TranslationFieldAndReferenceValidator.TranslationUnknownFieldValueNotice;
import org.mobilitydata.gtfsvalidator.validator.TranslationFieldAndReferenceValidator.TranslationUnknownTableNameNotice;

@RunWith(JUnit4.class)
public final class TranslationFieldAndReferenceValidatorTest {
  private static final GtfsAgency AGENCY =
      new GtfsAgency.Builder().setAgencyId("agency0").setAgencyName("Agency Zero").build();
  private static final GtfsStopTime STOP_TIME =
      new GtfsStopTime.Builder().setTripId("trip0").setStopSequence(0).build();
  private static final GtfsFeedInfo FEED_INFO =
      new GtfsFeedInfo.Builder().setFeedLang(Locale.CANADA).build();

  private static final String[] NEW_FORMAT_CSV_HEADERS =
      new String[] {
        "table_name",
        "field_name",
        "language",
        "translation",
        "record_id",
        "record_sub_id",
        "field_value",
      };

  private static List<ValidationNotice> generateNotices(
      CsvHeader translationHeader, List<GtfsTranslation> translations) {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsTranslationTableContainer translationTable =
        GtfsTranslationTableContainer.forHeaderAndEntities(
            new GtfsTranslationTableDescriptor(), translationHeader, translations, noticeContainer);
    new TranslationFieldAndReferenceValidator(
            translationTable,
            new GtfsFeedContainer(
                ImmutableList.of(
                    translationTable,
                    GtfsAgencyTableContainer.forEntities(ImmutableList.of(AGENCY), noticeContainer),
                    GtfsStopTimeTableContainer.forEntities(
                        ImmutableList.of(STOP_TIME), noticeContainer),
                    GtfsFeedInfoTableContainer.forEntities(
                        ImmutableList.of(FEED_INFO), noticeContainer))))
        .validate(noticeContainer);
    return noticeContainer.getValidationNotices();
  }

  @Test
  public void wrongTableName_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("wrong")
            .setFieldName("any")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownTableNameNotice(translation));
  }

  @Test
  public void fieldValueFound_yieldsNoNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setFieldValue("Agency Zero")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .isEmpty();
  }

  @Test
  public void recordIdAndFieldValueDefined_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setRecordId("id")
            .setFieldValue("Agency Zero")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnexpectedValueNotice(translation, "record_id", "id"));
  }

  @Test
  public void recordSubIdAndFieldValueDefined_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setRecordSubId("sub_id")
            .setFieldValue("Agency Zero")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(
            new TranslationUnexpectedValueNotice(translation, "record_sub_id", "sub_id"));
  }

  @Test
  public void noRecordIdForStopTable_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new MissingRequiredFieldNotice("translations.txt", 2, "record_id"));
  }

  @Test
  public void wrongRecordIdForStopTable_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setRecordId("any")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationForeignKeyViolationNotice(translation));
  }

  @Test
  public void feedInfoTranslation_yieldsNoNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("feed_info")
            .setFieldName("feed_publisher_name")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .isEmpty();
  }

  @Test
  public void unexpectedRecordIdForFeedInfo_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("feed_info")
            .setFieldName("feed_publisher_name")
            .setLanguage(Locale.forLanguageTag("en"))
            .setRecordId("feed-id")
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnexpectedValueNotice(translation, "record_id", "feed-id"));
  }

  @Test
  public void agencyTranslation_yieldsNoNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setRecordId("agency0")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .isEmpty();
  }

  @Test
  public void stopTimeTranslation_yieldsNoNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("stop_times")
            .setFieldName("stop_headsign")
            .setRecordId("trip0")
            .setRecordSubId("0")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .isEmpty();
  }

  @Test
  public void unparsableIntegerForStopTime_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("stop_times")
            .setFieldName("stop_headsign")
            .setRecordId("trip0")
            .setRecordSubId("not-an-int")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationForeignKeyViolationNotice(translation));
  }

  @Test
  public void unknownFieldNameWithRecordId_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_nme")
            .setRecordId("agency0")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldNameNotice(translation));
  }

  @Test
  public void unknownFieldNameWithUnknownRecordId_yieldsOnlyUnknownFieldNameNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_nme")
            .setRecordId("no-such-agency")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldNameNotice(translation));
  }

  @Test
  public void unknownFieldNameWithFieldValue_yieldsOnlyUnknownFieldNameNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_nme")
            .setFieldValue("Agency Zero")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldNameNotice(translation));
  }

  @Test
  public void nonEntityGetterAsFieldName_yieldsNotice() {
    // csv_row_number has a getter on every entity but is not a GTFS field.
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("csv_row_number")
            .setRecordId("agency0")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldNameNotice(translation));
  }

  @Test
  public void fieldValueNotFound_yieldsNotice() {
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setFieldValue("Agency One")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldValueNotice(translation));
  }

  @Test
  public void fieldValueInOtherField_yieldsNotice() {
    // "agency0" exists in agency_id, not in agency_name.
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setFieldValue("agency0")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .containsExactly(new TranslationUnknownFieldValueNotice(translation));
  }

  @Test
  public void fieldValueOnNonTextField_yieldsNoNotice() {
    // stop_sequence is an integer field: its values are not compared with field_value.
    GtfsTranslation translation =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("stop_times")
            .setFieldName("stop_sequence")
            .setFieldValue("99")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    assertThat(
            generateNotices(new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(translation)))
        .isEmpty();
  }

  @Test
  public void severalFieldValueTranslations_eachChecked() {
    GtfsTranslation found =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(2)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setFieldValue("Agency Zero")
            .setLanguage(Locale.forLanguageTag("en"))
            .build();
    GtfsTranslation notFound =
        new GtfsTranslation.Builder()
            .setCsvRowNumber(3)
            .setTableName("agency")
            .setFieldName("agency_name")
            .setFieldValue("Agency One")
            .setLanguage(Locale.forLanguageTag("fr"))
            .build();
    assertThat(
            generateNotices(
                new CsvHeader(NEW_FORMAT_CSV_HEADERS), ImmutableList.of(found, notFound)))
        .containsExactly(new TranslationUnknownFieldValueNotice(notFound));
  }
}
