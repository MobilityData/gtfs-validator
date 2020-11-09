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
import java.util.List;

public class FastTravelBetweenStopsNotice extends ErrorNotice {

    public FastTravelBetweenStopsNotice(final String tripId,
                                        final float speedKmh,
                                        final List<Integer> stopSequenceList) {
        super("stop_times.txt", E_046,
                "Fast travel between stops",
                String.format("Fast travel detected in trip: `%s` over stop sequence: `%s`." +
                                " Calculated speed: `%s` kmh.",
                        tripId,
                        stopSequenceList,
                        speedKmh),
                null);

        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "trip_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "stop_sequence_list");
        putNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE_LIST, stopSequenceList);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
