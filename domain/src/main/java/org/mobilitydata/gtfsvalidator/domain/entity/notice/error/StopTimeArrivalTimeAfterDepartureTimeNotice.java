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

public class StopTimeArrivalTimeAfterDepartureTimeNotice extends ErrorNotice {
    public StopTimeArrivalTimeAfterDepartureTimeNotice(final String filename,
                                                       final String arrivalTimeAsString,
                                                       final String departureTimeAsString,
                                                       final String tripId,
                                                       final Integer stopSequence) {
        super("stop_times.txt",
                E_045,
                "Fields `arrival_time` and `departure_time` out of order",
                String.format("`departure_time`: `%s` precedes `arrival_time`: `%s` in file `%s` for entity" +
                                " with composite id: (`tripId`=`%s` ; `stopSequence`=`%s`).",
                        departureTimeAsString,
                        arrivalTimeAsString,
                        filename,
                        tripId,
                        stopSequence),
                null);

        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "tripId");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "stopSequence");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, stopSequence);
        putNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME, arrivalTimeAsString);
        putNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME, departureTimeAsString);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
