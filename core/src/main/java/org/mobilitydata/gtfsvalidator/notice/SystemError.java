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
 * SystemError is the base class for all notices related to retrieving and processing a GTFS feed.
 * The system errors are not related to the content of the feed itself.
 *
 * <p>System errors include internal errors in the validator (e.g., runtime exceptions) and failure
 * to download or to store a feed.
 *
 * <p>Users should not normally inherit from this class. All validators written by users should
 * generate subclasses of {@link ValidationNotice}.
 *
 * <p>A validation-as-a-service solution should not share system errors with external clients
 * because that may leak internal information. Only validation notices should be shared.
 */
public abstract class SystemError extends Notice {
  public SystemError(Map<String, Object> context) {
    // by default SystemError.severity is set to SeverityLevel.ERROR
    super(context, SeverityLevel.ERROR);
  }
}
