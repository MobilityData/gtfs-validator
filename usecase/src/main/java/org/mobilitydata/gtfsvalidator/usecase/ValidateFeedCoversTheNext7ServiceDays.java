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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoExpiresInLessThan7DaysNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDate;

/**
 * Use case to validate that GTFS feed is valid for at least the next 7 days.
 * This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor with
 * {@code FeedInfo} entities.
 */
public class ValidateFeedCoversTheNext7ServiceDays {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger used to log information about the validation process
     */
    public ValidateFeedCoversTheNext7ServiceDays(final GtfsDataRepository dataRepo,
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
        logger.info("Validating rule 'E038 - Dataset should be valid for at least the next 7 days'");
        final LocalDate currentDate = LocalDate.now();

        dataRepo.getFeedInfoAll().forEach((feedPublisherName, feedInfo) -> {
            final LocalDate feedEndDate = feedInfo.getFeedEndDate();
            if (feedEndDate != null) {
                if (feedEndDate.isBefore(currentDate.plusDays(7))) {
                    resultRepo.addNotice(
                            new FeedInfoExpiresInLessThan7DaysNotice(
                                    "feed_info.txt",
                                    currentDate.toString(),
                                    feedEndDate.toString(),
                                    "feed_end_date",
                                    "feed_publisher_name",
                                    "feed_publisher_url",
                                    "feed_lang",
                                    feedPublisherName,
                                    feedInfo.getFeedPublisherUrl(),
                                    feedInfo.getFeedLang())
                    );

                }
            }
        });
    }
}
