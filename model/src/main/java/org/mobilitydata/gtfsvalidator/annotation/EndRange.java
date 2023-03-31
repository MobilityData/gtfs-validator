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

package org.mobilitydata.gtfsvalidator.annotation;

/**
 * Specifies a field for the end point of a date or time range. A validator for having start and end
 * points in order will be generated automatically.
 *
 * <p>Validation is performed only when both start and end field are set. If any field is missing,
 * then no notice is generated. You need to put an extra {@link Required} annotation if you want to
 * make any field required.
 *
 * <p>Example.
 *
 * <pre>
 * {@literal @}GtfsTable(value = "feed_info.txt", singleRow = true)
 * public interface GtfsFeedInfoSchema extends GtfsEntity {
 *   {@literal @}EndRange(field = "feed_end_date", allowEqual = true)
 *   GtfsDate feedStartDate();
 *
 *   GtfsDate feedEndDate();
 * }
 * </pre>
 */
public @interface EndRange {

  /** Name of a GTFS field in the same file that holds the end range, e.g, {@code "end_date"}. */
  String field();

  /** Allow start and end points to be equal. */
  boolean allowEqual();
}
