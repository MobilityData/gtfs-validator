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

import com.google.common.collect.ImmutableMap;

/**
 * Describes a runtime exception while creating a code{@GtfsInput}.
 */
public class GtfsInputCreationError extends SystemError {

    public GtfsInputCreationError(String exceptionClassName, String message) {
        super(ImmutableMap.of("exception", exceptionClassName, "message", message));
    }

    @Override
    public String getCode() {
        return "runtime_exception_at_gtfs_input_creation";
    }
}
