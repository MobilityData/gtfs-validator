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

public class OverlappingTripFrequenciesNotice extends ErrorNotice {
    public OverlappingTripFrequenciesNotice(final String tripId,
                                            final String currentFrequencyStartTime,
                                            final String currentFrequencyEndTime,
                                            final String conflictingFrequencyStartTime,
                                            final String conflictingFrequencyEndTime) {
        super("frequencies.txt",
                E_053,
                "Overlapping trip frequencies",
                String.format("Overlapping trip frequencies for trip: `%s`. First period `%s`-`%s` overlaps with" +
                                " second period: `%s`-`%s`.",
                        tripId, currentFrequencyStartTime, currentFrequencyEndTime,
                        conflictingFrequencyStartTime, conflictingFrequencyEndTime),
                null);

        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_PART, "tripId");
        putNoticeSpecific(KEY_COMPOSITE_KEY_FIRST_VALUE, tripId);
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_PART, "startTime");
        putNoticeSpecific(KEY_COMPOSITE_KEY_SECOND_VALUE, currentFrequencyStartTime);
        putNoticeSpecific(KEY_CONFLICTING_FREQUENCY_START_TIME, conflictingFrequencyStartTime);
        putNoticeSpecific(KEY_CONFLICTING_FREQUENCY_END_TIME, conflictingFrequencyEndTime);
    }

    @Override
    public void export(NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
