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

package org.mobilitydata.gtfsvalidator.usecase.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;

public class Agency extends Entity {

    // todo: add hash field for agency file

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

    @Override
    public Entity solveType(GtfsDataRepository gtfsDataRepository) {
        return gtfsDataRepository.addEntity(this);
    }

    public static class AgencyBuilder {

        private String agencyId;
        private String agencyName;
        private String agencyUrl;
        private String agencyTimezone;
        private String agencyLang;
        private String agencyPhone;
        private String agencyFareUrl;
        private String agencyEmail;

        public AgencyBuilder(@NotNull final String agencyName,
                             @NotNull final String agencyUrl,
                             @NotNull final String agencyTimezone) {
            this.agencyName = agencyName;
            this.agencyUrl = agencyUrl;
            this.agencyTimezone = agencyTimezone;
        }

        public AgencyBuilder agencyName(@Nullable final String agencyName) {
            this.agencyName = agencyName;
            return this;
        }

        public AgencyBuilder agencyUrl(@Nullable final String agencyUrl) {
            this.agencyUrl = agencyUrl;
            return this;
        }

        public AgencyBuilder agencyTimezone(@Nullable final String agencyTimezone) {
            this.agencyTimezone = agencyTimezone;
            return this;
        }

        public AgencyBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public AgencyBuilder agencyLang(@Nullable final String agencyLang) {
            this.agencyLang = agencyLang;
            return this;
        }

        public AgencyBuilder agencyPhone(@Nullable final String agencyPhone) {
            this.agencyPhone = agencyPhone;
            return this;
        }

        public AgencyBuilder agencyFareUrl(@Nullable final String agencyFareUrl) {
            this.agencyFareUrl = agencyFareUrl;
            return this;
        }

        public AgencyBuilder agencyEmail(@Nullable final String agencyEmail) {
            this.agencyEmail = agencyEmail;
            return this;
        }

        public Agency build() {
            return new Agency(agencyId, agencyName, agencyUrl, agencyTimezone, agencyLang, agencyPhone,
                    agencyFareUrl, agencyEmail);
        }
    }
}