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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.OverlappingTripFrequenciesNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ValidateFrequencyOverlap {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;
    private final TimeUtils timeUtils;

    public ValidateFrequencyOverlap(final GtfsDataRepository dataRepo,
                                    final ValidationResultRepository resultRepo,
                                    final TimeUtils timeUtils,
                                    final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
        this.timeUtils = timeUtils;
    }

    public void execute() {
        logger.info("Validating rule 'E053 - Trip frequencies overlap'");
        dataRepo.getFrequencyAllByTripId().forEach((tripId, frequencyCollection) -> {
            final List<String> visitedFrequencyTripIdStartTimeCollection = new ArrayList<>();

            frequencyCollection.forEach(currentFrequency -> {
                visitedFrequencyTripIdStartTimeCollection.add(currentFrequency.getFrequencyMappingKey());
                final int currentFrequencyStartTime = currentFrequency.getStartTime();
                final int currentFrequencyEndTime = currentFrequency.getEndTime();
                if (currentFrequencyStartTime < currentFrequencyEndTime) {
                    frequencyCollection.forEach(unvisitedFrequency -> {
                        if (!visitedFrequencyTripIdStartTimeCollection.contains(
                                unvisitedFrequency.getFrequencyMappingKey())) {
                            final int unvisitedFrequencyStartTime = unvisitedFrequency.getStartTime();
                            final int unvisitedFrequencyEndTime = unvisitedFrequency.getEndTime();
                            if (unvisitedFrequencyStartTime < unvisitedFrequencyEndTime) {
                                if (timeUtils.arePeriodsOverlapping(currentFrequencyStartTime, currentFrequencyEndTime,
                                        unvisitedFrequencyStartTime, unvisitedFrequencyEndTime)) {
                                    resultRepo.addNotice(
                                            new OverlappingTripFrequenciesNotice(tripId,
                                                    timeUtils.convertIntegerToHMMSS(currentFrequencyStartTime),
                                                    timeUtils.convertIntegerToHMMSS(currentFrequencyEndTime),
                                                    timeUtils.convertIntegerToHMMSS(unvisitedFrequencyStartTime),
                                                    timeUtils.convertIntegerToHMMSS(unvisitedFrequencyEndTime)));
                                }
                            }
                        }
                    });
                }
            });
        });
    }
}
