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

public class MissingAgencyIdNotice extends ErrorNotice {

    public MissingAgencyIdNotice(final String filename, final String entityId) {
        super(filename, E_029,
                "Missing `agency_id` value",
                "File `agency.txt` counts more than one record. Missing value for field: `agency_id` in " +
                        "GTFS file: `" + filename + "`, for entity with id: `" + entityId + "`.", entityId);
    }

    /**
     * Additional constructor to be used in use case "ValidateAgencyIdRequirement"
     * @param agencyName  `agency_name` of the record missing value for field `agency_id`
     */
    public MissingAgencyIdNotice(final String agencyName) {
        super("agency.txt", E_029,
                "Missing `agency_id` value",
                "File `agency.txt` counts more than one record. Missing value for field: `agency_id` in " +
                        "GTFS file: `agency.txt`, for entity with `agency_name`: `" + agencyName + "`.", null);
        putNoticeSpecific(KEY_AGENCY_NAME, agencyName);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
