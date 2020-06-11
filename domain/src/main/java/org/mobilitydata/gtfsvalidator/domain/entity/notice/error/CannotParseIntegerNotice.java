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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class CannotParseIntegerNotice extends ErrorNotice {

    public CannotParseIntegerNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, E_005,
                "Invalid integer value",
                "Value: '" + rawValue + "' of field: " + fieldName
                        + " with type integer can't be parsed in file: " + filename + " at row: " + lineNumber,
                null);
        putExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME, fieldName);
        putExtra(NOTICE_SPECIFIC_KEY__LINE_NUMBER, lineNumber);
        putExtra(NOTICE_SPECIFIC_KEY__RAW_VALUE, rawValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
