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

import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableSet;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;

/** Represents the country code of a GTFS feed. */
public class CountryCode {
  // for unknown and invalid territories
  public static final String ZZ = "ZZ";
  private static final Set<String> ISO_COUNTRIES = ImmutableSet.copyOf(Locale.getISOCountries());
  // ISO 3166-1 Alpha 2 country code for the United Kingdom is "GB" but feed names may use "UK".
  private static final String UK = "UK";
  private static final String GB = "GB";
  private final String countryCode;

  private CountryCode(@Nullable String countryCode) {
    this.countryCode = countryCode;
  }

  /**
   * Returns the {@code CountryCode} associated to the cldrCode provided as parameter: special
   * country code "ZZ" is returned if the cldrCode provided is invalid or unknown
   *
   * @param cldrCode the Unicode Common Locale Data Repository code to make a {@code CountryCode}
   *     from
   * @return the {@code CountryCode} associated to the cldrCode provided as parameter: special
   *     country code "ZZ" is returned if the cldrCode provided is invalid or unknown
   */
  public static CountryCode forStringOrUnknown(String cldrCode) {
    String upperCaseCldrCode = Ascii.toUpperCase(cldrCode);
    if (upperCaseCldrCode.equals(UK)) {
      upperCaseCldrCode = GB;
    }
    if (!ISO_COUNTRIES.contains(upperCaseCldrCode)) {
      upperCaseCldrCode = ZZ;
    }
    return new CountryCode(upperCaseCldrCode);
  }

  /**
   * Returns true if this is the unknown region ("ZZ"). This value is returned by APIs when no
   * suitable region can be determined, which can indicate bad input or missing data.
   */
  public boolean isUnknown() {
    return countryCode.equals(ZZ);
  }

  /**
   * Returns the uppercase ISO 3166-1 Alpha 2 country code component, e.g., "NL" or "AU".
   *
   * <p>Note that this returns "GB" for feeds that use the non-standard "UK" country code.
   *
   * @return uppercase ISO Alpha country code
   */
  public String getCountryCode() {
    return countryCode;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof CountryCode) {
      return this.countryCode.equals(((CountryCode) other).countryCode);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return countryCode.hashCode();
  }

  @Override
  public String toString() {
    return countryCode;
  }
}
