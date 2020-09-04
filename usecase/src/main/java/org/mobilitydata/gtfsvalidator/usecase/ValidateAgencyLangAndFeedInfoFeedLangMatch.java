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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FeedInfoLangAgencyLangMismatchNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashSet;
import java.util.Set;

/**
 * Use case to validate that `agency.agency_lang` and `feed_info.feed_lang` match. This use case is triggered after
 * completing the {@code GtfsDataRepository} provided in the constructor with {@code FeedInfo} entities.
 */
public class ValidateAgencyLangAndFeedInfoFeedLangMatch {
    private static final String MUL = "mul";
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    /**
     * @param dataRepo   a repository storing the data of a GTFS dataset
     * @param resultRepo a repository storing information about the validation process
     * @param logger     a logger to log information about the validation process
     */
    public ValidateAgencyLangAndFeedInfoFeedLangMatch(final GtfsDataRepository dataRepo,
                                                      final ValidationResultRepository resultRepo,
                                                      final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    /**
     * Use case execution method: checks if for each {@code Agency} defines a value for `agency.agency_lang` that
     * matches the value contained in `feed_info.feed_lang` if `feed_info.txt` is provided. If this requirement is not
     * met, a {@code FeedInfoLangAgencyLangMismatchNotice} is added to the {@code ValidationResultRepo} provided
     * in the constructor.
     */
    public void execute() {
        logger.info("Validating rule 'E055 - Mismatching feed and agency language fields'");

        if (dataRepo.getFeedInfoAll().size() == 0) {
            return;
        }
        // .get can be used without isPresent check here since line 55 ensures the presence of at least 1 element
        // in the map
        final String feedInfoFeedLang =
                dataRepo.getFeedInfoAll().values().stream().findFirst().get().getFeedLang();
        final Set<String> agencyLangCollection = new HashSet<>();
        dataRepo.getAgencyAll().forEach((agencyId, agency) -> agencyLangCollection.add(agency.getAgencyLang()));
        if (feedInfoFeedLang.equals(MUL)) {
            // If feed_lang is mul and there isn't more than one agency_lang, that's an error
            if (agencyLangCollection.size() <= 1) {
                resultRepo.addNotice(new FeedInfoLangAgencyLangMismatchNotice(agencyLangCollection));
                return;
            }
            return;
        }
        // If there is more than one agency_lang and feed_lang isn't mul, that's an error
        if (agencyLangCollection.size() > 1) {
            resultRepo.addNotice(new FeedInfoLangAgencyLangMismatchNotice(agencyLangCollection, feedInfoFeedLang));
            return;
        }

        dataRepo.getAgencyAll().forEach((agencyId, agency) -> {
            // If feed_lang is not mul and differs from agency_lang, that's an error
            if (!feedInfoFeedLang.equals(agency.getAgencyLang())) {
                resultRepo.addNotice(
                        new FeedInfoLangAgencyLangMismatchNotice(
                                agencyId,
                                agency.getAgencyName(),
                                agency.getAgencyLang(),
                                feedInfoFeedLang));
            }
        });
    }
}
