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

/** Describes a pair of latitude and longitude fields. */
@AutoValue
public abstract class LatLonDescriptor {

  public static LatLonDescriptor create(String latField, String lonField, String latLonField) {
    return new AutoValue_LatLonDescriptor(latField, lonField, latLonField);
  }

  /** Latitude field name in lowerCamelCase, e.g. {@code "stopLat"}. */
  public abstract String latField();

  /** Longitude field name in lowerCamelCase, e.g. {@code "stopLon"}. */
  public abstract String lonField();

  /** Combined lat-lon getter name in lowerCamelCase, e.g. {@code "stopLatLon"}. */
  public abstract String latLonField();
}
