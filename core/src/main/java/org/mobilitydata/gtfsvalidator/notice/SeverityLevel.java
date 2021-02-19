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

package org.mobilitydata.gtfsvalidator.notice;

/** Describes the level of severity of a notice generated during validation. */
public enum SeverityLevel {
  /**
   * INFO - for items that do not affect the feed's quality, such as unknown files or unknown fields.
   */
  INFO,

  /**
   * WARNING - for items that will affect the quality of GTFS datasets but the GTFS spec does
   * expressly require or prohibit. For example, these might be items recommended using the language
   * "should" or "should not" in the GTFS spec, or items recommended in the MobilityData GTFS Best
   * Practices (https://gtfs.org/best-practices/).
   */
  WARNING,

  /** ERROR - for items that the GTFS spec (https://github.com/google/transit/tree/master/gtfs/spec/en)
   * explicitly requires or prohibits (e.g., using the language "must"). The validator uses RFC2119
   * (https://tools.ietf.org/html/rfc2119) to interpret the language in the GTFS spec. */
  ERROR
}
