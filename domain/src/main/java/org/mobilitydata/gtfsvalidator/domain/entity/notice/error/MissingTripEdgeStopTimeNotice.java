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

public class MissingTripEdgeStopTimeNotice extends ErrorNotice {

    public MissingTripEdgeStopTimeNotice(final String fieldName, final String tripId, final Integer stopSequence) {
        super("stop_times.txt", E_044,
                "Missing trip edge stop time value",
                "Edge of trip with id:`" + tripId + "` is missing value for field:`" + fieldName
                        + "`. Stop time composite id: " +
                        "`trip_id`: `" + tripId + "`" + "--" +
                        "`stop_sequence`: `" + stopSequence + "`.",
                null);

        putNoticeSpecific(KEY_FIELD_NAME, fieldName);
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "trip_id");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "stop_sequence");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, stopSequence);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}