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

public class FrequencyStartTimeAfterEndTimeNotice extends ErrorNotice {
    public FrequencyStartTimeAfterEndTimeNotice(final String filename,
                                                final String startTimeAsString,
                                                final String endTimeAsString,
                                                final String tripId) {
        super(filename,
                E_048,
                "`end_time` after `start_time` in `frequencies.txt`",
                String.format("`end_time`: `%s` precedes `start_time`: `%s` for entity with composite id: " +
                                "`tripId`: `%s` -- `startTime`: `%s` in file: `%s`.",
                        endTimeAsString,
                        startTimeAsString,
                        tripId,
                        startTimeAsString,
                        filename),
                null);

        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "tripId");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "startTime");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, startTimeAsString);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
