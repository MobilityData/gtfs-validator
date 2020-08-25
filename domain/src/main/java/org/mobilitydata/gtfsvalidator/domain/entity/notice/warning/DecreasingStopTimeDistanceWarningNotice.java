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

package org.mobilitydata.gtfsvalidator.domain.entity.notice.warning;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.WarningNotice;

import java.io.IOException;

public class DecreasingStopTimeDistanceWarningNotice extends WarningNotice {
    public DecreasingStopTimeDistanceWarningNotice(final String tripId,
                                                   final Integer stopSequence,
                                                   final Float shapeDistTraveled,
                                                   final Integer conflictingStopSequence) {
        super("stop_times.txt", W_013,
                "Decreasing traveled distance values",
                String.format("Trip with `trip_id` `%s` the stop `%s` has shape_dist_traveled=`%s`, which should be" +
                                " larger than the previous ones. In this case, the previous distance was `%s` (for the " +
                                "stop `%s`).",
                        tripId, conflictingStopSequence, shapeDistTraveled, shapeDistTraveled, stopSequence),
                null);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "trip_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "stop_sequence");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, stopSequence);
        putNoticeSpecific(KEY_STOP_TIME_SHAPE_DIST_TRAVELED, shapeDistTraveled);
        putNoticeSpecific(KEY_STOP_TIME_CONFLICTING_SHAPE_DIST_TRAVELED, shapeDistTraveled);
        putNoticeSpecific(KEY_STOP_TIME_CONFLICTING_STOP_SEQUENCE, conflictingStopSequence);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
