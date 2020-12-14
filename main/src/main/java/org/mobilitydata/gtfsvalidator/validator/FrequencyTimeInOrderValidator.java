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

import static org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableLoader.FILENAME;

/**
 * Validates `frequencies.start_time` is before `frequencies.end_time` for a single entity.
 * <p>
 * Generated notices:
 * * StartAndEndTimeOutOfOrder
 */
@GtfsValidator
public class FrequencyTimeInOrderValidator extends SingleEntityValidator<GtfsFrequency> {
    @Override
    public void validate(GtfsFrequency frequency, NoticeContainer noticeContainer) {
        // startTime and endTime are assumed to be not null: therefore, we do not check hasStartTime and hasEndTime.
        if (frequency.startTime().isAfter(frequency.endTime())) {
            noticeContainer.addNotice(
                    new StartAndEndTimeOutOfOrderNotice(
                            FILENAME,
                            frequency.tripId(),
                            frequency.csvRowNumber(),
                            frequency.startTime(),
                            frequency.endTime()
                    )
            );
        }
    }
}


