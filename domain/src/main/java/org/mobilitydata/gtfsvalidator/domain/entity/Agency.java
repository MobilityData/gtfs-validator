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

public class Agency {

    private final String agencyId;

    @NotNull
    private final String agencyName;

    @NotNull
    private final String agencyUrl;

    @NotNull
    private final String agencyTimezone;

    private final String agencyLang;
    private final String agencyPhone;
    private final String agencyFareUrl;
    private final String agencyEmail;

    public Agency(String agencyId,
                  @NotNull String agencyName,
                  @NotNull String agencyUrl,
                  @NotNull String agencyTimezone,
                  String agencyLang,
                  String agencyPhone,
                  String agencyFareUrl,
                  String agencyEmail) {

        this.agencyId = agencyId;
        this.agencyName = agencyName;
        this.agencyUrl = agencyUrl;
        this.agencyTimezone = agencyTimezone;
        this.agencyLang = agencyLang;
        this.agencyPhone = agencyPhone;
        this.agencyFareUrl = agencyFareUrl;
        this.agencyEmail = agencyEmail;
    }

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

    public String getAgencyLang() {
        return agencyLang;
    }

    public String getAgencyPhone() {
        return agencyPhone;
    }

    public String getAgencyFareUrl() {
        return agencyFareUrl;
    }

    public String getAgencyEmail() {
        return agencyEmail;
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

        public AgencyBuilder(@NotNull String agencyName,
                             @NotNull String agencyUrl,
                             @NotNull String agencyTimezone) {
            this.agencyName = agencyName;
            this.agencyUrl = agencyUrl;
            this.agencyTimezone = agencyTimezone;
        }

        public AgencyBuilder agencyId(@Nullable String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public AgencyBuilder agencyLang(@Nullable String agencyLang) {
            this.agencyLang = agencyLang;
            return this;
        }

        public AgencyBuilder agencyPhone(@Nullable String agencyPhone) {
            this.agencyPhone = agencyPhone;
            return this;
        }

        public AgencyBuilder agencyFareUrl(@Nullable String agencyFareUrl) {
            this.agencyFareUrl = agencyFareUrl;
            return this;
        }

        public AgencyBuilder agencyEmail(@Nullable String agencyEmail) {
            this.agencyEmail = agencyEmail;
            return this;
        }

        public Agency build() {
            return new Agency(agencyId, agencyName, agencyUrl, agencyTimezone, agencyLang, agencyPhone,
                    agencyFareUrl, agencyEmail);
        }
    }
}