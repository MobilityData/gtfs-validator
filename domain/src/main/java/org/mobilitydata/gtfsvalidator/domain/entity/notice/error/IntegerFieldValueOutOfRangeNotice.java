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
    public IntegerFieldValueOutOfRangeNotice(final String filename, final String fieldName, final String entityId,
                                             final int rangeMin, final int rangeMax, final int actualValue) {
        super(filename, E_010,
                "Out of range integer value",
                "Invalid value for field:`" + fieldName + "` of entity with id:`" + entityId +
                        "` -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue,
                entityId);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_RANGE_MAX, rangeMax);
        putNoticeSpecific(KEY_RANGE_MIN, rangeMin);
        putNoticeSpecific(KEY_ACTUAL_VALUE, actualValue);
    }

    /**
     * Notice alternative constructor handling entities with no id, but a composite key made of 2 elements
     *
     * @param filename                the name of the file
     * @param fieldName               the name of the field whose value is missing
     * @param rangeMin                the minimum range value for an integer
     * @param rangeMax                the maximum range value for an integer
     * @param actualValue             the actual integer value
     * @param compositeKeyFirstPart   the "title" of the first part of the composite key
     * @param compositeKeySecondPart  the "title" of the second part of the composite key
     * @param compositeKeyFirstValue  the value of the first part of the composite key
     * @param compositeKeySecondValue the value of the second part of the composite key
     */
    public IntegerFieldValueOutOfRangeNotice(final String filename, final String fieldName, final int rangeMin,
                                             final int rangeMax, final int actualValue,
                                             final String compositeKeyFirstPart, final String compositeKeySecondPart,
                                             final Object compositeKeyFirstValue, final Object compositeKeySecondValue) {
        super(filename, E_010,
                "Out of range integer value",
                "Invalid value for field:`" + fieldName
                        + "` marked as required in entity with composite id: " +
                        "`" + compositeKeyFirstPart + "`: `" + compositeKeyFirstValue + "`" + "--" +
                        "`" + compositeKeySecondPart + "`: `" + compositeKeySecondValue +
                        "` -- min:" + rangeMin + " max:" + rangeMax + " actual:" + actualValue,
                null);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_RANGE_MAX, rangeMax);
        putNoticeSpecific(KEY_RANGE_MIN, rangeMin);
        putNoticeSpecific(KEY_ACTUAL_VALUE, actualValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, compositeKeyFirstPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, compositeKeySecondPart);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, compositeKeyFirstValue);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, compositeKeySecondValue);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}