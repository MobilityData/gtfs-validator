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

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslation;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTranslationTableLoader;

/**
 * Validates that translations are provided in accordance with GTFS Specification.
 *
 * <p>Checks that fields and values provided in translations are correct and checks that a
 * translation references existing entity.
 *
 * <p>This validator gracefully handles legacy Google translations format. If {@code
 * translations.txt} does not follow the new format, then no validations are performed.
 */
@GtfsValidator
public class TranslationFieldAndReferenceValidator extends FileValidator {
  private final GtfsTranslationTableContainer translationTable;
  private final GtfsFeedContainer feedContainer;

  @Inject
  TranslationFieldAndReferenceValidator(
      GtfsTranslationTableContainer translationTable, GtfsFeedContainer feedContainer) {
    this.translationTable = translationTable;
    this.feedContainer = feedContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // The legacy Google translation format does not define `translations.table_name` field.
    if (!translationTable.getHeader().hasColumn(GtfsTranslationTableLoader.TABLE_NAME_FIELD_NAME)) {
      // Skip validation if legacy Google translation format is detected.
      return;
    }
    // If GtfsTranslationSchema.java is patched to enable the legacy Google format, then
    // fields field_name, language and table_name become optional. Here we have detected that
    // table_name header is present, so this is the standard GTFS translation format. Check that the
    // standard required fields are present.
    if (!validateStandardRequiredFields(noticeContainer)) {
      return;
    }
    for (GtfsTranslation translation : translationTable.getEntities()) {
      validateTranslation(translation, noticeContainer);
    }
  }

  /**
   * Emits errors for missing required fields field_name, language and table_name.
   *
   * @return true if all required fields are set for all entities, false otherwise
   */
  private boolean validateStandardRequiredFields(NoticeContainer noticeContainer) {
    boolean isValid = true;
    for (GtfsTranslation translation : translationTable.getEntities()) {
      if (!translation.hasFieldName()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTranslationTableLoader.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslationTableLoader.FIELD_NAME_FIELD_NAME));
        isValid = false;
      }
      if (!translation.hasLanguage()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTranslationTableLoader.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslationTableLoader.LANGUAGE_FIELD_NAME));
        isValid = false;
      }
      if (!translation.hasTableName()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTranslationTableLoader.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslationTableLoader.TABLE_NAME_FIELD_NAME));
        isValid = false;
      }
    }
    return isValid;
  }

  /** Validates a single row in {@code translations.txt}. */
  private void validateTranslation(GtfsTranslation translation, NoticeContainer noticeContainer) {
    if (translation.hasFieldValue()) {
      if (translation.hasRecordId()) {
        noticeContainer.addValidationNotice(
            new TranslationUnexpectedValueNotice(
                translation,
                GtfsTranslationTableLoader.RECORD_ID_FIELD_NAME,
                translation.recordId()));
      }
      if (translation.hasRecordSubId()) {
        noticeContainer.addValidationNotice(
            new TranslationUnexpectedValueNotice(
                translation,
                GtfsTranslationTableLoader.RECORD_SUB_ID_FIELD_NAME,
                translation.recordSubId()));
      }
    }
    Optional<GtfsTableContainer<?>> parentTable =
        feedContainer.getTableForFilename(translation.tableName() + ".txt");
    if (!parentTable.isPresent() || parentTable.get().isMissingFile()) {
      noticeContainer.addValidationNotice(new TranslationUnknownTableNameNotice(translation));
    } else if (!translation.hasFieldValue()) {
      validateReferenceIntegrity(translation, parentTable.get(), noticeContainer);
    }
  }

  /**
   * Checks that {@code record_id, record_sub_id} fields are properly assigned and reference an
   * existing row in the parent table.
   */
  private void validateReferenceIntegrity(
      GtfsTranslation translation,
      GtfsTableContainer<?> parentTable,
      NoticeContainer noticeContainer) {
    ImmutableList<String> keyColumnNames = parentTable.getKeyColumnNames();
    if (isMissingOrUnexpectedField(
            translation,
            translation.hasRecordId(),
            keyColumnNames.size() >= 1,
            GtfsTranslationTableLoader.RECORD_ID_FIELD_NAME,
            translation.recordId(),
            noticeContainer)
        || isMissingOrUnexpectedField(
            translation,
            translation.hasRecordSubId(),
            keyColumnNames.size() >= 2,
            GtfsTranslationTableLoader.RECORD_SUB_ID_FIELD_NAME,
            translation.recordSubId(),
            noticeContainer)) {
      return;
    }
    if (!parentTable.byPrimaryKey(translation.recordId(), translation.recordSubId()).isPresent()) {
      noticeContainer.addValidationNotice(new TranslationForeignKeyViolationNotice(translation));
    }
  }

  /**
   * Checks that a field is present (or missing) as expected and emits errors if not.
   *
   * @return whether the field is missing or unexpected
   */
  private static boolean isMissingOrUnexpectedField(
      GtfsTranslation translation,
      boolean actualPresence,
      boolean expectedPresence,
      String fieldName,
      String fieldValue,
      NoticeContainer noticeContainer) {
    if (expectedPresence == actualPresence) {
      return false;
    }
    if (actualPresence) {
      noticeContainer.addValidationNotice(
          new TranslationUnexpectedValueNotice(translation, fieldName, fieldValue));
    } else {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(
              GtfsTranslationTableLoader.FILENAME, translation.csvRowNumber(), fieldName));
    }
    return true;
  }

  /** A translation references an unknown or missing GTFS table. */
  static class TranslationUnknownTableNameNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tableName;

    TranslationUnknownTableNameNotice(GtfsTranslation translation) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
    }
  }

  /** A field in a translations row has value but must be empty. */
  static class TranslationUnexpectedValueNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String fieldName;
    private final String fieldValue;

    TranslationUnexpectedValueNotice(
        GtfsTranslation translation, String fieldName, String fieldValue) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = translation.csvRowNumber();
      this.fieldValue = fieldValue;
      this.fieldName = fieldName;
    }
  }

  /**
   * An entity with the given {@code record_id, record_sub_id} cannot be found in the referenced
   * table.
   */
  static class TranslationForeignKeyViolationNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String tableName;
    private final String recordId;
    private final String recordSubId;

    TranslationForeignKeyViolationNotice(GtfsTranslation translation) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
      this.recordId = translation.recordId();
      this.recordSubId = translation.recordSubId();
    }
  }
}
