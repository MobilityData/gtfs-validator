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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;

/**
 * A read-only context passed to particular validator objects. It gives information relevant for
 * validation: properties of the feed as a whole, system properties (current time) etc.
 */
@AutoValue
public abstract class ValidationContext {
  public static Builder builder() {
    return new AutoValue_ValidationContext.Builder();
  }

  /**
   * Represents a name of a GTFS feed, such as "nl-openov".
   *
   * @return the @code{GtfsFeedName} representing the feed's name
   */
  public abstract CountryCode countryCode();

  /**
   * The time when validation started.
   *
   * <p>Validator code should use this property instead of calling LocalDate.now() etc. for the
   * following reasons:
   *
   * <ul>
   *   <li>current date and time is changing but it should not randomly affect validation notices;
   *   <li>unit tests may need to override the current time.
   * </ul>
   *
   * @return The time when validation started as {@code CurrentDateTime}
   */
  public abstract CurrentDateTime currentDateTime();

  public <T> T get(Class clazz) {
    if (CountryCode.class.isAssignableFrom(clazz)) {
      return (T) countryCode();
    }
    if (CurrentDateTime.class.isAssignableFrom(clazz)) {
      return (T) currentDateTime();
    }
    throw new IllegalArgumentException(
        "Cannot find " + clazz.getCanonicalName() + " in validation context");
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setCountryCode(CountryCode countryCode);

    public abstract Builder setCurrentDateTime(CurrentDateTime currentDateTime);

    public abstract ValidationContext build();
  }
}
