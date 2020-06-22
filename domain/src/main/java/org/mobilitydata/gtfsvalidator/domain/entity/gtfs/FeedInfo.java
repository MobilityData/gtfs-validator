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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in feed_info.txt. Can not be directly instantiated: user must use the
 * {@link FeedInfoBuilder} to create this.
 */
public class FeedInfo extends GtfsEntity {
    @NotNull
    private final String feedPublisherName;
    @NotNull
    private final String feedPublisherUrl;
    @NotNull
    private final String feedLang;
    @Nullable
    private final LocalDate feedStartDate;
    @Nullable
    private final LocalDate feedEndDate;
    @Nullable
    private final String feedVersion;
    @Nullable
    private final String feedContactEmail;
    @Nullable
    private final String feedContactUrl;

    /**
     * Class for all entities defined in feed_info.txt
     *
     * @param feedPublisherName full name of the organization that publishes the dataset
     * @param feedPublisherUrl  URL of the dataset publishing organization's website
     * @param feedLang          default language used for the text in this dataset
     * @param feedStartDate     date from which the services defined in dataset are valid
     * @param feedEndDate       date until which the services defined in dataset are valid
     * @param feedVersion       indicates the current version of their GTFS dataset
     * @param feedContactEmail  email address for communication regarding the GTFS dataset and data publishing
     *                          practices
     * @param feedContactUrl    URL for contact information, a web-form, support desk, or other tools for communication
     *                          regarding the GTFS dataset and data publishing practices
     */
    private FeedInfo(@NotNull String feedPublisherName,
                     @NotNull String feedPublisherUrl,
                     @NotNull String feedLang,
                     @Nullable LocalDate feedStartDate,
                     @Nullable LocalDate feedEndDate,
                     @Nullable String feedVersion,
                     @Nullable String feedContactEmail,
                     @Nullable String feedContactUrl) {
        this.feedPublisherName = feedPublisherName;
        this.feedPublisherUrl = feedPublisherUrl;
        this.feedLang = feedLang;
        this.feedStartDate = feedStartDate;
        this.feedEndDate = feedEndDate;
        this.feedVersion = feedVersion;
        this.feedContactEmail = feedContactEmail;
        this.feedContactUrl = feedContactUrl;
    }

    @NotNull
    public String getFeedPublisherName() {
        return feedPublisherName;
    }

    @NotNull
    public String getFeedPublisherUrl() {
        return feedPublisherUrl;
    }

    @NotNull
    public String getFeedLang() {
        return feedLang;
    }

    @Nullable
    public LocalDate getStartDate() {
        return feedStartDate;
    }

    @Nullable
    public LocalDate getEndDate() {
        return feedEndDate;
    }

    @Nullable
    public String getFeedVersion() {
        return feedVersion;
    }

    @Nullable
    public String getFeedContactEmail() {
        return feedContactEmail;
    }

    @Nullable
    public String getFeedContactUrl() {
        return feedContactUrl;
    }

    /**
     * Builder class to create {@code FeedInfo} objects. Allows an unordered definition of the different attributes of
     * {@link FeedInfo}.
     */
    public static class FeedInfoBuilder {
        private String feedPublisherName;
        private String feedPublisherUrl;
        private String feedLang;
        private LocalDate feedStartDate;
        private LocalDate feedEndDate;
        private String feedVersion;
        private String feedContactEmail;
        private String feedContactUrl;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field feedPublisherName value and returns this
         *
         * @param feedPublisherName full name of the organization that publishes the dataset
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedPublisherName(@NotNull final String feedPublisherName) {
            this.feedPublisherName = feedPublisherName;
            return this;
        }

        /**
         * Sets field feedPublisherUrl value and returns this
         *
         * @param feedPublisherUrl URL of the dataset publishing organization's website
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedPublisherUrl(@NotNull final String feedPublisherUrl) {
            this.feedPublisherUrl = feedPublisherUrl;
            return this;
        }

        /**
         * Sets field feedLang value and returns this
         *
         * @param feedLang default language used for the text in this dataset
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedLang(@NotNull final String feedLang) {
            this.feedLang = feedLang;
            return this;
        }

        /**
         * Sets field feedStartDate value and returns this
         *
         * @param feedStartDate date from which the services defined in dataset are valid
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedStartDate(final LocalDate feedStartDate) {
            this.feedStartDate = feedStartDate;
            return this;
        }

        /**
         * Sets field feedEndDate value and returns this
         *
         * @param feedEndDate date until which the services defined in dataset are valid
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedEndDate(final LocalDate feedEndDate) {
            this.feedEndDate = feedEndDate;
            return this;
        }

        /**
         * Sets field feedVersion value and returns this
         *
         * @param feedVersion indicates the current version of their GTFS dataset
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedVersion(final String feedVersion) {
            this.feedVersion = feedVersion;
            return this;
        }

        /**
         * Sets field feedContactEmail value and returns this
         *
         * @param feedContactEmail email address for communication regarding the GTFS dataset and data publishing
         *                         practices
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedContactEmail(final String feedContactEmail) {
            this.feedContactEmail = feedContactEmail;
            return this;
        }

        /**
         * Sets field feedContactUrl value and returns this
         *
         * @param feedContactUrl URL for contact information, a web-form, support desk, or other tools for
         *                       communication regarding the GTFS dataset and data publishing practices
         * @return builder for future object creation
         */
        public FeedInfoBuilder feedContactUrl(final String feedContactUrl) {
            this.feedContactUrl = feedContactUrl;
            return this;
        }

        /**
         * Returns {@code EntityBuildResult} representing a row from feed_info.txt if the requirements from the official
         * GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues
         *
         * @return {@link EntityBuildResult} representing a row from feed_info.txt if the requirements from the official
         * GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues.
         */
        public EntityBuildResult<?> build() {
            if (feedPublisherName == null || feedPublisherUrl == null || feedLang == null) {
                if (feedPublisherName == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("feed_info.txt",
                            "feed_publisher_name", null));
                }
                if (feedPublisherUrl == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("feed_info.txt",
                            "feed_contact_url", null));
                }
                if (feedLang == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("feed_info.txt",
                            "feed_lang", null));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new FeedInfo(feedPublisherName, feedPublisherUrl, feedLang,
                        feedStartDate, feedEndDate, feedVersion, feedContactEmail, feedContactUrl));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         * @return builder with all fields set to null;
         */
        public FeedInfoBuilder clearFieldAll() {
            feedPublisherName = null;
            feedPublisherUrl = null;
            feedLang = null;
            feedStartDate = null;
            feedEndDate = null;
            feedVersion = null;
            feedContactEmail = null;
            feedContactUrl = null;
            noticeCollection.clear();
            return this;
        }
    }
}
