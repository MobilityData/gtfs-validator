/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class CannotParseIntegerNotice extends ErrorNotice {

    public CannotParseIntegerNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, E_005,
                "Invalid integer value",
                "Value: '" + rawValue + "' of field: " + fieldName + " with type integer can't be parsed in file: " + filename + " at row: " + lineNumber);
    }
}
