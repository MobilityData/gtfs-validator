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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTimeArrivalTimeAfterDepartureTimeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

/**
 * Use case to validate that `departure_time` does not precede the `arrival_time` date if both are fields are
 * provided. This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor with
 * {@code StopTime} entities.
 */
public class ValidateStopTimeDepartureTimeAfterArrivalTime {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final TimeUtils timeUtils;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateStopTimeDepartureTimeAfterArrivalTime(final GtfsDataRepository dataRepo,
                                                         final ValidationResultRepository resultRepo,
                                                         final TimeUtils timeUtils,
                                                         final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.timeUtils = timeUtils;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if for each {@code StopTime} contained in {@link GtfsDataRepository} that
     * `departure_time` date does not precede the `arrival_time`. If this requirement is not met, a
     * {@code StopTimeArrivalTimeAfterDepartureTimeNotice} is added to the {@code ValidationResultRepo} provided in the
     * constructor.
     * This verification is executed if {@link StopTime} has non-null value for both fields `departure_time` and
     * `arrival_time`.
     */
    public void execute() {
        logger.info("Validating rule 'E045 - `departure_time` and `arrival_time` out of order");
        dataRepo.getStopTimeAll().forEach((tripId, tripStopTimes) -> tripStopTimes.forEach((stopSequence, stopTime) -> {
            final Integer stopTimeArrivalTime = stopTime.getArrivalTime();
            final Integer stopTimeDepartureTime = stopTime.getDepartureTime();
            if (stopTimeDepartureTime != null && stopTimeArrivalTime != null) {
                if (stopTimeDepartureTime < stopTimeArrivalTime) {
                    resultRepo.addNotice(
                            new StopTimeArrivalTimeAfterDepartureTimeNotice(
                                    "stop_times.txt",
                                    timeUtils.convertIntegerToHHMMSS(stopTimeArrivalTime),
                                    timeUtils.convertIntegerToHHMMSS(stopTimeDepartureTime),
                                    tripId,
                                    stopSequence)
                    );
                }
            }
        }));
    }
}
