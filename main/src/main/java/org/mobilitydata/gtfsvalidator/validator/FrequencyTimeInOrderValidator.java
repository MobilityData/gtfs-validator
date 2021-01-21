/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndTimeOutOfOrderNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

import static org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableLoader.FILENAME;

/**
 * Validates `frequencies.start_time` is before or equal to `frequencies.end_time` for a single entity.
 * <p>
 * Generated notice: {@link StartAndEndTimeOutOfOrderNotice}.
 */
@GtfsValidator
public class FrequencyTimeInOrderValidator extends SingleEntityValidator<GtfsFrequency> {
    @Override
    public void validate(GtfsFrequency frequency, NoticeContainer noticeContainer) {
        // validate() will only be called if startTime and endTime have been populated for this frequency
        GtfsTime startTime = frequency.startTime();
        GtfsTime endTime = frequency.endTime();
        if (startTime.isAfter(endTime)) {
            noticeContainer.addValidationNotice(
                new StartAndEndTimeOutOfOrderNotice(
                    FILENAME, frequency.tripId(), frequency.csvRowNumber(),
                    startTime, endTime));
        }
    }
}

