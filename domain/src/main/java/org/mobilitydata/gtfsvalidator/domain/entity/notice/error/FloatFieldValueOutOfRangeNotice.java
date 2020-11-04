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

public class FloatFieldValueOutOfRangeNotice extends ErrorNotice {

    public FloatFieldValueOutOfRangeNotice(
            final String filename,
            final String fieldName,
            final String entityId,
            final float rangeMin,
            final float rangeMax,
            final float actualValue) {
        super(filename, E_011,
                "Out of range float value",
                String.format("Invalid value for field: `%s` of entity with id: `%s`" +
                                " -- min: `%s` max: `%s` actual: `%s`",
                        fieldName,
                        entityId,
                        rangeMin,
                        rangeMax,
                        actualValue),
                entityId);
        putNoticeSpecific(KEY_RANGE_MIN, rangeMin);
        putNoticeSpecific(KEY_RANGE_MAX, rangeMax);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_ACTUAL_VALUE, actualValue);
    }

    /**
     * Notice alternative constructor handling entities with no id
     *
     * @param filename                the name of the file
     * @param fieldName               the name of the field
     * @param rangeMin                the lower bound for thew value contained in field with field name "fieldName"
     * @param rangeMax                the upper bound for thew value contained in field with field name "fieldName"
     * @param actualValue             the provided value in field "fieldName"
     * @param compositeKeyFirstPart   the "title" of the first part of the composite key
     * @param compositeKeySecondPart  the "title" of the second part of the composite key
     * @param compositeKeyFirstValue  the value of the first part of the composite key
     * @param compositeKeySecondValue the value of the second part of the composite key
     */
    public FloatFieldValueOutOfRangeNotice(
            final String filename,
            final String fieldName,
            final float rangeMin,
            final float rangeMax,
            final float actualValue,
            final String compositeKeyFirstPart,
            final String compositeKeySecondPart,
            final Object compositeKeyFirstValue,
            final Object compositeKeySecondValue) {
        super(filename, E_011,
                "Out of range float value",
                String.format("Invalid value for field: `%s` of entity with composite id: \n" +
                                "`%s`: `%s \n" +
                                "`%s`: `%s \n." +
                                "min: `%s` max: `%s` actual: `%s`",
                        fieldName,
                        compositeKeyFirstPart,
                        compositeKeyFirstValue,
                        compositeKeySecondPart,
                        compositeKeySecondValue,
                        rangeMin,
                        rangeMax,
                        actualValue),
                null);
        putNoticeSpecific(KEY_RANGE_MIN, rangeMin);
        putNoticeSpecific(KEY_RANGE_MAX, rangeMax);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
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
