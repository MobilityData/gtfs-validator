/*
 * Copyright 2021 Google LLC
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

package org.mobilitydata.gtfsvalidator.processor;

import com.google.auto.value.AutoValue;

/** Describes and end-range relation to another field. */
@AutoValue
public abstract class EndRangeDescriptor {

  public static EndRangeDescriptor create(String field, boolean allowEqual) {
    return new AutoValue_EndRangeDescriptor(field, allowEqual);
  }

  /** Name of a GTFS field that holds the end range, in lowerCamelCase, e.g, {@code "endDate"}. */
  public abstract String field();

  /** Allow start and end points to be equal. */
  public abstract boolean allowEqual();
}
