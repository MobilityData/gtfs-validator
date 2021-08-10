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

package org.mobilitydata.gtfsvalidator.notice;

/**
 * ValidationNotice is the base class for all validation errors and warnings related to the content
 * of a GTFS feed.
 *
 * <p>This is the parent class for the most of notices, such as {@link DuplicatedColumnNotice},
 * {@link NumberOutOfRangeNotice} and notices outside of the validator core, including 3rd-party
 * notices.
 */
public abstract class ValidationNotice extends Notice {

  public ValidationNotice(SeverityLevel severityLevel) {
    super(severityLevel);
  }
}
