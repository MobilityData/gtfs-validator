/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Type of a field in a GTFS table.
 *
 * <p>See {@code @FieldType} annotation for examples how to specify a type.
 *
 * <p>This enum corresponds to the list of types in the standard
 * (https://github.com/google/transit/blob/master/gtfs/spec/en/reference.md#field-types) with one
 * exception: non-negative, positive and non-zero floats and integers are FLOAT and INTEGER with an
 * extra {@code @NonNegative}, {@code @Positive} or {@code @NonZero} annotation.
 *
 * <p>Many GTFS types are deducted from the actual Java types in schema definition:
 *
 * <ul>
 *   <li>{@code int} - {@code INTEGER};
 *   <li>{@code double} - {@code FLOAT};
 *   <li>{@code String} - {@code TEXT};
 *   <li>{@code GtfsColor} - {@code COLOR};
 *   <li>{@code GtfsDate} - {@code DATE};
 *   <li>{@code GtfsTime} - {@code TIME};
 *   <li>{@code ZoneId} - {@code TIMEZONE};
 *   <li>{@code Locale} - {@code LANGUAGE_CODE};
 *   <li>{@code Currency} - {@code CURRENCY_CODE}
 *   <li>{@code BigDecimal} - {@code DECIMAL}.
 * </ul>
 *
 * However, if you need {@code EMAIL} instead of {@code TEXT}, {@code LATITUDE} instead of {@code
 * FLOAT} etc, then you need to specify a {@code FieldTypeEnum} using {@code @FieldType} annotation.
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
