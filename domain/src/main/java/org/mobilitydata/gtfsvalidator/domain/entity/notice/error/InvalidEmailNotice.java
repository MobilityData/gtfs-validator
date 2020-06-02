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

public class InvalidEmailNotice extends ErrorNotice {
    private String emailValue;
    private String fieldName;

    public InvalidEmailNotice(final String filename, final String fieldName,
                              final String entityId, final String emailValue) {
        super(filename, E_023,
                "Invalid email",
                "Invalid email:" + emailValue + " in field:" + fieldName + " for entity with id:" + entityId,
                entityId);
        this.fieldName = fieldName;
        this.emailValue = emailValue;
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }

    public String getEmailValue() {
        return emailValue;
    }

    public String getFieldName() {
        return fieldName;
    }
}
