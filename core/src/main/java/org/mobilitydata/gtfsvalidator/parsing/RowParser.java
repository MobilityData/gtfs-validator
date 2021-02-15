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
import java.util.Currency;
import java.util.Locale;
import javax.annotation.Nullable;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.EmptyRowNotice;
import org.mobilitydata.gtfsvalidator.notice.FieldParsingError;
import org.mobilitydata.gtfsvalidator.notice.InvalidRowLengthError;
import org.mobilitydata.gtfsvalidator.notice.LeadingOrTrailingWhitespacesNotice;
import org.mobilitydata.gtfsvalidator.notice.MissingRequiredFieldError;
import org.mobilitydata.gtfsvalidator.notice.NewLineInValueNotice;
import org.mobilitydata.gtfsvalidator.notice.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.NumberOutOfRangeError;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.UnexpectedEnumValueError;
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
  private final GtfsFeedName feedName;
  private final ValueParser<Integer> integerParser =
      new ValueParser("integer") {
        @Override
        Integer parseString(String s) {
          return Integer.parseInt(s);
        }
      };
  private final ValueParser<Double> floatParser =
      new ValueParser("float") {
        @Override
        Double parseString(String s) {
          return Double.parseDouble(s);
        }
      };
  private final ValueParser<BigDecimal> decimalParser =
      new ValueParser("decimal") {
        @Override
        BigDecimal parseString(String s) {
          return new BigDecimal(s);
        }
      };
  private final ValueParser<ZoneId> timezoneParser =
      new ValueParser("timezone") {
        @Override
        ZoneId parseString(String s) {
          return ZoneId.of(s);
        }
      };
  private final ValueParser<Locale> languageCodeParser =
      new ValueParser("language code") {
        // FIXME: Enhance checks for IETF BCP 47 language code.
        @Override
        Locale parseString(String s) {
          return Locale.forLanguageTag(s);
        }
      };
  private final ValueParser<GtfsColor> colorParser =
      new ValueParser("color") {
        @Override
        GtfsColor parseString(String s) {
          return GtfsColor.fromString(s);
        }
      };
  private final ValueParser<Double> latitudeParser =
      new ValueParser("latitude") {
        @Override
        Double parseString(String s) {
          double d = Double.parseDouble(s);
          if (!(-90 <= d && d <= 90)) {
            throw new IllegalArgumentException("Latitude must be within [-90, 90]" + s);
          }
          return d;
        }
      };
  private final ValueParser<Double> longitudeParser =
      new ValueParser("longitude") {
        @Override
        Double parseString(String s) {
          double d = Double.parseDouble(s);
          if (!(-180 <= d && d <= 180)) {
            throw new IllegalArgumentException("Longitude must be within [-180, 180]" + s);
          }
          return d;
        }
      };
  private final ValueParser<Currency> currencyParser =
      new ValueParser("currency") {
        @Override
        Currency parseString(String s) {
          return Currency.getInstance(s);
        }
      };
  private final ValueParser<GtfsDate> dateParser =
      new ValueParser("date") {
        @Override
        GtfsDate parseString(String s) {
          return GtfsDate.fromString(s);
        }
      };
  private final ValueParser<GtfsTime> timeParser =
      new ValueParser("time") {
        @Override
        GtfsTime parseString(String s) {
          return GtfsTime.fromString(s);
        }
      };
  private final ValueParser<String> emailParser =
      new ValueParser("email") {
        @Override
        String parseString(String s) {
          if (!EmailValidator.getInstance().isValid(s)) {
            throw new IllegalArgumentException("Invalid email " + s);
          }
          return s;
        }
      };
  private final ValueParser<String> urlParser =
      new ValueParser("URL") {
        @Override
        String parseString(String s) {
          if (!UrlValidator.getInstance().isValid(s)) {
            throw new IllegalArgumentException("Invalid URL " + s);
          }
          return s;
        }
      };
  private final ValueParser<String> phoneNumberParser =
      new ValueParser("phone number") {
        private final PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        @Override
        String parseString(String s) {
          if (!phoneUtil.isPossibleNumber(s, feedName.getISOAlpha2CountryCode())) {
            throw new IllegalArgumentException("Invalid phone number " + s);
          }
          return s;
        }
      };
  private CsvRow row;
  private boolean parseErrorsInRow;

  public RowParser(GtfsFeedName feedName, NoticeContainer noticeContainer) {
    this.feedName = feedName;
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
   * This function may add notices to {@code noticeContainer}.
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
          new InvalidRowLengthError(
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
          new MissingRequiredFieldError(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex)));
    }
    if (s != null) {
      if (s.indexOf('\n') != -1 || s.indexOf('\r') != -1) {
        addNoticeInRow(
            new NewLineInValueNotice(
                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s));
      }
      final String stripped = s.strip();
      if (stripped.length() < s.length()) {
        addNoticeInRow(
            new LeadingOrTrailingWhitespacesNotice(
                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), s));
        s = stripped;
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
    String value = asString(columnIndex, required);
    if (value != null && !hasOnlyPrintableAscii(value)) {
      addNoticeInRow(
          new NonAsciiOrNonPrintableCharNotice(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex)));
    }
    return value;
  }

  @Nullable
  public String asUrl(int columnIndex, boolean required) {
    return urlParser.parseField(columnIndex, required);
  }

  @Nullable
  public String asEmail(int columnIndex, boolean required) {
    return emailParser.parseField(columnIndex, required);
  }

  @Nullable
  public String asPhoneNumber(int columnIndex, boolean required) {
    return phoneNumberParser.parseField(columnIndex, required);
  }

  @Nullable
  public Locale asLanguageCode(int columnIndex, boolean required) {
    return languageCodeParser.parseField(columnIndex, required);
  }

  @Nullable
  public ZoneId asTimezone(int columnIndex, boolean required) {
    return timezoneParser.parseField(columnIndex, required);
  }

  @Nullable
  public Currency asCurrencyCode(int columnIndex, boolean required) {
    return currencyParser.parseField(columnIndex, required);
  }

  @Nullable
  public Double asFloat(int columnIndex, boolean required) {
    return floatParser.parseField(columnIndex, required);
  }

  @Nullable
  public Double asFloat(int columnIndex, boolean required, NumberBounds bounds) {
    Double value = asFloat(columnIndex, required);
    if (value != null) {
      switch (bounds) {
        case POSITIVE:
          if (value <= 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "positive float",
                    value));
          }
          break;
        case NON_NEGATIVE:
          if (value < 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-negative float",
                    value));
          }
          break;
        case NON_ZERO:
          if (value == 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-zero float",
                    value));
          }
          break;
      }
    }
    return value;
  }

  @Nullable
  public Double asLatitude(int columnIndex, boolean required) {
    return latitudeParser.parseField(columnIndex, required);
  }

  @Nullable
  public Double asLongitude(int columnIndex, boolean required) {
    return longitudeParser.parseField(columnIndex, required);
  }

  @Nullable
  public Integer asInteger(int columnIndex, boolean required) {
    return integerParser.parseField(columnIndex, required);
  }

  @Nullable
  public Integer asInteger(int columnIndex, boolean required, NumberBounds bounds) {
    Integer value = asInteger(columnIndex, required);
    if (value != null) {
      switch (bounds) {
        case POSITIVE:
          if (value <= 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "positive integer",
                    value));
          }
          break;
        case NON_NEGATIVE:
          if (value < 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-negative integer",
                    value));
          }
          break;
        case NON_ZERO:
          if (value == 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-zero integer",
                    value));
          }
          break;
      }
    }
    return value;
  }

  @Nullable
  public BigDecimal asDecimal(int columnIndex, boolean required) {
    return decimalParser.parseField(columnIndex, required);
  }

  @Nullable
  public BigDecimal asDecimal(int columnIndex, boolean required, NumberBounds bounds) {
    BigDecimal value = asDecimal(columnIndex, required);
    if (value != null) {
      final int compareToZero = value.compareTo(new BigDecimal(0));
      switch (bounds) {
        case POSITIVE:
          if (compareToZero <= 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "positive decimal",
                    value));
          }
          break;
        case NON_NEGATIVE:
          if (compareToZero < 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-negative decimal",
                    value));
          }
          break;
        case NON_ZERO:
          if (compareToZero == 0) {
            addNoticeInRow(
                new NumberOutOfRangeError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "non-zero decimal",
                    value));
          }
          break;
      }
    }
    return value;
  }

  @Nullable
  public GtfsColor asColor(int columnIndex, boolean required) {
    return colorParser.parseField(columnIndex, required);
  }

  @Nullable
  public <E> Integer asEnum(int columnIndex, boolean required, EnumCreator<E> enumCreator) {
    String s = asString(columnIndex, required);
    if (s == null) {
      return null;
    }
    int i;
    try {
      i = Integer.parseInt(s);
    } catch (Exception ex) {
      addNoticeInRow(
          new FieldParsingError(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), "enum", s));
      return null;
    }
    if (enumCreator.convert(i) == null) {
      addNoticeInRow(
          new UnexpectedEnumValueError(
              row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex), i));
    }
    return i;
  }

  @Nullable
  public GtfsTime asTime(int columnIndex, boolean required) {
    return timeParser.parseField(columnIndex, required);
  }

  @Nullable
  public GtfsDate asDate(int columnIndex, boolean required) {
    return dateParser.parseField(columnIndex, required);
  }

  private void addNoticeInRow(ValidationNotice notice) {
    if (notice.getSeverityLevel().ordinal() >= SeverityLevel.ERROR.ordinal()) {
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

  abstract class ValueParser<T> {

    private final String formatName;

    public ValueParser(String formatName) {
      this.formatName = formatName;
    }

    abstract T parseString(String s);

    final T parseField(int columnIndex, boolean required) {
      String s = asString(columnIndex, required);
      if (s == null) {
        return null;
      }
      try {
        return parseString(s);
      } catch (Exception ex) {
        addNoticeInRow(
            new FieldParsingError(
                row.getFileName(),
                row.getRowNumber(),
                row.getColumnName(columnIndex),
                formatName,
                s));
        return null;
      }
    }
  }
}
