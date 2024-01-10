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

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;

/**
 * A read-only context passed to particular validator objects. It gives information relevant for
 * validation: properties of the feed as a whole, system properties (current time) etc.
 */
public class ValidationContext {
  public static Builder builder() {
    return new Builder();
  }

  private final ImmutableMap<Class<?>, Object> context;

  private ValidationContext(ImmutableMap<Class<?>, Object> context) {
    this.context = context;
  }

  /**
   * Represents the country code of a GTFS feed, such as US or NL.
   *
   * @return the @code{CountryCode} representing the feed's country code
   */
  public CountryCode countryCode() {
    return get(CountryCode.class);
  }

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
   * @return The time when validation started as @code{ZonedDateTime}
   */
  public DateForValidation dateForValidation() {
    return get(DateForValidation.class);
  }

  /** Returns a member of the context with requested class. */
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> clazz) {
    Object o = context.get(clazz);
    if (o == null) {
      throw new IllegalArgumentException(
          "Cannot find " + clazz.getCanonicalName() + " in validation context");
    }
    return (T) o;
  }

  /** Builder for {@link ValidationContext}. */
  public static class Builder {
    private final ImmutableMap.Builder<Class<?>, Object> context = ImmutableMap.builder();

    /** Sets the country code. */
    public Builder setCountryCode(CountryCode countryCode) {
      return set(CountryCode.class, countryCode);
    }

    /** Sets the current time. */
    public Builder setDateForValidation(DateForValidation dateForValidation) {
      return set(DateForValidation.class, dateForValidation);
    }

    /** Sets a member of the context with requested class. */
    public <T> Builder set(Class<T> clazz, T obj) {
      context.put(clazz, obj);
      return this;
    }

    public ValidationContext build() {
      return new ValidationContext(context.buildOrThrow());
    }
  }
}
