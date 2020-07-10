/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;

public class AgencyIdNotFoundNotice extends ErrorNotice {
    public AgencyIdNotFoundNotice(final String filename, final String fieldName, final String entityId) {
        super(filename,
                E_035,
                "GTFS `agency_id` does not exist in GTFS data",
                "Field: `" + fieldName + "` for entity from file : `" + filename + "` with id: `" + entityId +
                        "` does not refer to any record from `agency.txt`",
                entityId);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
    }

    @Override
    public void export(NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
