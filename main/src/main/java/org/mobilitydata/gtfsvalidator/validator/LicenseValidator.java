/*
 * Copyright 2026 MobilityData IO
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

import com.google.common.base.Ascii;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsGeoJsonFeature;
import org.mobilitydata.gtfsvalidator.table.GtfsLicense;
import org.mobilitydata.gtfsvalidator.table.GtfsLicenseSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsLicenseTableContainer;

/**
 * Validates records of `licenses.txt`.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link LicensesUnknownTableNameNotice}
 *   <li>{@link MissingLicenseSpdxIdOrCustomLicenseUrlNotice}
 *   <li>{@link ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice}
 *   <li>{@link DuplicateKeyNotice}
 * </ul>
 */
@GtfsValidator
public class LicenseValidator extends FileValidator {

  private static final String GEOJSON_FILENAME = GtfsGeoJsonFeature.FILENAME;
  private static final String GEOJSON_TABLE_NAME =
      GEOJSON_FILENAME.substring(0, GEOJSON_FILENAME.lastIndexOf('.'));

  private final GtfsLicenseTableContainer licenseTable;
  private final GtfsFeedContainer feedContainer;

  @Inject
  LicenseValidator(GtfsLicenseTableContainer licenseTable, GtfsFeedContainer feedContainer) {
    this.licenseTable = licenseTable;
    this.feedContainer = feedContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    // Table names are matched case-insensitively and `locations` and `locations.geojson` name the
    // same file, so different values can refer to the same file. The primary key check only
    // catches identical values; this catches the rest.
    Map<String, GtfsLicense> licenseByNormalizedTableName = new HashMap<>();
    for (GtfsLicense license : licenseTable.getEntities()) {
      validateLicense(license, noticeContainer);
      if (!license.hasLicensedTableName()) {
        continue;
      }
      GtfsLicense first =
          licenseByNormalizedTableName.putIfAbsent(
              normalizeTableName(license.licensedTableName()), license);
      if (first != null && !first.licensedTableName().equals(license.licensedTableName())) {
        noticeContainer.addValidationNotice(
            new DuplicateKeyNotice(
                GtfsLicense.FILENAME,
                first.csvRowNumber(),
                license.csvRowNumber(),
                GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME,
                license.licensedTableName()));
      }
    }
  }

  private void validateLicense(GtfsLicense license, NoticeContainer noticeContainer) {
    if (license.hasLicensedTableName() && !isKnownTableName(license.licensedTableName())) {
      noticeContainer.addValidationNotice(new LicensesUnknownTableNameNotice(license));
    }
    if (!license.hasLicenseSpdxId() && !license.hasCustomLicenseUrl()) {
      noticeContainer.addValidationNotice(
          new MissingLicenseSpdxIdOrCustomLicenseUrlNotice(license.csvRowNumber()));
    } else if (license.hasLicenseSpdxId() && license.hasCustomLicenseUrl()) {
      noticeContainer.addValidationNotice(
          new ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice(license.csvRowNumber()));
    }
  }

  /**
   * Tells whether {@code tableName} refers to a known GTFS file.
   *
   * <p>A `.txt` file is referred to without its extension (e.g. `shapes`). `locations.geojson` may
   * be referred to as either `locations` or `locations.geojson`. Matching is case-insensitive, like
   * file loading.
   */
  private boolean isKnownTableName(String tableName) {
    return normalizeTableName(tableName).equals(GEOJSON_TABLE_NAME)
        || feedContainer.getTableForFilename(tableName + ".txt").isPresent();
  }

  /** Lowercases {@code tableName} and maps `locations.geojson` to `locations`. */
  private static String normalizeTableName(String tableName) {
    String normalized = Ascii.toLowerCase(tableName);
    return normalized.equals(GEOJSON_FILENAME) ? GEOJSON_TABLE_NAME : normalized;
  }

  /**
   * A `licenses.txt` table name does not match any known GTFS file.
   *
   * <p>`licensed_table_name` should be the name of a GTFS file, omitting the `.txt` extension.
   * `locations.geojson` may be referred to as `locations` or `locations.geojson`.
   */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsLicenseSchema.class))
  static class LicensesUnknownTableNameNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The name of the faulty field. */
    private final String fieldName;

    /** The value of the faulty field. */
    private final String fieldValue;

    /** The name of the faulty file. */
    private final String filename;

    LicensesUnknownTableNameNotice(GtfsLicense license) {
      this.csvRowNumber = license.csvRowNumber();
      this.fieldName = GtfsLicense.LICENSED_TABLE_NAME_FIELD_NAME;
      this.fieldValue = license.licensedTableName();
      this.filename = GtfsLicense.FILENAME;
    }
  }

  /**
   * `licenses.txt` requires exactly one of `license_spdx_id` or `custom_license_url` and both are
   * empty.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsLicenseSchema.class))
  static class MissingLicenseSpdxIdOrCustomLicenseUrlNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The name of the faulty file. */
    private final String filename;

    MissingLicenseSpdxIdOrCustomLicenseUrlNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
      this.filename = GtfsLicense.FILENAME;
    }
  }

  /** A `licenses.txt` record contains both `license_spdx_id` and `custom_license_url`. */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsLicenseSchema.class))
  static class ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The name of the faulty file. */
    private final String filename;

    ForbiddenLicenseSpdxIdOrCustomLicenseUrlNotice(int csvRowNumber) {
      this.csvRowNumber = csvRowNumber;
      this.filename = GtfsLicense.FILENAME;
    }
  }
}
