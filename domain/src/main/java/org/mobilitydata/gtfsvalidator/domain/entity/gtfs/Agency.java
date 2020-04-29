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

import java.util.List;

/**
 * Class for all entities defined in agency.txt. Can not be directly instantiated: user must use the
 * {@link AgencyBuilder} to create this.
 */
@SuppressWarnings("ALL")
public class Agency {

    @Nullable
    private final String agencyId;

    @NotNull
    private final String agencyName;

    @NotNull
    private final String agencyUrl;

    @NotNull
    private final String agencyTimezone;

    @Nullable
    private final String agencyLang;

    @Nullable
    private final String agencyPhone;

    @Nullable
    private final String agencyFareUrl;

    @Nullable
    private final String agencyEmail;

    /**
     * Class for all entities defined in agency.txt
     *
     * @param agencyId       identifies a transit brand which is often synonymous with a transit agency
     * @param agencyName     full name of the transit agency
     * @param agencyUrl      URL of the transit agency
     * @param agencyTimezone timezone where the transit agency is located
     * @param agencyLang     primary language used by this transit agency
     * @param agencyPhone    a voice telephone number for the specified agency
     * @param agencyFareUrl  URL of a web page that allows a rider to purchase tickets or other fare instruments for
     *                       that agency online
     * @param agencyEmail    email address actively monitored by the agency’s customer service department
     */
    private Agency(@Nullable final String agencyId,
                   @NotNull final String agencyName,
                   @NotNull final String agencyUrl,
                   @NotNull final String agencyTimezone,
                   @Nullable final String agencyLang,
                   @Nullable final String agencyPhone,
                   @Nullable final String agencyFareUrl,
                   @Nullable final String agencyEmail) {

        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.agencyUrl = agencyUrl;
        this.agencyTimezone = agencyTimezone;
        this.agencyLang = agencyLang;
        this.agencyPhone = agencyPhone;
        this.agencyFareUrl = agencyFareUrl;
        this.agencyEmail = agencyEmail;
    }

    @Nullable
    public String getAgencyId() {
        return agencyId;
    }

    @NotNull
    public String getAgencyName() {
        return agencyName;
    }

    @NotNull
    public String getAgencyUrl() {
        return agencyUrl;
    }

    @NotNull
    public String getAgencyTimezone() {
        return agencyTimezone;
    }

    @Nullable
    public String getAgencyLang() {
        return agencyLang;
    }

    @Nullable
    public String getAgencyPhone() {
        return agencyPhone;
    }

    @Nullable
    public String getAgencyFareUrl() {
        return agencyFareUrl;
    }

    @Nullable
    public String getAgencyEmail() {
        return agencyEmail;
    }

    /**
     * Builder class to create {@link Agency} objects. Allows an unordered definition of the different attributes of
     * {@link Agency}.
     */
    public static class AgencyBuilder {

        private String agencyId;
        private String agencyName;
        private String agencyUrl;
        private String agencyTimezone;
        private String agencyLang;
        private String agencyPhone;
        private String agencyFareUrl;
        private String agencyEmail;
        private final List<Notice> noticeCollection;

        public AgencyBuilder(final List<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
        }

        /**
         * Sets field agencyName value and returns this
         *
         * @param agencyName defines the record that corresponds to the field to be translated
         * @return builder for future object creation
         */
        public AgencyBuilder agencyName(@NotNull final String agencyName) {
            this.agencyName = agencyName;
            return this;
        }

        /**
         * Sets field agencyUrl value and returns this
         *
         * @param agencyUrl defines the record that corresponds to the field to be translated
         * @return builder for future object creation
         */
        public AgencyBuilder agencyUrl(@NotNull final String agencyUrl) {
            this.agencyUrl = agencyUrl;
            return this;
        }

        /**
         * Sets field agencyTimezone value and returns this
         *
         * @param agencyTimezone timezone where the transit agency is located
         * @return builder for future object creation
         */
        public AgencyBuilder agencyTimezone(@NotNull final String agencyTimezone) {
            this.agencyTimezone = agencyTimezone;
            return this;
        }

        /**
         * Sets field agencyId value and returns this
         *
         * @param agencyId identifies a transit brand which is often synonymous with a transit agency
         * @return builder for future object creation
         */
        public AgencyBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        /**
         * Sets field agencyLang value and returns this
         *
         * @param agencyLang primary language used by this transit agency
         * @return builder for future object creation
         */
        public AgencyBuilder agencyLang(@Nullable final String agencyLang) {
            this.agencyLang = agencyLang;
            return this;
        }

        /**
         * Sets field agencyPhone value and returns this
         *
         * @param agencyPhone a voice telephone number for the specified agency
         * @return builder for future object creation
         */
        public AgencyBuilder agencyPhone(@Nullable final String agencyPhone) {
            this.agencyPhone = agencyPhone;
            return this;
        }

        /**
         * Sets field agencyFareUrl value and returns this
         *
         * @param agencyFareUrl URL of a web page that allows a rider to purchase tickets or other fare instruments for
         *                      that agency online         * @return builder for future object creation
         */
        public AgencyBuilder agencyFareUrl(@Nullable final String agencyFareUrl) {
            this.agencyFareUrl = agencyFareUrl;
            return this;
        }

        /**
         * Sets field agencyEmail value and returns this
         *
         * @param agencyEmail email address actively monitored by the agency’s customer service department
         * @return builder for future object creation
         */
        @SuppressWarnings("UnusedReturnValue")
        public AgencyBuilder agencyEmail(@Nullable final String agencyEmail) {
            this.agencyEmail = agencyEmail;
            return this;
        }

        /**
         * Entity representing a row from agency.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns list of {@link Notice}.
         *
         * @return Entity representing a row from agency.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns list of {@link Notice}.
         */
        public EntityBuildResult build() {
            noticeCollection.clear();

            if (agencyName == null || agencyUrl == null || agencyTimezone == null) {

                if (agencyName == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("agency.txt", "agency_name",
                            agencyId));
                }
                if (agencyUrl == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("agency.txt", "agency_url",
                            agencyId));
                }
                if (agencyTimezone == null) {
                    noticeCollection.add((new MissingRequiredValueNotice("agency.txt",
                            "agency_timezone", agencyId)));
                }
                //noinspection unchecked
                return new EntityBuildResult(noticeCollection, EntityBuildResult.Status.FAILURE);
            } else {
                //noinspection unchecked
                return new EntityBuildResult(new Agency(agencyId, agencyName, agencyUrl, agencyTimezone, agencyLang,
                        agencyPhone, agencyFareUrl, agencyEmail),
                        EntityBuildResult.Status.SUCCESS);
            }
        }
    }
}