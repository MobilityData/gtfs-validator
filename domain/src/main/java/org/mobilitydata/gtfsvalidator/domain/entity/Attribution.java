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

import java.util.Objects;

public class Attribution {

    @Nullable
    private final String attributionId;

    @Nullable
    private final String agencyId;

    @Nullable
    private final String routeId;

    @Nullable
    private final String tripId;

    @NotNull
    private final String organizationName;

    private final boolean isProducer;
    private final boolean isAuthority;
    private final boolean isOperator;

    @Nullable
    private final String attributionUrl;

    @Nullable
    private final String attributionEmail;

    @Nullable
    private final String attributionPhone;

    private Attribution(@Nullable final String attributionId,
                        @Nullable final String agencyId,
                        @Nullable final String routeId,
                        @Nullable final String tripId,
                        @NotNull final String organizationName,
                        final boolean isProducer,
                        final boolean isAuthority,
                        final boolean isOperator,
                        @Nullable final String attributionUrl,
                        @Nullable final String attributionEmail,
                        @Nullable final String attributionPhone) {
        this.attributionId = attributionId;
        this.agencyId = agencyId;
        this.routeId = routeId;
        this.tripId = tripId;
        this.organizationName = organizationName;
        this.isProducer = isProducer;
        this.isOperator = isOperator;
        this.isAuthority = isAuthority;
        this.attributionUrl = attributionUrl;
        this.attributionEmail = attributionEmail;
        this.attributionPhone = attributionPhone;
    }

    @Nullable
    public String getAttributionId() {
        return attributionId;
    }

    @Nullable
    public String getAgencyId() {
        return agencyId;
    }

    @Nullable
    public String getRouteId() {
        return routeId;
    }

    @Nullable
    public String getTripId() {
        return tripId;
    }

    @NotNull
    public String getOrganizationName() {
        return organizationName;
    }

    @Nullable
    public String getAttributionUrl() {
        return attributionUrl;
    }

    @Nullable
    public String getAttributionEmail() {
        return attributionEmail;
    }

    @Nullable
    public String getAttributionPhone() {
        return attributionPhone;
    }

    public boolean isProducer() {
        return isProducer;
    }

    public boolean isAuthority() {
        return isAuthority;
    }

    public boolean isOperator() {
        return isOperator;
    }

    public static class AttributionBuilder {
        @Nullable
        private String attributionId;
        @Nullable
        private String agencyId;
        @Nullable
        private String routeId;
        @Nullable
        private String tripId;
        @NotNull
        private String organizationName;
        private boolean isProducer;
        private boolean isAuthority;
        private boolean isOperator;
        @Nullable
        private String attributionUrl;
        @Nullable
        private String attributionEmail;
        @Nullable
        private String attributionPhone;

        public AttributionBuilder(@NotNull final String organizationName) {
            this.organizationName = organizationName;
        }

        public AttributionBuilder attributionId(@Nullable final String attributionId) {
            this.attributionId = attributionId;
            return this;
        }

        public AttributionBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public AttributionBuilder routeId(@Nullable final String routeId) {
            this.routeId = routeId;
            return this;
        }

        public AttributionBuilder tripId(@Nullable final String tripId) {
            this.tripId = tripId;
            return this;
        }

        public AttributionBuilder organizationName(@NotNull final String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public AttributionBuilder attributionUrl(@Nullable final String attributionUrl) {
            this.attributionUrl = attributionUrl;
            return this;
        }

        public AttributionBuilder attributionEmail(@Nullable final String attributionEmail) {
            this.attributionEmail = attributionEmail;
            return this;
        }

        public AttributionBuilder attributionPhone(@Nullable final String attributionPhone) {
            this.attributionPhone = attributionPhone;
            return this;
        }

        public AttributionBuilder isProducer(@Nullable final Integer isProducer) {
            this.isProducer = Objects.equals(isProducer, 1);
            return this;
        }

        public AttributionBuilder isAuthority(@Nullable final Integer isAuthority) {
            this.isAuthority = Objects.equals(isAuthority, 1);
            return this;
        }

        public AttributionBuilder isOperator(@Nullable final Integer isOperator) {
            this.isOperator = Objects.equals(isOperator, 1);
            return this;
        }

        public Attribution build() {
            return new Attribution(attributionId, agencyId, routeId, tripId, organizationName, isProducer, isOperator,
                    isAuthority, attributionUrl, attributionEmail, attributionPhone);
        }
    }
}
