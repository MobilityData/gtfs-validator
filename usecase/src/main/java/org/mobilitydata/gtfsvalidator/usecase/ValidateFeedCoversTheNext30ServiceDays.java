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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.FeedInfoExpiresInLessThan30DaysNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDateTime;

/**
 * Use case to validate that GTFS feed covers at least the next 30 days of service.
 * This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor with
 * {@code FeedInfo} entities.
 */
public class ValidateFeedCoversTheNext30ServiceDays {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateFeedCoversTheNext30ServiceDays(final GtfsDataRepository dataRepo,
                                                  final ValidationResultRepository resultRepo,
                                                  final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if GTFS feed is valid for at least the next 7 days. If this requirement is not
     * met, a
     * {@code FeedInfoExpiresInLessThan7DaysNotice} is added to the {@code ValidationResultRepo} provided in the
     * constructor.
     * This verification is executed if {@link FeedInfo} has non-null value for field `feed_end_date`.
     */
    public void execute() {
        logger.info("Validating rule 'W009 - Dataset should cover at least the next 30 days of service'"
                + System.lineSeparator());
        final LocalDateTime currentDate = LocalDateTime.now();
        final LocalDateTime currentDateAsYYYYMMDDHHMM = LocalDateTime.of(
                currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth(), 0, 0);

        dataRepo.getFeedInfoAll().stream()
                .filter(feedInfo -> feedInfo.getFeedEndDate() != null)
                .filter(feedInfo -> feedInfo.getFeedEndDate().isAfter(currentDateAsYYYYMMDDHHMM.plusDays(7)) &&
                        feedInfo.getFeedEndDate().isBefore(currentDateAsYYYYMMDDHHMM.plusDays(30)))
                .forEach(invalidFeedInfo -> resultRepo.addNotice(
                        new FeedInfoExpiresInLessThan30DaysNotice(currentDateAsYYYYMMDDHHMM.toString(),
                                invalidFeedInfo.getFeedEndDate().toString(), invalidFeedInfo.getFeedPublisherName())));
    }
}
