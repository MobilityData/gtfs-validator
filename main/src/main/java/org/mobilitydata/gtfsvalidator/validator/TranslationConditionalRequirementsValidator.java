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

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.TranslationFieldValueDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationFieldValueNotDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordIdDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordIdNotDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordSubIdDefinedNotice;
import org.mobilitydata.gtfsvalidator.notice.TranslationRecordSubIdNotDefinedNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslation;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;
/**
 * Validates conditional requirements on `translations.record_id`, `translations.record_sub_id`, and
 * `translations.field_value` fields:
 *
 * <p>`translations.record_id` is: forbidden if `translations.table_name` is `feed_info`, forbidden
 * if `translations.field_value` is defined, required if `translations.field_value` is empty.
 *
 * <p>`translations.record_sub_id` is: forbidden if `translations.table_name` is `feed_info`,
 * forbidden if `translations.field_value` is defined, required if `translations.table_name` is
 * `stop_times` and `translations.record_id` is defined.
 *
 * <p>`translations.field_value` is: forbidden if `translations.table_name` is `feed_info`,
 * forbidden if `translations.record_id` is defined, required if `translations.record_id` is empty.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link TranslationRecordIdDefinedNotice} - `translations.record_id` is defined when it
 *       should not be
 *   <li>{@link TranslationRecordIdNotDefinedNotice} - `translations.record_id` is not defined when
 *       it should be
 *   <li>{@link TranslationRecordSubIdDefinedNotice} - `translations.record_sub_id` is defined when
 *       it should not be
 *   <li>{@link TranslationRecordSubIdNotDefinedNotice} - `translations.record_sub_id` is not
 *       defined when it should be
 *   <li>{@link TranslationFieldValueDefinedNotice} - `translations.field_value` is defined when it
 *       should be
 *   <li>{@link TranslationFieldValueNotDefinedNotice} - `translations.field_value` is defined when
 *       it should not be
 * </ul>
 */
@GtfsValidator
public class TranslationConditionalRequirementsValidator extends FileValidator {

  private static final String TXT_SUFFIX = ".txt";
  @Inject GtfsTranslationTableContainer translationTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTranslation translation : translationTable.getEntities()) {
      if (StringUtils.removeEnd(translation.tableName(), TXT_SUFFIX)
          .equals(StringUtils.removeEnd(GtfsStopTimeTableLoader.FILENAME, TXT_SUFFIX))) {
        if ((translation.hasRecordId() && translation.hasRecordSubId())) {
          noticeContainer.addValidationNotice(
              new TranslationRecordSubIdDefinedNotice(translation.csvRowNumber()));
        }
      } else if (StringUtils.removeEnd(translation.tableName(), TXT_SUFFIX)
          .equals(StringUtils.removeEnd(GtfsFeedInfoTableLoader.FILENAME, TXT_SUFFIX))) {
        if (translation.hasRecordId()) {
          noticeContainer.addValidationNotice(
              new TranslationRecordIdDefinedNotice(translation.csvRowNumber()));
        }
        if (translation.hasRecordSubId()) {
          noticeContainer.addValidationNotice(
              new TranslationRecordSubIdDefinedNotice(translation.csvRowNumber()));
        }
        if (translation.hasFieldValue()) {
          noticeContainer.addValidationNotice(
              new TranslationFieldValueDefinedNotice(translation.csvRowNumber()));
        }
      } else {
        if (translation.hasFieldValue() && translation.fieldValue().isEmpty()) {
          if (!translation.hasRecordId()) {
            noticeContainer.addValidationNotice(
                new TranslationRecordIdNotDefinedNotice(translation.csvRowNumber()));
          }
        } else if (translation.hasRecordId() && translation.recordId().isEmpty()) {
          if (!translation.hasFieldValue()) {
            noticeContainer.addValidationNotice(
                new TranslationFieldValueNotDefinedNotice(translation.csvRowNumber()));
          }
        } else if (translation.hasFieldValue()) {
          if (translation.hasRecordId()) {
            noticeContainer.addValidationNotice(
                new TranslationRecordIdDefinedNotice(translation.csvRowNumber()));
          }
          if (translation.hasRecordSubId()) {
            noticeContainer.addValidationNotice(
                new TranslationRecordSubIdDefinedNotice(translation.csvRowNumber()));
          }
        }
      }
    }
  }
}
