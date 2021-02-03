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
   * INFO - is used for things that do not affect the feed's quality, such as unknown files or
   * unknown fields
   */
  INFO,

  /**
   * WARNING - is used for things that affects the feed's quality (e.g., insufficient color
   * contrast) but the feed remains functional
   */
  WARNING,

  /** ERROR - is used when the feed cannot function (e.g., broken foreign key reference) */
  ERROR
}
