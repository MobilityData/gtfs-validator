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

package org.mobilitydata.gtfsvalidator.usecase.notice.error;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.io.IOException;

public class UnexpectedValueNotice extends ErrorNotice {
    private int enumValue;
    private String fieldName;

    public UnexpectedValueNotice(String filename, String fieldName, String entityId, int enumValue) {
        super(filename, E_018,
                "Unexpected enum value",
                "Invalid value :" + enumValue + " - for field:" + fieldName + " in file:" + filename +
                        " for entity with id:" + entityId, entityId);
        this.fieldName = fieldName;
        this.enumValue = enumValue;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }

    public String getEnumValue() {
        return String.valueOf(enumValue);
    }

    public String getFieldName() {
        return fieldName;
    }
}