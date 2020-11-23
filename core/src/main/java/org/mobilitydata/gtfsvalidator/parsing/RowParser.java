package org.mobilitydata.gtfsvalidator.parsing;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.*;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import java.time.ZoneId;
import java.util.Currency;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Parses cells of a CSV row as values of requested data types.
 * <p>
 * Interface functions of this class receive an instance of {@code NoticeContainer}. If a cell value cannot be parsed,
 * these functions add a notice to the container, return null and don't throw an exception.
 */
public class RowParser {
    public static final boolean REQUIRED = true;
    public static final boolean OPTIONAL = false;
    private final NoticeContainer noticeContainer;
    private final GtfsFeedName feedName;
    private final ValueParser<Boolean> booleanParser = new ValueParser("boolean") {
        @Override
        Boolean parseString(String s) {
            int i = Integer.parseInt(s);
            switch (i) {
                case 0:
                    return false;
                case 1:
                    return true;
                default:
                    throw new IllegalArgumentException("Illegal boolean value" + i);
            }
        }
    };
    private final ValueParser<Integer> integerParser = new ValueParser("integer") {
        @Override
        Integer parseString(String s) {
            return Integer.parseInt(s);
        }
    };
    private final ValueParser<Double> floatParser = new ValueParser("float") {
        @Override
        Double parseString(String s) {
            return Double.parseDouble(s);
        }
    };
    private final ValueParser<TimeZone> timezoneParser = new ValueParser("timezone") {
        @Override
        TimeZone parseString(String s) {
            return TimeZone.getTimeZone(ZoneId.of(s));
        }
    };
    private final ValueParser<Locale> languageCodeParser = new ValueParser("language code") {
        // TODO: Enhance checks for IETF BCP 47 language code.
        @Override
        Locale parseString(String s) {
            return Locale.forLanguageTag(s);
        }
    };
    private final ValueParser<GtfsColor> colorParser = new ValueParser("color") {
        @Override
        GtfsColor parseString(String s) {
            return GtfsColor.fromString(s);
        }
    };
    private final ValueParser<Double> latitudeParser = new ValueParser("latitude") {
        @Override
        Double parseString(String s) {
            double d = Double.parseDouble(s);
            if (!(-90 <= d && d <= 90)) {
                throw new IllegalArgumentException("Latitude must be within [-90, 90]" + s);
            }
            return d;
        }
    };
    private final ValueParser<Double> longitudeParser = new ValueParser("longitude") {
        @Override
        Double parseString(String s) {
            double d = Double.parseDouble(s);
            if (!(-180 <= d && d <= 180)) {
                throw new IllegalArgumentException("Longitude must be within [-180, 180]" + s);
            }
            return d;
        }
    };
    private final ValueParser<Currency> currencyParser = new ValueParser("currency") {
        @Override
        Currency parseString(String s) {
            return Currency.getInstance(s);
        }
    };
    private final ValueParser<GtfsDate> dateParser = new ValueParser("date") {
        @Override
        GtfsDate parseString(String s) {
            return GtfsDate.fromString(s);
        }
    };
    private final ValueParser<GtfsTime> timeParser = new ValueParser("time") {
        @Override
        GtfsTime parseString(String s) {
            return GtfsTime.fromString(s);
        }
    };
    private final ValueParser<String> emailParser = new ValueParser("email") {
        @Override
        String parseString(String s) {
            if (!EmailValidator.getInstance().isValid(s)) {
                throw new IllegalArgumentException("Invalid email " + s);
            }
            return s;
        }
    };
    private final ValueParser<String> urlParser = new ValueParser("URL") {
        @Override
        String parseString(String s) {
            if (!UrlValidator.getInstance().isValid(s)) {
                throw new IllegalArgumentException("Invalid URL " + s);
            }
            return s;
        }
    };
    private final ValueParser<String> phoneNumberParser = new ValueParser("phone number") {
        private PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

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

    public void setRow(CsvRow row) {
        this.row = row;
        this.parseErrorsInRow = false;
    }

    public boolean hasParseErrorsInRow() {
        return parseErrorsInRow;
    }

    public void checkRowColumnCount(CsvFile csvFile) {
        if (row.getColumnCount() != csvFile.getColumnCount()) {
            noticeContainer.addNotice(new InvalidRowLengthError(csvFile.getFileName(), row.getRowNumber(),
                    row.getColumnCount(), csvFile.getColumnCount()));
            parseErrorsInRow = true;
        }
    }

    public String asString(int columnIndex, boolean required) {
        String s = row.asString(columnIndex);
        if (required && s == null) {
            noticeContainer.addNotice(new MissingRequiredFieldError(row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex)));
            parseErrorsInRow = true;
        }
        return s;
    }

    public String asText(int columnIndex, boolean required) {
        return asString(columnIndex, required);
    }

    public String asId(int columnIndex, boolean required) {
        return asString(columnIndex, required);
    }

    public String asUrl(int columnIndex, boolean required) {
        return urlParser.parseField(columnIndex, required);
    }

    public String asEmail(int columnIndex, boolean required) {
        return emailParser.parseField(columnIndex, required);
    }

    public String asPhoneNumber(int columnIndex, boolean required) {
        return phoneNumberParser.parseField(columnIndex, required);
    }

    public Locale asLanguageCode(int columnIndex, boolean required) {
        return languageCodeParser.parseField(columnIndex, required);
    }

    public TimeZone asTimezone(int columnIndex, boolean required) {
        return timezoneParser.parseField(columnIndex, required);
    }

    public Currency asCurrencyCode(int columnIndex, boolean required) {
        return currencyParser.parseField(columnIndex, required);
    }

    public Double asFloat(int columnIndex, boolean required) {
        return floatParser.parseField(columnIndex, required);
    }

    public Double asFloat(int columnIndex, boolean required, NumberBounds bounds) {
        Double value = asFloat(columnIndex, required);
        if (value != null) {
            switch (bounds) {
                case POSITIVE:
                    if (value <= 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "positive float", value));
                    }
                    break;
                case NON_NEGATIVE:
                    if (value < 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "non-negative float", value));
                    }
                    break;
                case NON_ZERO:
                    if (value == 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "non-zero float", value));
                    }
                    break;
            }
        }
        return value;
    }

    public Double asLatitude(int columnIndex, boolean required) {
        return latitudeParser.parseField(columnIndex, required);
    }

    public Double asLongitude(int columnIndex, boolean required) {
        return longitudeParser.parseField(columnIndex, required);
    }

    public Integer asInteger(int columnIndex, boolean required) {
        return integerParser.parseField(columnIndex, required);
    }

    public Integer asInteger(int columnIndex, boolean required, NumberBounds bounds) {
        Integer value = asInteger(columnIndex, required);
        if (value != null) {
            switch (bounds) {
                case POSITIVE:
                    if (value <= 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "positive integer", value));
                    }
                    break;
                case NON_NEGATIVE:
                    if (value < 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "non-negative integer", value));
                    }
                    break;
                case NON_ZERO:
                    if (value == 0) {
                        parseErrorsInRow = true;
                        noticeContainer.addNotice(new NumberOutOfBoundsError(
                                row.getFileName(), row.getRowNumber(), row.getColumnName(columnIndex),
                                "non-zero integer", value));
                    }
                    break;
            }
        }
        return value;
    }

    public GtfsColor asColor(int columnIndex, boolean required) {
        return colorParser.parseField(columnIndex, required);
    }

    public Boolean asBoolean(int columnIndex, boolean required) {
        return booleanParser.parseField(columnIndex, required);
    }

    public <E> Integer asEnum(int columnIndex, boolean required, EnumCreator<E> enumCreator) {
        String s = asString(columnIndex, required);
        if (s == null) {
            return null;
        }
        int i;
        try {
            i = Integer.parseInt(s);
        } catch (Exception ex) {
            parseErrorsInRow = true;
            noticeContainer.addNotice(new FieldParsingError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    "enum",
                    s));
            return null;
        }
        if (enumCreator.convert(i) == null) {
            parseErrorsInRow = true;
            noticeContainer.addNotice(new UnexpectedEnumValueError(
                    row.getFileName(),
                    row.getRowNumber(),
                    row.getColumnName(columnIndex),
                    i));
        }
        return i;
    }

    public GtfsTime asTime(int columnIndex, boolean required) {
        return timeParser.parseField(columnIndex, required);
    }

    public GtfsDate asDate(int columnIndex, boolean required) {
        return dateParser.parseField(columnIndex, required);
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
        private String formatName;

        public ValueParser(String formatName) {
            this.formatName = formatName;
        }

        abstract T parseString(String s);

        final T parseField(int columnIndex, boolean required) {
            String s = asString(columnIndex, required);
            if (s == null || s.isEmpty()) {
                return null;
            }
            try {
                return parseString(s);
            } catch (Exception ex) {
                parseErrorsInRow = true;
                noticeContainer.addNotice(new FieldParsingError(
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

