package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Type of a field in a GTFS table.
 * <p>
 * See {@code @FieldType} annotation for examples how to specify a type.
 * <p>
 * This enum corresponds to the list of types in the standard
 * (https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#field-types) with one exception:
 * non-negative, positive and non-zero floats and integers are FLOAT and INTEGER with an extra {@code @NonNegative},
 * {@code @Positive} or {@code @NonZero} annotation.
 * <p>
 * Many GTFS types are deducted from the actual Java types in schema definition:
 * <p>
 * * {@code int} - {@code INTEGER};
 * * {@code double} - {@code FLOAT};
 * * {@code String} - {@code TEXT};
 * * {@code GtfsColor} - {@code COLOR};
 * * {@code GtfsDate} - {@code DATE};
 * * {@code GtfsTime} - {@code TIME};
 * * {@code TimeZone} - {@code TIMEZONE};
 * * {@code Locale} - {@code LANGUAGE_CODE};
 * * {@code Currency} - {@code CURRENCY_CODE}
 * * {@code BigDecimal} - {@code DECIMAL}.
 * <p>
 * However, if you need {@code EMAIL} instead of {@code TEXT}, {@code LATITUDE} instead of {@code FLOAT} etc,
 * then you need to specify a {@code FieldTypeEnum} using {@code @FieldType} annotation.
 */
public enum FieldTypeEnum {
    INTEGER,
    FLOAT,
    DECIMAL,
    TEXT,
    ID,
    COLOR,
    CURRENCY_CODE,
    DATE,
    EMAIL,
    ENUM,
    LANGUAGE_CODE,
    LATITUDE,
    LONGITUDE,
    PHONE_NUMBER,
    TIME,
    TIMEZONE,
    URL,
}
