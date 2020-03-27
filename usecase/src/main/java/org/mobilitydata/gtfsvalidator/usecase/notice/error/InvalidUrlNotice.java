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

public class InvalidUrlNotice extends ErrorNotice {
    private String urlValue;
    private String fieldName;

    public InvalidUrlNotice(String filename, String fieldName, String entityId, String urlValue) {
        super(filename, E_012,
                "Invalid url",
                "Invalid url:" + urlValue + " in field:" + fieldName + " for entity with id:" + entityId,
                entityId);
        this.fieldName = fieldName;
        this.urlValue = urlValue;
    }

    @Override
    public void export(ValidationResultRepository.NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }

    public String getUrlValue() {
        return urlValue;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getFieldName() {
        return fieldName;
    }
}