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

public class UnexpectedEnumValueNotice extends ErrorNotice {
    private final Integer enumValue;
    private final String fieldName;

    public UnexpectedEnumValueNotice(final String filename, final String fieldName, final String entityId,
                                     final Integer enumValue) {
        super(filename, E_021,
                "Unexpected enum value",
                "Invalid value :" + enumValue + " - for field:" + fieldName + " in file:" + filename +
                        " for entity with id:" + entityId, entityId);
        this.fieldName = fieldName;
        this.enumValue = enumValue;
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }

    public String getEnumValue() {
        return String.valueOf(enumValue);
    }

    public String getFieldName() {
        return fieldName;
    }
}