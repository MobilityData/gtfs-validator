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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingFeedEndDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

/**
 * Use case to validate that for each {@code FeedInfo} entity containing a non null value for field
 * `feed_start_date` that a non-value has been provided to field `feed_end_date`.
 * This use case is triggered after completing the {@code GtfsDataRepository} provided in the constructor with
 * {@code FeedInfo} entities.
 */
public class ValidateFeedInfoFeedEndDateIsPresent {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateFeedInfoFeedEndDateIsPresent(final GtfsDataRepository dataRepo,
                                                final ValidationResultRepository resultRepo,
                                                final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * use case execution method: validates for each {@code FeedInfo} entity containing a non null value for field
     * `feed_start_date` that a non-value has been provided to field `feed_end_date`.
     */
    public void execute() {
        logger.info("Validating rule W010 - `feed_end_date` should be provided if `feed_start_date` is provided"
                + System.lineSeparator());

        dataRepo.getFeedInfoAll()
                .forEach((feedPublisherName, feedInfo) -> {
                    if (feedInfo.getFeedStartDate() != null && feedInfo.getFeedEndDate() == null) {
                        resultRepo.addNotice(
                                new MissingFeedEndDateNotice(
                                        "feed_info.txt",
                                        "feed_end_date",
                                        "feed_publisher_name",
                                        "feed_publisher_url",
                                        "feed_lang",
                                        feedPublisherName,
                                        feedInfo.getFeedPublisherUrl(),
                                        feedInfo.getFeedLang()
                                )
                        );
                    }
                });
    }
}
