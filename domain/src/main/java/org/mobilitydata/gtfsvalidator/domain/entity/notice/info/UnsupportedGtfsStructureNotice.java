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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.info;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.InfoNotice;

import java.io.IOException;

public class UnsupportedGtfsStructureNotice extends InfoNotice {
    public UnsupportedGtfsStructureNotice(final String tripId,
                                          final String otherTripId,
                                          final String serviceId,
                                          final String otherServiceId) {
        super("trips.txt",
                I_002,
                "Unsupported GTFS structure",
                String.format("The structure of this GTFS archive is not supported yet. " +
                        "Trip with tripId: `%s` and serviceId: `%s`; and" +
                        "trip with tripId: `%s` and serviceId: `%s` do not both refer to `calendar.txt` or " +
                        "`calendar_dates.txt.",
                        tripId,
                        serviceId,
                        otherTripId,
                        otherServiceId),
                null);
        putNoticeSpecific(KEY_TRIP_ID, tripId);
        putNoticeSpecific(KEY_OTHER_TRIP_ID, otherTripId);
        putNoticeSpecific(KEY_SERVICE_ID, serviceId);
        putNoticeSpecific(KEY_OTHER_SERVICE_ID, otherServiceId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
