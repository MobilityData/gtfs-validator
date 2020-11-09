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

public class BackwardsTimeTravelInStopNotice extends ErrorNotice {
    public BackwardsTimeTravelInStopNotice(final String tripId,
                                           final Integer stopSequence,
                                           final String arrivalTimeAsString,
                                           final String previousStopDepartureTimeAsString,
                                           final Integer previousStopTimeStopSequence) {
        super("stop_times.txt", E_047,
                "Bad stoptime time combination",
                String.format("The `arrival_time`: `%s` (`stop_sequence`: `%s`) occurs before `departure_time`:" +
                                "`%s` (`stop_sequence`: `%s`) in `trip_id`: `%s`",
                        arrivalTimeAsString,
                        stopSequence,
                        previousStopDepartureTimeAsString,
                        previousStopTimeStopSequence,
                        tripId),
                null);

        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, KEY_STOP_TIME_TRIP_ID);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, KEY_STOP_TIME_STOP_SEQUENCE);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, stopSequence);
        putNoticeSpecific(KEY_STOP_TIME_ARRIVAL_TIME, arrivalTimeAsString);
        putNoticeSpecific(KEY_STOP_TIME_DEPARTURE_TIME, previousStopDepartureTimeAsString);
        putNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE, previousStopTimeStopSequence);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
