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

public class IncoherentValuesForFields extends ErrorNotice {
    private String fieldName;
    private String conflictingFieldName;

    public IncoherentValuesForFields(String filename, String fieldName, String conflictingFieldName, String entityId) {
        super(filename, E_013,
                "Conflicting field values",
                "Conflicting field values for fields:" + fieldName + " and field:" + conflictingFieldName
                        + " for entity with id:" + entityId,
                entityId);
        this.fieldName = fieldName;
        this.conflictingFieldName = conflictingFieldName;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }

    public String getEntityId() {
        return entityId;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getConflictingFieldName() {
        return conflictingFieldName;
    }
}