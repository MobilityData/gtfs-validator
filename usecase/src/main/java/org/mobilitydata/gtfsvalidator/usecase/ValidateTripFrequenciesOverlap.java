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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.frequencies.Frequency;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OverlappingTripFrequenciesNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Use case to verify that for each trip defined in `trips.txt` frequencies (defined by GTFS file `frequencies.txt` do
 * not overlap.
 */
public class ValidateTripFrequenciesOverlap {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;
    private final TimeUtils timeUtils;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param timeUtils  an instance of {@code TimeUtils} used to perform calculation on times
     * @param logger     a logger to log information about the validation process
     */
    public ValidateTripFrequenciesOverlap(final GtfsDataRepository dataRepo,
                                          final ValidationResultRepository resultRepo,
                                          final TimeUtils timeUtils,
                                          final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
        this.timeUtils = timeUtils;
    }

    /**
     * Use case execution method: checks for each trip that frequencies defined in `frequencies.txt` do not overlap.
     * A notice is generated each time frequencies overlap for a given trip_id. The notice is then added to the
     * {@link ValidationResultRepository} provided in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E053 - Trip frequencies overlap'");

        dataRepo.getFrequencyAllByTripId().forEach((tripId, frequencyCollection) -> {
            final Map<String, Frequency> visitedFrequencyTripIdStartTimeCollection = new HashMap<>();

            frequencyCollection.forEach(currentFrequency -> {
                visitedFrequencyTripIdStartTimeCollection.put(currentFrequency.getFrequencyMappingKey(),
                        currentFrequency);
                final int currentFrequencyStartTime = currentFrequency.getStartTime();
                final int currentFrequencyEndTime = currentFrequency.getEndTime();
                if (isFrequencyValid(currentFrequency)) {
                    frequencyCollection.forEach(unvisitedFrequency -> {
                        if (!visitedFrequencyTripIdStartTimeCollection.containsKey(
                                unvisitedFrequency.getFrequencyMappingKey())) {
                            final int unvisitedFrequencyStartTime = unvisitedFrequency.getStartTime();
                            final int unvisitedFrequencyEndTime = unvisitedFrequency.getEndTime();
                            if (isFrequencyValid(unvisitedFrequency)) {
                                if (timeUtils.arePeriodsOverlapping(currentFrequencyStartTime, currentFrequencyEndTime,
                                        unvisitedFrequencyStartTime, unvisitedFrequencyEndTime)) {
                                    resultRepo.addNotice(
                                            new OverlappingTripFrequenciesNotice(tripId,
                                                    timeUtils.convertIntegerToHMMSS(unvisitedFrequencyStartTime),
                                                    timeUtils.convertIntegerToHMMSS(unvisitedFrequencyEndTime),
                                                    timeUtils.convertIntegerToHMMSS(currentFrequencyStartTime),
                                                    timeUtils.convertIntegerToHMMSS(currentFrequencyEndTime)));
                                }
                            }
                        }
                    });
                }
            });
        });
    }

    /**
     * Utility method to check if a frequency is valid (e.g start_time < end_time)
     *
     * @param frequency {@link Frequency} to check
     * @return true is the frequency is valid (e.g start_time < end_time), otherwise returns false
     */
    private boolean isFrequencyValid(final Frequency frequency) {
        return frequency.getStartTime() < frequency.getEndTime();
    }
}
