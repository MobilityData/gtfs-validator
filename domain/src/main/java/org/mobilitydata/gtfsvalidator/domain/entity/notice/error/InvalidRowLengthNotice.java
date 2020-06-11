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

public class InvalidRowLengthNotice extends ErrorNotice {

    public InvalidRowLengthNotice(String filename, int rowIndex, int expectedLength, int actualLength) {
        super(filename, E_004,
                "Invalid row length",
                "Invalid length for row:" + rowIndex +
                        " -- expected:" + expectedLength + " actual:" + actualLength,
                null);
        putExtra(NOTICE_SPECIFIC_KEY__ROW_INDEX, rowIndex);
        putExtra(NOTICE_SPECIFIC_KEY__EXPECTED_LENGTH, expectedLength);
        putExtra(NOTICE_SPECIFIC_KEY__ACTUAL_LENGTH, actualLength);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
