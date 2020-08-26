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

public class ValidateAgencyLangAndFeedInfoFeedLangMatch {
    private final GtfsDataRepository dataRepo;
    private final ValidationResultRepository resultRepo;
    private final Logger logger;

    public ValidateAgencyLangAndFeedInfoFeedLangMatch(final GtfsDataRepository dataRepo,
                                                      final ValidationResultRepository resultRepo,
                                                      final Logger logger) {
        this.dataRepo = dataRepo;
        this.resultRepo = resultRepo;
        this.logger = logger;
    }

    public void execute() {
        logger.info("Validating rule 'E055 - Mismatching feed and agency language fields'");
        final String feedInfoFeedLang = dataRepo.getFeedInfoAll().size() > 1 ?
                dataRepo.getFeedInfoAll().values().stream().findFirst().get().getFeedLang() :
                null;
        if (feedInfoFeedLang != null) {
            dataRepo.getAgencyAll().forEach((agencyId, agency) -> {
                if (agencyId != null) {
                    if (!feedInfoFeedLang.equals("mul"))
                        if (!agency.getAgencyLang().equals(feedInfoFeedLang)) {
                            resultRepo.addNotice(
                                    new FeedInfoLangAgencyLangMismatchNotice(
                                            agencyId,
                                            agency.getAgencyName(),
                                            agency.getAgencyName(),
                                            feedInfoFeedLang)
                            );
                        }
                }
            });
        }
    }
}
