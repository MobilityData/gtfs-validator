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

package org.mobilitydata.gtfsvalidator.validator;

import com.google.auto.value.AutoValue;

/** Context describing a cell in a GTFS file. */
@AutoValue
public abstract class GtfsCellContext {
  /** Name of a GTFS file. */
  public abstract String filename();

  /** Number of a row in the GTFS file. */
  public abstract long csvRowNumber();

  /** Name of a field in the GTFS file. */
  public abstract String fieldName();

  /** Creates a context that describes a cell in a GTFS file. */
  public static GtfsCellContext create(String filename, long csvRowNumber, String fieldName) {
    return new AutoValue_GtfsCellContext(filename, csvRowNumber, fieldName);
  }
}
