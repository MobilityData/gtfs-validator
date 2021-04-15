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

package org.mobilitydata.gtfsvalidator.input;

import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/** Represents the country code of a GTFS feed. */
public class CountryCode {
  private static final Set<String> ISO_COUNTRIES = ImmutableSet.copyOf(Locale.getISOCountries());
  private static final String INVALID_COUNTRY_CODE_MESSAGE =
      "Country code must follow format `cc`, where cc is an ISO Alpha 2 country code";
  // ISO 3166-1 Alpha 2 country code for the United Kingdom is "GB" but feed names may use "UK".
  private static final String UK = "UK";
  private static final String GB = "GB";
  private final Optional<String> optionalCountryCode;

  private CountryCode(String countryCode) {
    this.optionalCountryCode = Optional.ofNullable(countryCode);
  }

  /**
   * Checks that the given string is a valid ISO Alpha 2 country code (case insensitive).
   *
   * <p>We support "UK" as an additional country code equivalent to the ISO 3166-1 Alpha 2 code
   * "GB".
   *
   * @param s the given string
   * @return true if the given string is a valid ISO Alpha 2 country code (case insensitive)
   */
  public static boolean isValidISOAlpha2(String s) {
    if (s.length() != 2) {
      return false;
    }
    s = s.toUpperCase();
    return s.equals(UK) || ISO_COUNTRIES.contains(s);
  }

  /**
   * Parses a string as a country code name.
   *
   * @param countryCode country code for a GTFS dataset
   * @return a valid GtfsCountryCode object
   * @throws IllegalArgumentException if illegal @param feedName
   */
  public static CountryCode parseString(String countryCode) throws IllegalArgumentException {
    if (countryCode == null) {
      return new CountryCode(null);
    }
    if (isValidISOAlpha2(countryCode)) {
      return new CountryCode(countryCode);
    }
    throw new IllegalArgumentException(INVALID_COUNTRY_CODE_MESSAGE);
  }

  public Optional<String> getCountryCode() {
    return optionalCountryCode;
  }

  /**
   * Returns the uppercase ISO 3166-1 Alpha 2 country code component, e.g., "NL" or "AU".
   *
   * <p>Note that this returns "GB" for feeds that use the non-standard "UK" country code.
   *
   * @return uppercase ISO Alpha country code
   */
  public String getISOAlpha2CountryCode() {
    String countryCode = optionalCountryCode.get().toUpperCase();
    return countryCode.equals(UK) ? GB : countryCode;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof CountryCode) {
      return this.optionalCountryCode.equals(((CountryCode) other).optionalCountryCode);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return optionalCountryCode.hashCode();
  }
}
