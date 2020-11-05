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

public class RouteIdNotFoundNotice extends ErrorNotice {
    public RouteIdNotFoundNotice(final String filename, final String entityId, final String routeId,
                                 final String fieldName) {
        super(filename,
                E_033,
                "Value of field `service_id` should exist in GTFS `routes.txt`",
                String.format("Trip with `trip_id`: `%s` refers to non-existing route from file `routes.txt` " +
                        "with `route_id`: `%s`.",
                        entityId,
                        routeId),
                entityId);
        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_UNKNOWN_ROUTE_ID, routeId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
