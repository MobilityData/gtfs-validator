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

public class IllegalFieldValueCombination extends ErrorNotice {

    public IllegalFieldValueCombination(final String filename, final String fieldName,
                                        final String conflictingFieldName, final String entityId) {
        super(filename, E_019,
                "Conflicting field values",
                "Conflicting field values for fields:`" + fieldName + "` and field:`" + conflictingFieldName
                        + "`" + entityId, entityId);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_CONFLICTING_FIELD_NAME, conflictingFieldName);
    }

    /**
     * Notice alternative constructor handling entities with no id
     *
     * @param filename                 the name of the file
     * @param fieldName                the name of the field
     * @param conflictingFieldName     the name of the field whose value clashes with the value contained in "fieldName"
     * @param compositeKeyFirstPart    the "title" of the first part of the composite key
     * @param compositeKeySecondPart   the "title" of the second part of the composite key
     * @param compositeKeyFirstValue   the value of the first part of the composite key
     * @param compositeKeySecondValue  the value of the second part of the composite key
     */
    public IllegalFieldValueCombination(final String filename, final String fieldName,
                                        final String conflictingFieldName, final String compositeKeyFirstPart,
                                        final String compositeKeySecondPart, final Object compositeKeyFirstValue,
                                        final Object compositeKeySecondValue) {
        super(filename, E_019,
                "Conflicting field values",
                "Conflicting field values for fields:`" + fieldName + "` and field:`" + conflictingFieldName
                        + "` for entity with composite id: `" + compositeKeyFirstPart + "`: `" + compositeKeyFirstValue
                        + "`" + "--" + compositeKeySecondPart + "`: `" + compositeKeySecondValue + "`.", null);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_CONFLICTING_FIELD_NAME, conflictingFieldName);
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