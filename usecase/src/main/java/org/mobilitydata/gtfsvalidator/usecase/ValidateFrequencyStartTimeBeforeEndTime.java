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

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FrequencyStartTimeAfterEndTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

/**
 * Use case to validate that `start_time` does not precede the `end_time`. This use case is triggered after completing
 * the {@code GtfsDataRepository} provided in the constructor with {@code Frequency} entities.
 */
public class ValidateFrequencyStartTimeBeforeEndTime {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final TimeUtils timeUtils;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateFrequencyStartTimeBeforeEndTime(final GtfsDataRepository dataRepo,
                                                   final ValidationResultRepository resultRepo,
                                                   final TimeUtils timeUtils,
                                                   final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.timeUtils = timeUtils;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if for each {@code Frequency} contained in {@link GtfsDataRepository} that
     * `end_time` date does not precede the `start_time`. If this requirement is not met, a
     * {@code StartTimeAfterEndTimeNotice} is added to the {@code ValidationResultRepo} provided in the
     * constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E048 - `start_time` and `end_time` out of order");
        dataRepo.getFrequencyAll().forEach((tripIdStartTime, frequency) -> {
            // endTime and startTime cannot be null at this stage. See Frequency builder.
            // Reference: http://gtfs.org/reference/static#frequenciestxt
            if (frequency.getEndTime() < frequency.getStartTime()) {
                resultRepo.addNotice(
                        new FrequencyStartTimeAfterEndTimeNotice(
                                "frequencies.txt",
                                timeUtils.convertIntegerToHHMMSS(frequency.getStartTime()),
                                timeUtils.convertIntegerToHHMMSS(frequency.getEndTime()),
                                frequency.getTripId())
                );
            }
        });
    }
}
