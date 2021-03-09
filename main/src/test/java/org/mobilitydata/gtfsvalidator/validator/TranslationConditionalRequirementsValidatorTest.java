/*
 * Copyright 2021 MobilityData IO
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
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.TranslationFieldValueDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationFieldValueNotDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordIdDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordIdNotDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordSubIdDefinedNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsRouteTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslation;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;

public class TranslationConditionalRequirementsValidatorTest {
  private static GtfsTranslationTableContainer createTranslationTable(
      NoticeContainer noticeContainer, List<GtfsTranslation> entities) {
    return GtfsTranslationTableContainer.forEntities(entities, noticeContainer);
  }

  private static GtfsTranslation createTranslation(
      long csvRowNumber, String tableName, String recordId, String recordSubId, String fieldValue) {
    return new GtfsTranslation.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setTableName(tableName)
        .setFieldName("field name value")
        .setLanguage(Locale.CANADA)
        .setTranslation("translation value")
        .setRecordId(recordId)
        .setRecordSubId(recordSubId)
        .setFieldValue(fieldValue)
        .build();
  }

  @Test
  public void recordIdIsForbiddenIfTableNameIsFeedInfo() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(
                    5, GtfsFeedInfoTableLoader.FILENAME, "record id value", null, null),
                createTranslation(15, GtfsFeedInfoTableLoader.FILENAME, null, null, null),
                createTranslation(6, GtfsFeedInfoTableLoader.FILENAME, "", null, null)));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new TranslationRecordIdDefinedNotice[] {
              new TranslationRecordIdDefinedNotice(5), new TranslationRecordIdDefinedNotice(6)
            });
  }

  @Test
  public void recordIdIsForbiddenIfFieldValueIsDefined() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(
                    5, GtfsRouteTableLoader.FILENAME, "record id", null, "field value"),
                createTranslation(15, GtfsRouteTableLoader.FILENAME, null, null, "field value"),
                createTranslation(6, GtfsRouteTableLoader.FILENAME, "", null, "field value")));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new TranslationRecordIdDefinedNotice[] {
              new TranslationRecordIdDefinedNotice(5), new TranslationRecordIdDefinedNotice(6)
            });
  }

  @Test
  public void recordIdIsRequiredIfFieldValueIsEmpty() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(6, GtfsRouteTableLoader.FILENAME, null, null, ""),
                createTranslation(
                    15, GtfsRouteTableLoader.FILENAME, "record id value", "record sub id", "")));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new TranslationRecordIdNotDefinedNotice(6));
  }

  @Test
  public void recordSubIdIsForbiddenIfTableNameIsFeedInfo() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(5, GtfsFeedInfoTableLoader.FILENAME, null, "record sub id", null),
                createTranslation(15, GtfsFeedInfoTableLoader.FILENAME, null, null, null),
                createTranslation(6, GtfsFeedInfoTableLoader.FILENAME, null, "", null)));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new TranslationRecordSubIdDefinedNotice[] {
              new TranslationRecordSubIdDefinedNotice(5), new TranslationRecordSubIdDefinedNotice(6)
            });
  }

  @Test
  public void recordSubIdIsForbiddenIfFieldValueIsDefined() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(15, GtfsRouteTableLoader.FILENAME, null, null, "field value"),
                createTranslation(
                    5, GtfsRouteTableLoader.FILENAME, null, "record sub id", "field value")));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new TranslationRecordSubIdDefinedNotice(5));
  }

  @Test
  public void recordSubIdIsForbiddenIfTableNameIsStopTimesAndRecordIdIsDefined() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(
                    5, GtfsStopTimeTableLoader.FILENAME, "record id value", "record sub id", null),
                createTranslation(
                    15, GtfsStopTimeTableLoader.FILENAME, "record id value", null, "field value"),
                createTranslation(
                    9, GtfsStopTimeTableLoader.FILENAME, "record id value", "", null)));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new TranslationRecordSubIdDefinedNotice[] {
              new TranslationRecordSubIdDefinedNotice(5), new TranslationRecordSubIdDefinedNotice(9)
            });
  }

  @Test
  public void fieldValueIsForbiddenIfTableNameIsFeedInfo() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(5, GtfsFeedInfoTableLoader.FILENAME, null, null, "field value"),
                createTranslation(15, GtfsFeedInfoTableLoader.FILENAME, null, null, null),
                createTranslation(6, GtfsFeedInfoTableLoader.FILENAME, null, null, "field value")));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactlyElementsIn(
            new TranslationFieldValueDefinedNotice[] {
              new TranslationFieldValueDefinedNotice(5), new TranslationFieldValueDefinedNotice(6)
            });
  }

  @Test
  public void fieldValueIsRequiredIfRecordIdIsEmpty() {
    NoticeContainer noticeContainer = new NoticeContainer();
    TranslationConditionalRequirementsValidator translationConditionalRequirementsValidator =
        new TranslationConditionalRequirementsValidator();
    translationConditionalRequirementsValidator.translationTable =
        createTranslationTable(
            noticeContainer,
            ImmutableList.of(
                createTranslation(15, GtfsRouteTableLoader.FILENAME, "", null, "field value"),
                createTranslation(
                    5, GtfsRouteTableLoader.FILENAME, "", "record sub id value", null)));

    translationConditionalRequirementsValidator.validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices())
        .containsExactly(new TranslationFieldValueNotDefinedNotice(5));
  }
}
