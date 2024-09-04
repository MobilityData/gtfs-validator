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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import com.google.common.collect.ImmutableList;
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

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
    if (!translationTable.getHeader().hasColumn(GtfsTranslation.TABLE_NAME_FIELD_NAME)) {
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
                GtfsTranslation.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslation.FIELD_NAME_FIELD_NAME));
        isValid = false;
      }
      if (!translation.hasLanguage()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTranslation.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslation.LANGUAGE_FIELD_NAME));
        isValid = false;
      }
      if (!translation.hasTableName()) {
        noticeContainer.addValidationNotice(
            new MissingRequiredFieldNotice(
                GtfsTranslation.FILENAME,
                translation.csvRowNumber(),
                GtfsTranslation.TABLE_NAME_FIELD_NAME));
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
                translation, GtfsTranslation.RECORD_ID_FIELD_NAME, translation.recordId()));
      }
      if (translation.hasRecordSubId()) {
        noticeContainer.addValidationNotice(
            new TranslationUnexpectedValueNotice(
                translation, GtfsTranslation.RECORD_SUB_ID_FIELD_NAME, translation.recordSubId()));
      }
    }
    Optional<GtfsEntityContainer<GtfsTranslation, GtfsTranslationTableDescriptor>> parentTable =
        feedContainer.getTableForFilename(translation.tableName() + ".txt");
    if (parentTable.isEmpty() || parentTable.get().isMissingFile()) {
      noticeContainer.addValidationNotice(new TranslationUnknownTableNameNotice(translation));
    } else if (!translation.hasFieldValue()) {
      if (parentTable.isPresent() && parentTable.get() instanceof GtfsTableContainer) {
        validateReferenceIntegrity(
            translation, (GtfsTableContainer) parentTable.get(), noticeContainer);
      } else {
        //        TODO check for JSON Tables here
      }
    }
  }

  /**
   * Checks that {@code record_id, record_sub_id} fields are properly assigned and reference an
   * existing row in the parent table.
   */
  private void validateReferenceIntegrity(
      GtfsTranslation translation,
      GtfsTableContainer<?, ?> parentTable,
      NoticeContainer noticeContainer) {
    ImmutableList<String> keyColumnNames = parentTable.getKeyColumnNames();
    if (isMissingOrUnexpectedField(
            translation,
            translation.hasRecordId(),
            keyColumnNames.size() >= 1,
            GtfsTranslation.RECORD_ID_FIELD_NAME,
            translation.recordId(),
            noticeContainer)
        || isMissingOrUnexpectedField(
            translation,
            translation.hasRecordSubId(),
            keyColumnNames.size() >= 2,
            GtfsTranslation.RECORD_SUB_ID_FIELD_NAME,
            translation.recordSubId(),
            noticeContainer)) {
      return;
    }
    Optional<?> entity =
        parentTable.byTranslationKey(translation.recordId(), translation.recordSubId());
    if (entity.isEmpty()) {
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
              GtfsTranslation.FILENAME, translation.csvRowNumber(), fieldName));
    }
    return true;
  }

  /** A field in a translations row has value but must be empty. */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTranslationSchema.class))
  static class TranslationUnexpectedValueNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The name of the field that was expected to be empty. */
    private final String fieldName;

    /** Actual value of the field that was expected to be empty. */
    private final String fieldValue;

    TranslationUnexpectedValueNotice(
        GtfsTranslation translation, String fieldName, String fieldValue) {
      this.csvRowNumber = translation.csvRowNumber();
      this.fieldValue = fieldValue;
      this.fieldName = fieldName;
    }
  }

  /** A translation references an unknown or missing GTFS table. */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsTranslationSchema.class))
  static class TranslationUnknownTableNameNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** `table_name` of the faulty record. */
    private final String tableName;

    TranslationUnknownTableNameNotice(GtfsTranslation translation) {
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
    }
  }

  /**
   * An entity with the given `record_id` and `record_sub_id` cannot be found in the referenced
   * table.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTranslationSchema.class))
  static class TranslationForeignKeyViolationNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** `table_name` of the faulty record. */
    private final String tableName;

    /** `record_id` of the faulty record. */
    private final String recordId;

    /** `record_sub_id` of the faulty record. */
    private final String recordSubId;

    TranslationForeignKeyViolationNotice(GtfsTranslation translation) {
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
      this.recordId = translation.recordId();
      this.recordSubId = translation.recordSubId();
    }
  }
}
