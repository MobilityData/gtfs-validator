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

import java.time.ZonedDateTime;

/** Represents the current date and time */
public class CurrentDateTime {

  private final ZonedDateTime now;

  public CurrentDateTime(ZonedDateTime now) {
    this.now = now;
  }

  public ZonedDateTime getNow() {
    return now;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof CurrentDateTime) {
      return this.now.equals(((CurrentDateTime) other).now);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return now.hashCode();
  }
}
