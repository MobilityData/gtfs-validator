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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class NonAsciiOrNonPrintableCharNotice extends WarningNotice {

    public NonAsciiOrNonPrintableCharNotice(String filename, String fieldName, String entityId, String idValue) {
        super(filename, W_003,
                "Suspicious id",
                "Non ascii or non printable character(s) in:`" + idValue + "` in field:`"
                        + fieldName + "` for entity with id:`" + entityId + "`",
                entityId);
        putNoticeSpecific(NOTICE_SPECIFIC_KEY__FIELD_NAME, fieldName);
    }

    @Override
    public void export(final NoticeExporter exporter)
            throws IOException {
        exporter.export(this);
    }
}