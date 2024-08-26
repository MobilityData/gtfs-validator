package org.mobilitydata.gtfsvalidator.table;

/**
 * Status of loading this table. This is includes parsing of the CSV file and validation of the
 * single file, but does not include any cross-file validations.
 */
public enum TableStatus {
  /** The file is completely empty, i.e. it has no rows and even no headers. */
  EMPTY_FILE,

  /** The file is missing in the GTFS feed. */
  MISSING_FILE,

  /** The file was parsed successfully. It has headers and 0, 1 or many rows. */
  PARSABLE_HEADERS_AND_ROWS,

  /**
   * The file has invalid headers, e.g., they failed to parse or some required headers are missing.
   * The other rows were not scanned.
   *
   * <p>Note that unknown headers are not considered invalid.
   */
  INVALID_HEADERS,

  /**
   * Some of the rows failed to parse, e.g., they have missing required fields or invalid field
   * values.
   *
   * <p>However, the headers are valid.
   *
   * <p>This does not include cross-file or cross-row validation. This also does not include
   * single-entity validation.
   */
  UNPARSABLE_ROWS,
}
