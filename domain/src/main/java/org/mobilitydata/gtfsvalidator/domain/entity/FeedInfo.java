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

package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public class FeedInfo {

    @NotNull
    private final String feedPublisherName;

    @NotNull
    private final String feedPublisherUrl;

    @NotNull
    private final String feedLang;

    @Nullable
    private final LocalDateTime feedStartDate;

    @Nullable
    private final LocalDateTime feedEndDate;

    @Nullable
    private final String feedVersion;

    @Nullable
    private final String feedContactEmail;

    @Nullable
    private final String feedContactUrl;

    private FeedInfo(@NotNull String feedPublisherName,
                     @NotNull String feedPublisherUrl,
                     @NotNull String feedLang,
                     @Nullable LocalDateTime feedStartDate,
                     @Nullable LocalDateTime feedEndDate,
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
    public LocalDateTime getStartDate() {
        return feedStartDate;
    }

    @Nullable
    public LocalDateTime getEndDate() {
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

    public static class FeedInfoBuilder {
        @NotNull
        private String feedPublisherName;

        @NotNull
        private String feedPublisherUrl;

        @NotNull
        private String feedLang;

        @Nullable
        private LocalDateTime feedStartDate;

        @Nullable
        private LocalDateTime feedEndDate;

        @Nullable
        private String feedVersion;

        @Nullable
        private String feedContactEmail;

        @Nullable
        private String feedContactUrl;

        public FeedInfoBuilder(@NotNull String feedPublisherName,
                               @NotNull String feedPublisherUrl,
                               @NotNull String feedLang) {
            this.feedPublisherName = feedPublisherName;
            this.feedPublisherUrl = feedPublisherUrl;
            this.feedLang = feedLang;
        }

        public FeedInfoBuilder feedPublisherName(@NotNull String feedPublisherName) {
            this.feedPublisherName = feedPublisherName;
            return this;
        }

        public FeedInfoBuilder feedPublisherUrl(@NotNull String feedPublisherUrl) {
            this.feedPublisherUrl = feedPublisherUrl;
            return this;
        }

        public FeedInfoBuilder feedLang(@NotNull String feedLang) {
            this.feedLang = feedLang;
            return this;
        }

        public FeedInfoBuilder startDate(@NotNull LocalDateTime feedStartDate) {
            this.feedStartDate = feedStartDate;
            return this;
        }

        public FeedInfoBuilder feedEndDate(@NotNull LocalDateTime feedEndDate) {
            this.feedEndDate = feedEndDate;
            return this;
        }

        public FeedInfoBuilder feedVersion(@NotNull String feedVersion) {
            this.feedVersion = feedVersion;
            return this;
        }

        public FeedInfoBuilder feedContactEmail(@NotNull String feedContactEmail) {
            this.feedContactEmail = feedContactEmail;
            return this;
        }

        public FeedInfoBuilder feedContactUrl(@NotNull String feedContactUrl) {
            this.feedContactUrl = feedContactUrl;
            return this;
        }

        public FeedInfo build() {
            return new FeedInfo(feedPublisherName, feedPublisherUrl, feedLang, feedStartDate, feedEndDate, feedVersion,
                    feedContactEmail, feedContactUrl);
        }
    }
}
