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

    private final String agency_id;
    private @NotNull
    final String agency_name;
    private @NotNull
    final String agency_url;
    private @NotNull
    final String agency_timezone;
    private final String agency_lang;
    private final String agency_phone;
    private final String agency_fare_url;
    private final String agency_email;

    public Agency(String agency_id, @NotNull String agency_name, @NotNull String agency_url,
                  @NotNull String agency_timezone, String agency_lang, String agency_phone,
                  String agency_fare_url, String agency_email) {

        this.agency_id = agency_id;
        this.agency_name = agency_name;
        this.agency_url = agency_url;
        this.agency_timezone = agency_timezone;
        this.agency_lang = agency_lang;
        this.agency_phone = agency_phone;
        this.agency_fare_url = agency_fare_url;
        this.agency_email = agency_email;
    }

    // getters only for immutability - followed by a builder pattern for construction of objects
    public String getAgency_id() {
        return agency_id;
    }

    public String getAgency_name() {
        return agency_name;
    }

    public String getAgency_url() {
        return agency_url;
    }

    public String getAgency_timezone() {
        return agency_timezone;
    }

    public String getAgency_lang() {
        return agency_lang;
    }

    public String getAgency_phone() {
        return agency_phone;
    }

    public String getAgency_fare_url() {
        return agency_fare_url;
    }

    public String getAgency_email() {
        return agency_email;
    }


    // builder pattern

    public static class AgencyBuilder {

        private String agency_id;
        private String agency_name;
        private String agency_url;
        private String agency_timezone;
        private String agency_lang;
        private String agency_phone;
        private String agency_fare_url;
        private String agency_email;

        // TODO: Check the necessity to include @NotNull attributes to builder pattern

        public AgencyBuilder agencyId(@Nullable String agency_id) {
            this.agency_id = agency_id;
            return this;
        }

        public AgencyBuilder agencyName(@NotNull String agency_name) {
            this.agency_name = agency_name;
            return this;
        }

        public AgencyBuilder agencyUrl(@NotNull String agency_url) {
            this.agency_url = agency_url;
            return this;
        }

        public AgencyBuilder agencyTimezone(@NotNull String agency_timezone) {
            this.agency_timezone = agency_timezone;
            return this;
        }

        public AgencyBuilder agencyLang(@Nullable String agency_lang) {
            this.agency_lang = agency_lang;
            return this;
        }

        public AgencyBuilder agencyPhone(@Nullable String agency_phone) {
            this.agency_phone = agency_phone;
            return this;
        }

        public AgencyBuilder agencyFareUrl(@Nullable String agency_fare_url) {
            this.agency_fare_url = agency_fare_url;
            return this;
        }

        public AgencyBuilder agencyEmail(@Nullable String agency_email) {
            this.agency_email = agency_email;
            return this;
        }

        public Agency build() {
            return new Agency(agency_id, agency_name, agency_url, agency_timezone, agency_lang, agency_phone,
                    agency_fare_url, agency_email);
        }
    }
}
