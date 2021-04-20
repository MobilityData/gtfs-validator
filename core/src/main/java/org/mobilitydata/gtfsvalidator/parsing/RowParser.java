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

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Currency;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidColorNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidCurrencyNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidDateNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidEmailNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidFloatNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidIntegerNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidLanguageCodeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidPhoneNumberNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimeNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidTimezoneNotice;
import org.mobilitydata.gtfsvalidator.notice.InvalidUrlNotice;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.NumberOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Parses cells of a CSV row as values of requested data types.
 *
 * <p>Interface functions of this class receive an instance of {@code NoticeContainer}. If a cell
 * value cannot be parsed, these functions add a notice to the container, return null and don't
 * throw an exception.
 */
public class RowParser {

  public static final boolean REQUIRED = true;
  public static final boolean OPTIONAL = false;
  private final NoticeContainer noticeContainer;
  private final CountryCode countryCode;
  private CsvRow row;
  private boolean parseErrorsInRow;

  public RowParser(CountryCode countryCode, NoticeContainer noticeContainer) {
    this.countryCode = countryCode;
    this.noticeContainer = noticeContainer;
  }

  public NoticeContainer getNoticeContainer() {
    return noticeContainer;
  }

  public void setRow(CsvRow row) {
    this.row = row;
    this.parseErrorsInRow = false;
  }

  public boolean hasParseErrorsInRow() {
    return parseErrorsInRow;
  }

  /**
   * Checks whether the row lengths (cell count) is the same as the amount of file headers.
   *
   * <p>This function may add notices to {@code noticeContainer}.
   *
   * @return true if the row length is equal to column count
   */
  public boolean checkRowLength() {
    if (row.getColumnCount() == 0) {
      // Empty row.
      return false;
    }

    CsvFile csvFile = row.getCsvFile();
    if (row.getColumnCount() == 1 && row.asString(0) == null) {
      // If the last row has only spaces and does not end with a newline, then Univocity parser
      // interprets it as a non-empty row that has a single column which is empty (sic!). We are
      // unsure if this is a bug or feature in Univocity, so we show a warning.
      addNoticeInRow(new EmptyRowNotice(csvFile.getFileName(), row.getRowNumber()));
      return false;
    }

    if (row.getColumnCount() != csvFile.getColumnCount()) {
      addNoticeInRow(
          new InvalidRowLengthNotice(
              csvFile.getFileName(),
              row.getRowNumber(),
              row.getColumnCount(),
              csvFile.getColumnCount()));
      return false;
    }
    return true;
  }

  @Nullable
  public String asString(int columnIndex, boolean required) {
    String s = row.asString(columnIndex);
    if (required && s == null) {
      addNoticeInRow(
          new MissingRequiredFieldNotice(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex)));
    }
    if (s != null) {
      if (s.indexOf('\n') != -1 || s.indexOf('\r') != -1) {
        addNoticeInRow(
            new NewLineInValueNotice(
                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s));
      }
      final String trimmed = s.trim();
      if (trimmed.length() < s.length()) {
        addNoticeInRow(
            new LeadingOrTrailingWhitespacesNotice(
                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s));
        s = trimmed;
      }
    }
    return s;
  }

  @Nullable
  public String asText(int columnIndex, boolean required) {
    return asString(columnIndex, required);
  }

  static boolean hasOnlyPrintableAscii(String s) {
    for (int i = 0, n = s.length(); i < n; ++i) {
      if (!(s.charAt(i) >= 32 && s.charAt(i) < 127)) {
        return false;
      }
    }
    return true;
  }

  @Nullable
  public String asId(int columnIndex, boolean required) {
    return asValidatedString(
        columnIndex,
        required,
        RowParser::hasOnlyPrintableAscii,
        NonAsciiOrNonPrintableCharNotice::new);
  }

  @Nullable
  public String asUrl(int columnIndex, boolean required) {
    return asValidatedString(
        columnIndex, required, s -> UrlValidator.getInstance().isValid(s), InvalidUrlNotice::new);
  }

  @Nullable
  public String asEmail(int columnIndex, boolean required) {
    return asValidatedString(
        columnIndex,
        required,
        s -> EmailValidator.getInstance().isValid(s),
        InvalidEmailNotice::new);
  }

  @Nullable
  public String asPhoneNumber(int columnIndex, boolean required) {
    if (!countryCode.getCountryCode().isPresent()) {
      return asString(columnIndex, required);
    }
    return asValidatedString(
        columnIndex,
        required,
        s ->
            PhoneNumberUtil.getInstance()
                .isPossibleNumber(s, countryCode.getCountryCode().get()),
        InvalidPhoneNumberNotice::new);
  }

  @Nullable
  public Locale asLanguageCode(int columnIndex, boolean required) {
    return parseAsType(
        columnIndex, required, Locale::forLanguageTag, InvalidLanguageCodeNotice::new);
  }

  @Nullable
  public ZoneId asTimezone(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, ZoneId::of, InvalidTimezoneNotice::new);
  }

  @Nullable
  public Currency asCurrencyCode(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, Currency::getInstance, InvalidCurrencyNotice::new);
  }

  @Nullable
  public Double asFloat(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, Double::parseDouble, InvalidFloatNotice::new);
  }

  @Nullable
  public Double asFloat(int columnIndex, boolean required, NumberBounds bounds) {
    return checkBounds(asFloat(columnIndex, required), 0.0, columnIndex, "float", bounds);
  }

  @Nullable
  public Double asLatitude(int columnIndex, boolean required) {
    Double value = asFloat(columnIndex, required);
    if (value != null && !(-90 <= value && value <= 90)) {
      addNoticeInRow(
          new NumberOutOfRangeNotice(
              row.getFileName(),
              row.getRowNumber(),
              row.getColumnName(columnIndex),
              "latitude within [-90, 90]",
              value));
      return null;
    }
    return value;
  }

  @Nullable
  public Double asLongitude(int columnIndex, boolean required) {
    Double value = asFloat(columnIndex, required);
    if (value != null && !(-180 <= value && value <= 180)) {
      addNoticeInRow(
          new NumberOutOfRangeNotice(
              row.getFileName(),
              row.getRowNumber(),
              row.getColumnName(columnIndex),
              "longitude within [-180, 180]",
              value));
      return null;
    }
    return value;
  }

  @Nullable
  public Integer asInteger(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, Integer::parseInt, InvalidIntegerNotice::new);
  }

  @Nullable
  public Integer asInteger(int columnIndex, boolean required, NumberBounds bounds) {
    return checkBounds(asInteger(columnIndex, required), 0, columnIndex, "integer", bounds);
  }

  @Nullable
  public BigDecimal asDecimal(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, BigDecimal::new, InvalidFloatNotice::new);
  }

  @Nullable
  public BigDecimal asDecimal(int columnIndex, boolean required, NumberBounds bounds) {
    return checkBounds(
        asDecimal(columnIndex, required), new BigDecimal(0), columnIndex, "decimal", bounds);
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
          addNoticeInRow(
              new NumberOutOfRangeNotice(
                  row.getFileName(),
                  row.getRowNumber(),
                  row.getColumnName(columnIndex),
                  "positive " + typeName,
                  value));
        }
        break;
      case NON_NEGATIVE:
        if (compareToZero < 0) {
          addNoticeInRow(
              new NumberOutOfRangeNotice(
                  row.getFileName(),
                  row.getRowNumber(),
                  row.getColumnName(columnIndex),
                  "non-negative " + typeName,
                  value));
        }
        break;
      case NON_ZERO:
        if (compareToZero == 0) {
          addNoticeInRow(
              new NumberOutOfRangeNotice(
                  row.getFileName(),
                  row.getRowNumber(),
                  row.getColumnName(columnIndex),
                  "non-zero " + typeName,
                  value));
        }
        break;
    }
    return value;
  }

  @Nullable
  public GtfsColor asColor(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, GtfsColor::fromString, InvalidColorNotice::new);
  }

  @Nullable
  public <E> Integer asEnum(int columnIndex, boolean required, EnumCreator<E> enumCreator) {
    Integer i = asInteger(columnIndex, required);
    if (i == null) {
      return null;
    }
    if (enumCreator.convert(i) == null) {
      addNoticeInRow(
          new UnexpectedEnumValueNotice(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), i));
    }
    return i;
  }

  @Nullable
  public GtfsTime asTime(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, GtfsTime::fromString, InvalidTimeNotice::new);
  }

  @Nullable
  public GtfsDate asDate(int columnIndex, boolean required) {
    return parseAsType(columnIndex, required, GtfsDate::fromString, InvalidDateNotice::new);
  }

  /**
   * Tells if a given notice is an {@code ERROR}.
   *
   * @param notice the notice to check
   * @return true if the notice is an error, false otherwise
   */
  private static boolean isError(ValidationNotice notice) {
    return notice.getSeverityLevel().ordinal() >= SeverityLevel.ERROR.ordinal();
  }

  /**
   * Adds notice to the container and updates {@link #parseErrorsInRow} if the notice is an error.
   *
   * @param notice
   */
  private void addNoticeInRow(ValidationNotice notice) {
    if (isError(notice)) {
      parseErrorsInRow = true;
    }
    noticeContainer.addValidationNotice(notice);
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
   * @param required whether the value is required according to GTFS
   * @param parsingFunction function that converts string to an object to return
   * @param noticingFunction function to create a notice about parse errors
   * @param <T> the type to return
   * @return a parsed object or null if the value is missing or invalid
   */
  @Nullable
  private <T> T parseAsType(
      int columnIndex,
      boolean required,
      Function<String, T> parsingFunction,
      NoticingFunction noticingFunction) {
    String s = asString(columnIndex, required);
    if (s == null) {
      return null;
    }
    try {
      return parsingFunction.apply(s);
    } catch (IllegalArgumentException | ZoneRulesException e) {
      // Most parsing functions throw an IllegalArgumentException but ZoneId.of() throws
      // a ZoneRulesException.
      addNoticeInRow(
          noticingFunction.apply(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s));
      return null;
    }
  }

  /**
   * Validates a string as URL, phone number etc. and adds notices for invalid values.
   *
   * <ul>
   *   <li>If {@code validatingFunction} returns true, then the value is considered valid and {@code
   *       asValidatedString} returns it.
   *   <li>If {@code noticingFunction} returns an error, then the value is considered invalid and
   *       {@code asValidatedString} returns null.
   *   <li>If {@code noticingFunction} returns a warning or notice, then the value is considered
   *       valid and {@code asValidatedString} returns it.
   * </ul>
   *
   * @param columnIndex index of the column to parse
   * @param required whether the value is required according to GTFS
   * @param validatingFunction the predicate to validate a given string
   * @param noticingFunction function to create a notice about parse errors
   * @return the cell value at the given column or null if the value is missing or invalid
   */
  @Nullable
  private String asValidatedString(
      int columnIndex,
      boolean required,
      Predicate<String> validatingFunction,
      NoticingFunction noticingFunction) {
    String s = asString(columnIndex, required);
    if (s == null) {
      return null;
    }
    if (!validatingFunction.test(s)) {
      ValidationNotice notice =
          noticingFunction.apply(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s);
      addNoticeInRow(notice);
      if (isError(notice)) {
        return null;
      }
    }
    return s;
  }

  /**
   * Generates a validation notice for the given value at the given position in a file.
   *
   * @param <T> the type of a returned notice
   */
  @FunctionalInterface
  private interface NoticingFunction<T extends ValidationNotice> {

    T apply(String filename, long csvRowNumber, String fieldName, String fieldValue);
  }
}
