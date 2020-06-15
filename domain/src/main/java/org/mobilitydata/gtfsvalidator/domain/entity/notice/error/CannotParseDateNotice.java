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

public class CannotParseDateNotice extends ErrorNotice {

    public CannotParseDateNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, E_017,
                "Invalid date value",
                "Value: '" + rawValue + "' of field: " + fieldName +
                        " with type date can't be parsed in file: " + filename + " at row: " + lineNumber,
                null);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_LINE_NUMBER, lineNumber);
        putNoticeSpecific(KEY_RAW_VALUE, rawValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
