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

package org.mobilitydata.gtfsvalidator.parsing;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.mobilitydata.gtfsvalidator.annotation.FieldLevelEnum;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidColorNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidDateNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidFloatNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidIntegerNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidLanguageCodeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimezoneNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRecommendedFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.NumberOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.notice.TooManyRowsNotice;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsColumnDescriptor;
import org.mobilitydata.gtfsvalidator.table.GtfsEnum;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.validator.GtfsCellContext;
import org.mobilitydata.gtfsvalidator.validator.GtfsFieldValidator;

/**
 * Parses cells of a CSV row as values of requested data types.
 *
 * <p>Interface functions of this class receive an instance of {@code NoticeContainer}. If a cell
 * value cannot be parsed, these functions add a notice to the container, return null and don't
 * throw an exception.
 */
public class RowParser {

  private final String fileName;
  private final CsvHeader header;
  private final GtfsFieldValidator fieldValidator;
  private CsvRow row;

  // The largest CSV files contain about 100 M rows (as of 2022). Set the limit to 1 billion.
  private static final long MAX_ROW_NUMBER = 1000000000;

  private NoticeContainer noticeContainer;

  public RowParser(String fileName, CsvHeader header, GtfsFieldValidator fieldValidator) {
    this.fileName = fileName;
    this.header = header;
    this.fieldValidator = fieldValidator;
  }

  public void setRow(CsvRow row, NoticeContainer noticeContainer) {
    this.row = row;
    this.noticeContainer = noticeContainer;
  }

  public NoticeContainer getNoticeContainer() {
    return noticeContainer;
  }

  public int getRowNumber() {
    return (int) row.getRowNumber();
  }

  public boolean checkRowNumber() {
    if (row.getRowNumber() > MAX_ROW_NUMBER) {
      noticeContainer.addValidationNotice(new TooManyRowsNotice(fileName, row.getRowNumber()));
      return false;
    }
    return true;
  }

  /**
   * Checks whether the row lengths (cell count) is the same as the amount of file headers.
   *
   * <p>This function may add notices to {@code noticeContainer}.
   *
   * @return true if the row length is equal to column count
   */
  public boolean checkRowLength() {
    final int rowColumnCount = row.getColumnCount();
    if (rowColumnCount == 0) {
      // Empty row.
      return false;
    }

    if (rowColumnCount == 1 && row.asString(0) == null) {
      // If the last row has only spaces and does not end with a newline, then Univocity parser
      // interprets it as a non-empty row that has a single column which is empty (sic!). We are
      // unsure if this is a bug or feature in Univocity, so we show a warning.
      noticeContainer.addValidationNotice(new EmptyRowNotice(fileName, getRowNumber()));
      return false;
    }

    if (rowColumnCount != header.getColumnCount()) {
      noticeContainer.addValidationNotice(
          new InvalidRowLengthNotice(
              fileName, getRowNumber(), rowColumnCount, header.getColumnCount()));
      return false;
    }
    return true;
  }

  //  @Nullable
  //  public String asString(int columnIndex, FieldLevelEnum level) {
  //    String s = row.asString(columnIndex);
  //    if (level == FieldLevelEnum.REQUIRED && s == null) {
  //      noticeContainer.addValidationNotice(
  //          new MissingRequiredFieldNotice(
  //              fileName, getRowNumber(), header.getColumnName(columnIndex)));
  //    } else if (level == FieldLevelEnum.RECOMMENDED && s == null) {
  //      noticeContainer.addValidationNotice(
  //          new MissingRecommendedFieldNotice(
  //              fileName, getRowNumber(), header.getColumnName(columnIndex)));
  //    }
  //    if (s != null) {
  //      s =
  //          fieldValidator.validateField(
  //              s,
  //              GtfsCellContext.create(fileName, getRowNumber(),
  // header.getColumnName(columnIndex)),
  //              noticeContainer);
  //    }
  //    return s;
  //  }

  @Nullable
  public String asString(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    String s = row.asString(columnIndex);
    if (columnDescriptor.fieldLevel() == FieldLevelEnum.REQUIRED && s == null) {
      noticeContainer.addValidationNotice(
          new MissingRequiredFieldNotice(fileName, getRowNumber(), columnDescriptor.columnName()));
    } else if (columnDescriptor.fieldLevel() == FieldLevelEnum.RECOMMENDED && s == null) {
      noticeContainer.addValidationNotice(
          new MissingRecommendedFieldNotice(
              fileName, getRowNumber(), columnDescriptor.columnName()));
    }
    if (s != null) {
      s =
          fieldValidator.validateField(
              s,
              GtfsCellContext.create(fileName, getRowNumber(), header.getColumnName(columnIndex)),
              noticeContainer);
    }
    return s;
  }

  @Nullable
  public String asText(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return asString(columnIndex, columnDescriptor);
  }

  @Nullable
  public String asId(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return asValidatedString(columnIndex, columnDescriptor, fieldValidator::validateId);
  }

  @Nullable
  public String asUrl(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return asValidatedString(columnIndex, columnDescriptor, fieldValidator::validateUrl);
  }

  @Nullable
  public String asEmail(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return asValidatedString(columnIndex, columnDescriptor, fieldValidator::validateEmail);
  }

  /**
   * Returns the string value of the phone number to be validated if a valid number according to the
   * {@code CountryCode}, returns {@code null} otherwise. Note that if {@code CountryCode} is
   * unknown, only phone number starting by "+" are validated.
   *
   * @param columnIndex the column index
   * @param columnDescriptor Gtfs Column Descriptor
   * @return the string value of the phone number to be validated if a valid number according to the
   *     {@code CountryCode}, returns {@code null} otherwise. Note that if {@code CountryCode} is
   *     unknown, only phone number starting by "+" are validated.
   */
  @Nullable
  public String asPhoneNumber(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return asValidatedString(columnIndex, columnDescriptor, fieldValidator::validatePhoneNumber);
  }

  @Nullable
  /**
   * Reads the value at the specified {@code columnIndex} and parses it as {@code Locale}.
   *
   * @param columnIndex the column index
   * @param level whether the value is required, recommended or optional according to GTFS
   * @return If parsing was successful returns {@code Locale}, otherwise, {@code null} is returned.
   */
  public Locale asLanguageCode(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(
        columnIndex, columnDescriptor, RowParser::parseLocale, InvalidLanguageCodeNotice::new);
  }

  /**
   * Returns a well-formed IETF BCP 47 language tag representing this locale.
   *
   * @return a BCP47 locale representing the {@code languageTag}.
   * @throws java.util.IllformedLocaleException if the {@code languageTag} doesn't match with IETF
   *     BCP 47 standard.
   * @see java.util.Locale.Builder#setLanguageTag(String) and check how <a
   *     href="https://docs.oracle.com/javase/tutorial/i18n/locale/extensions.html">BCP 47
   *     Extensions</a> implemented in java.
   */
  private static Locale parseLocale(String languageTag) {
    return new Locale.Builder().setLanguageTag(languageTag).build();
  }

  @Nullable
  public ZoneId asTimezone(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, ZoneId::of, InvalidTimezoneNotice::new);
  }

  @Nullable
  public Currency asCurrencyCode(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(
        columnIndex, columnDescriptor, Currency::getInstance, InvalidCurrencyNotice::new);
  }

  @Nullable
  public Double asFloat(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, Double::parseDouble, InvalidFloatNotice::new);
  }

  @Nullable
  public Double asFloat(
      int columnIndex, GtfsColumnDescriptor columnDescriptor, NumberBounds bounds) {
    return checkBounds(asFloat(columnIndex, columnDescriptor), 0.0, columnIndex, "float", bounds);
  }

  @Nullable
  public Double asLatitude(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    Double value = asFloat(columnIndex, columnDescriptor);
    if (value != null && !(-90 <= value && value <= 90)) {
      noticeContainer.addValidationNotice(
          new NumberOutOfRangeNotice(
              fileName,
              getRowNumber(),
              header.getColumnName(columnIndex),
              "latitude within [-90, 90]",
              value));
      return value;
    }
    return value;
  }

  @Nullable
  public Double asLongitude(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    Double value = asFloat(columnIndex, columnDescriptor);
    if (value != null && !(-180 <= value && value <= 180)) {
      noticeContainer.addValidationNotice(
          new NumberOutOfRangeNotice(
              fileName,
              getRowNumber(),
              header.getColumnName(columnIndex),
              "longitude within [-180, 180]",
              value));
      return value;
    }
    return value;
  }

  @Nullable
  public Integer asInteger(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, Integer::parseInt, InvalidIntegerNotice::new);
  }

  @Nullable
  public Integer asInteger(
      int columnIndex, GtfsColumnDescriptor columnDescriptor, NumberBounds bounds) {
    return checkBounds(asInteger(columnIndex, columnDescriptor), 0, columnIndex, "integer", bounds);
  }

  @Nullable
  public BigDecimal asDecimal(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, BigDecimal::new, InvalidFloatNotice::new);
  }

  @Nullable
  public BigDecimal asDecimal(
      int columnIndex, GtfsColumnDescriptor columnDescriptor, NumberBounds bounds) {
    return checkBounds(
        asDecimal(columnIndex, columnDescriptor),
        new BigDecimal(0),
        columnIndex,
        "decimal",
        bounds);
  }

  /**
   * Checks that the value meets boundary requirements.
   *
   * @param value the value to check
   * @param zero a zero instance for the given type
   * @param columnIndex index of CSV column where the value is located
   * @param typeName short descriptive type name, such as "integer"
   * @param bounds boundary requirements
   * @param <T> the actual type of the value
   * @return the same value as passed to the function
   */
  private <T extends Comparable<T>> T checkBounds(
      @Nullable T value, T zero, int columnIndex, String typeName, NumberBounds bounds) {
    if (value == null) {
      return null;
    }
    final int compareToZero = value.compareTo(zero);
    switch (bounds) {
      case POSITIVE:
        if (compareToZero <= 0) {
          noticeContainer.addValidationNotice(
              new NumberOutOfRangeNotice(
                  fileName,
                  getRowNumber(),
                  header.getColumnName(columnIndex),
                  "positive " + typeName,
                  value));
        }
        break;
      case NON_NEGATIVE:
        if (compareToZero < 0) {
          noticeContainer.addValidationNotice(
              new NumberOutOfRangeNotice(
                  fileName,
                  getRowNumber(),
                  header.getColumnName(columnIndex),
                  "non-negative " + typeName,
                  value));
        }
        break;
      case NON_ZERO:
        if (compareToZero == 0) {
          noticeContainer.addValidationNotice(
              new NumberOutOfRangeNotice(
                  fileName,
                  getRowNumber(),
                  header.getColumnName(columnIndex),
                  "non-zero " + typeName,
                  value));
        }
        break;
    }
    return value;
  }

  @Nullable
  public GtfsColor asColor(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(
        columnIndex, columnDescriptor, GtfsColor::fromString, InvalidColorNotice::new);
  }

  @Nullable
  public <E extends GtfsEnum> Integer asEnum(
      int columnIndex,
      GtfsColumnDescriptor columnDescriptor,
      EnumCreator<E> enumCreator,
      E unrecognized) {
    Integer i = asInteger(columnIndex, columnDescriptor);
    if (i == null) {
      return null;
    }
    if (enumCreator.convert(i) == null) {
      noticeContainer.addValidationNotice(
          new UnexpectedEnumValueNotice(
              fileName, getRowNumber(), header.getColumnName(columnIndex), i));
      return unrecognized.getNumber();
    }
    return i;
  }

  @Nullable
  public GtfsTime asTime(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, GtfsTime::fromString, InvalidTimeNotice::new);
  }

  @Nullable
  public GtfsDate asDate(int columnIndex, GtfsColumnDescriptor columnDescriptor) {
    return parseAsType(columnIndex, columnDescriptor, GtfsDate::fromString, InvalidDateNotice::new);
  }

  public enum NumberBounds {
    POSITIVE,
    NON_NEGATIVE,
    NON_ZERO,
  }

  @FunctionalInterface
  public interface EnumCreator<E> {

    E convert(int t);
  }

  /**
   * Parses a string to a given type: {@link GtfsTime}, {@link GtfsColor} etc. and adds notices if
   * parsing failed.
   *
   * @param columnIndex index of the column to parse
   * @param columnDescriptor Gtfs Column Descriptor
   * @param parsingFunction function that converts string to an object to return
   * @param noticingFunction function to create a notice about parse errors
   * @param <T> the type to return
   * @return a parsed object or null if the value is missing or invalid
   */
  @Nullable
  private <T> T parseAsType(
      int columnIndex,
      GtfsColumnDescriptor columnDescriptor,
      Function<String, T> parsingFunction,
      NoticingFunction noticingFunction) {
    String s = asString(columnIndex, columnDescriptor);
    if (s == null) {
      return null;
    }
    try {
      return parsingFunction.apply(s);
    } catch (RuntimeException e) {
      // Most parsing functions throw an IllegalArgumentException but ZoneId.of() throws
      // a ZoneRulesException or DateTimeException. Be sure to catch all of them.
      noticeContainer.addValidationNotice(
          noticingFunction.apply(fileName, getRowNumber(), header.getColumnName(columnIndex), s));
      return null;
    }
  }

  /**
   * Validates a string as URL, phone number etc. and adds notices for invalid values.
   *
   * <p>{@code validatingFunction} can emit errors or warnings to {@code noticeContainer}. If an
   * error is emitted, the value is considered invalid.
   *
   * @param columnIndex index of the column to parse
   * @param columnDescriptor GTFS column descriptor
   * @param validatingFunction the predicate to validate a given string
   * @return the cell value at the given column or null if the value is missing
   */
  @Nullable
  private String asValidatedString(
      int columnIndex,
      GtfsColumnDescriptor columnDescriptor,
      FieldValidatingFunction validatingFunction) {
    String s = asString(columnIndex, columnDescriptor);
    if (s == null) {
      return null;
    }
    validatingFunction.apply(
        s,
        GtfsCellContext.create(fileName, getRowNumber(), header.getColumnName(columnIndex)),
        noticeContainer);
    return s;
  }

  /**
   * Generates a validation notice for the given value at the given position in a file.
   *
   * @param <T> the type of a returned notice
   */
  @FunctionalInterface
  private interface NoticingFunction<T extends ValidationNotice> {

    T apply(String filename, int csvRowNumber, String fieldName, String fieldValue);
  }

  /** Validates the given field and adds appropriate notices to the container. */
  @FunctionalInterface
  private interface FieldValidatingFunction {

    void apply(String fieldValue, GtfsCellContext cellContext, NoticeContainer noticeContainer);
  }
}
