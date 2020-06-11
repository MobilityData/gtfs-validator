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

public class IntegerFieldValueOutOfRangeNotice extends ErrorNotice {
    public IntegerFieldValueOutOfRangeNotice(
            String filename,
            String fieldName,
            String entityId,
            int rangeMin,
            int rangeMax,
            int actualValue) {
        super(filename, E_010,
                "Out of range integer value",
                "Invalid value for field:" + fieldName + " of entity with id:" + entityId +
                        " -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue,
                entityId);
        putExtra(NOTICE_SPECIFIC_KEY__FIELD_NAME, fieldName);
        putExtra(NOTICE_SPECIFIC_KEY__RANGE_MAX, rangeMax);
        putExtra(NOTICE_SPECIFIC_KEY__RANGE_MIN, rangeMin);
        putExtra(NOTICE_SPECIFIC_KEY__ACTUAL_VALUE, actualValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}