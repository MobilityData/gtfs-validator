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

import java.util.Map;

/**
 * ValidationNotice is the base class for all validation errors and warnings related to the content
 * of a GTFS feed.
 *
 * <p>This is the parent class for the most of notices, such as {@link DuplicatedColumnNotice},
 * {@link NumberOutOfRangeError} and notices outside of the validator core, including 3rd-party
 * notices.
 */
public abstract class ValidationNotice extends Notice {
  // default constructor: might be removed later during refactor.
  public ValidationNotice(Map<String, Object> context) {
    // by default severityLevel is set to SeverityLevel.ERROR
    super(context, SeverityLevel.ERROR);
  }
  public ValidationNotice(Map<String, Object> context, SeverityLevel severityLevel) {
    super(context, severityLevel);
  }
}
