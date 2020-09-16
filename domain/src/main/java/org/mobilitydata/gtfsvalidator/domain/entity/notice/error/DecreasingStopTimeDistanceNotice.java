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

public class DecreasingStopTimeDistanceNotice extends ErrorNotice {
    public DecreasingStopTimeDistanceNotice(final String tripId,
                                            final int previousStopSequence,
                                            final Float previousShapeDistTraveled,
                                            final int stopSequence,
                                            final Float shapeDistTraveled) {
        super("stop_times.txt", E_057,
                "Decreasing `shape_dist_traveled` values",
                String.format("`trip_id`: `%s` `stop_sequence`: `%s` has a larger `shape_dist_traveled` (`%s`) than" +
                                " `stop_sequence`: `%s` (`%s`). `shape_dist_traveled` must increase with" +
                                " `stop_sequence`.",
                        tripId, stopSequence, shapeDistTraveled, previousStopSequence,
                        previousShapeDistTraveled),
                null);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "trip_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "stop_sequence");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, stopSequence);
        putNoticeSpecific(KEY_STOP_TIME_STOP_SEQUENCE, stopSequence);
        putNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED, shapeDistTraveled);
        putNoticeSpecific(KEY_STOP_TIME_PREVIOUS_STOP_SEQUENCE, previousStopSequence);
        putNoticeSpecific(KEY_STOP_TIME_PREVIOUS_SHAPE_DIST_TRAVELED, previousShapeDistTraveled);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
