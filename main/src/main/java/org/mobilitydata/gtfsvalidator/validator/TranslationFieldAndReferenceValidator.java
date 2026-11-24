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

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
 */
@GtfsValidator
public class TranslationFieldAndReferenceValidator extends FileValidator {

  private final GtfsTranslationTableContainer translationTable;

  private final GtfsFeedContainer feedContainer;

  /**
   * Values of the columns referenced by {@code field_value} translations, keyed by file name and
   * column name, so each column is read only once.
   */
  private final Map<String, Optional<Set<String>>> fieldValuesCache = new HashMap<>();

  @Inject
  TranslationFieldAndReferenceValidator(
      GtfsTranslationTableContainer translationTable, GtfsFeedContainer feedContainer) {
    this.translationTable = translationTable;
    this.feedContainer = feedContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsTranslation translation : translationTable.getEntities()) {
      validateTranslation(translation, noticeContainer);
    }
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
    Optional<GtfsEntityContainer<GtfsTranslation>> parentTable =
        feedContainer.getTableForFilename(translation.tableName() + ".txt");
    if (parentTable.isEmpty() || parentTable.get().isMissingFile()) {
      noticeContainer.addValidationNotice(new TranslationUnknownTableNameNotice(translation));
      return;
    }
    if (!isKnownFieldName(translation, parentTable.get())) {
      noticeContainer.addValidationNotice(new TranslationUnknownFieldNameNotice(translation));
      return;
    }
    if (!translation.hasFieldValue()) {
      if (parentTable.get() instanceof GtfsTableContainer) {
        validateReferenceIntegrity(
            translation, (GtfsTableContainer) parentTable.get(), noticeContainer);
      } else {
        //        TODO check for JSON Tables here
      }
    } else if (parentTable.get() instanceof GtfsTableContainer) {
      validateFieldValueReference(
          translation, (GtfsTableContainer<?, ?>) parentTable.get(), noticeContainer);
    }
  }

  /**
   * Returns whether {@code field_name} is a column defined for the parent table.
   *
   * <p>Returns true for tables that are not CSV, such as {@code locations.geojson}, since their
   * fields are not described by columns.
   */
  private static boolean isKnownFieldName(
      GtfsTranslation translation, GtfsEntityContainer<?> parentTable) {
    if (!(parentTable.getDescriptor() instanceof GtfsTableDescriptor)) {
      return true;
    }
    return ((GtfsTableDescriptor<?>) parentTable.getDescriptor())
        .getColumns().stream()
            .anyMatch(column -> column.columnName().equals(translation.fieldName()));
  }

  /**
   * Checks that at least one row of the parent table has {@code field_value} in the column named by
   * {@code field_name}.
   *
   * <p>Only text-like columns are checked: per the GTFS specification, other field types should not
   * be translated.
   */
  private void validateFieldValueReference(
      GtfsTranslation translation,
      GtfsTableContainer<?, ?> parentTable,
      NoticeContainer noticeContainer) {
    Optional<Set<String>> values =
        fieldValuesCache.computeIfAbsent(
            parentTable.gtfsFilename() + "/" + translation.fieldName(),
            key -> collectFieldValues(parentTable, translation.fieldName()));
    if (values.isPresent() && !values.get().contains(translation.fieldValue())) {
      noticeContainer.addValidationNotice(new TranslationUnknownFieldValueNotice(translation));
    }
  }

  /**
   * Collects every value of a {@code String} column in a table.
   *
   * @return the values, or empty if the column is not a {@code String} column and so cannot be
   *     compared with {@code field_value}
   */
  private static Optional<Set<String>> collectFieldValues(
      GtfsTableContainer<?, ?> table, String columnName) {
    Method getter;
    try {
      // Entities have no accessor by column name, so the generated getter is found by reflection
      // (route_short_name -> routeShortName()). This is safe because columnName has already been
      // checked against the schema columns, and it runs once per column thanks to the cache.
      getter =
          table
              .getEntityClass()
              .getMethod(CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName));
    } catch (NoSuchMethodException e) {
      return Optional.empty();
    }
    if (getter.getReturnType() != String.class) {
      return Optional.empty();
    }
    Set<String> values = new HashSet<>();
    try {
      for (GtfsEntity entity : table.getEntities()) {
        values.add((String) getter.invoke(entity));
      }
    } catch (ReflectiveOperationException e) {
      return Optional.empty();
    }
    return Optional.of(values);
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
   * A translation references a field that is not defined for the referenced table.
   *
   * <p>`field_name` must be the name of a field of the table given in `table_name`.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsTranslationSchema.class))
  static class TranslationUnknownFieldNameNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** `table_name` of the faulty record. */
    private final String tableName;

    /** `field_name` of the faulty record. */
    private final String fieldName;

    TranslationUnknownFieldNameNotice(GtfsTranslation translation) {
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
      this.fieldName = translation.fieldName();
    }
  }

  /**
   * A translation references an unknown or missing GTFS field value.
   *
   * <p>No row of the table given in `table_name` has exactly `field_value` in the field given by
   * `field_name`, so the translation is never applied.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsTranslationSchema.class))
  static class TranslationUnknownFieldValueNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** `table_name` of the faulty record. */
    private final String tableName;

    /** `field_name` of the faulty record. */
    private final String fieldName;

    /** `field_value` of the faulty record. */
    private final String fieldValue;

    TranslationUnknownFieldValueNotice(GtfsTranslation translation) {
      this.csvRowNumber = translation.csvRowNumber();
      this.tableName = translation.tableName();
      this.fieldName = translation.fieldName();
      this.fieldValue = translation.fieldValue();
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
