/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.usecase;

import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.FeedInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This use case turns a parsed entity representing a row from feed_info.txt into a concrete class
 */
public class ProcessParsedFeedInfo {
    private final ValidationResultRepository resultRepository;
    private final GtfsDataRepository gtfsDataRepository;
    private final FeedInfo.FeedInfoBuilder builder;


    public ProcessParsedFeedInfo(final ValidationResultRepository resultRepository,
                                 final GtfsDataRepository gtfsDataRepository,
                                 final FeedInfo.FeedInfoBuilder builder) {
        this.resultRepository = resultRepository;
        this.gtfsDataRepository = gtfsDataRepository;
        this.builder = builder;
    }

    /**
     * Use case execution method to go from a row from feed_info.txt to an internal representation.
     * <p>
     * This use case extracts values from a {@code ParsedEntity} and creates a {@code FeedInfo} object if the
     * requirements from the official GTFS specification are met. When these requirements are not met, related notices
     * generated in {@code FeedInfo.FeedInfoBuilder} are added to the result repository provided in the constructor.
     * This use case also adds a {@code DuplicatedEntityNotice} to said repository if the uniqueness constraint on
     * agency entities is not respected.
     *
     * @param validatedFeedInfo entity to be processed and added to the GTFS data repository
     */
    public void execute(final ParsedEntity validatedFeedInfo) {
        final String feedPublisherName = (String) validatedFeedInfo.get("feed_publisher_name");
        final String feedPublisherUrl = (String) validatedFeedInfo.get("feed_publisher_url");
        final String feedLang = (String) validatedFeedInfo.get("feed_lang");
        final LocalDateTime feedStartDate = (LocalDateTime) validatedFeedInfo.get("feed_start_date");
        final LocalDateTime feedEndDate = (LocalDateTime) validatedFeedInfo.get("feed_end_date");
        final String feedVersion = (String) validatedFeedInfo.get("feed_version");
        final String feedContactEmail = (String) validatedFeedInfo.get("feed_contact_email");
        final String feedContactUrl = (String) validatedFeedInfo.get("feed_contact_url");

        builder.feedPublisherName(feedPublisherName)
                .feedPublisherUrl(feedPublisherUrl)
                .feedLang(feedLang)
                .feedStartDate(feedStartDate)
                .feedEndDate(feedEndDate)
                .feedVersion(feedVersion)
                .feedContactEmail(feedContactEmail)
                .feedContactUrl(feedContactUrl);

        @SuppressWarnings("rawtypes") final EntityBuildResult agency = builder.build();

        if (agency.isSuccess()) {
            if (gtfsDataRepository.addFeedInfo((FeedInfo) agency.getData()) == null) {
                resultRepository.addNotice(new DuplicatedEntityNotice("feed_info.txt",
                        "feed_publisher_name", validatedFeedInfo.getEntityId()));
            }
        } else {
            //noinspection unchecked
            ((List<Notice>) agency.getData()).forEach(resultRepository::addNotice);
        }
    }
}