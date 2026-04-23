/*
 * Copyright 2021 MobilityData IO
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

import java.time.LocalDate;

/** The date to be used for validation rules. */
public class DateForValidation {

  private final LocalDate date;

  public DateForValidation(LocalDate date) {
    this.date = date;
  }

  public LocalDate getDate() {
    return date;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof DateForValidation) {
      return this.date.equals(((DateForValidation) other).date);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return date.hashCode();
  }
}
