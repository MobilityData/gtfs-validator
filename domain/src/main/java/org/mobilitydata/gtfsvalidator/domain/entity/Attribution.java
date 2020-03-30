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

/**
 * Entity representing a row from attributions.txt.
 * See http://gtfs.org/reference/static#attributionstxt
 * <p>
 * This class can not be directly instantiated. User must use {@link AttributionBuilder} to create a {@link Attribution}
 * object.
 */
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

    /**
     * Builder class to create {@link Attribution} objects.
     */
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

        /**
         * Builder class constructor. Creates a builder from organizationName provided as parameter. Allows an unordered
         * definition of the different attributes of {@link Attribution}.
         *
         * @param organizationName name of the organization that the dataset is attributed to
         */
        public AttributionBuilder(@NotNull final String organizationName) {
            this.organizationName = organizationName;
        }

        /**
         * Sets field attributionId value and returns this
         *
         * @param attributionId identifies an attribution for the dataset or a subset of it
         * @return builder for future object creation
         */
        public AttributionBuilder attributionId(@Nullable final String attributionId) {
            this.attributionId = attributionId;
            return this;
        }

        /**
         * Sets field agencyId value and returns this
         *
         * @param agencyId agency to which the attribution applies
         * @return builder for future object creation
         */
        public AttributionBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        /**
         * Sets field routeId value and returns this
         *
         * @param routeId route to which the attribution applies
         * @return builder for future object creation
         */
        public AttributionBuilder routeId(@Nullable final String routeId) {
            this.routeId = routeId;
            return this;
        }

        /**
         * Sets field tripId value and returns this
         *
         * @param tripId trip to which the attribution applies
         * @return builder for future object creation
         */
        public AttributionBuilder tripId(@Nullable final String tripId) {
            this.tripId = tripId;
            return this;
        }

        /**
         * Sets field organizationName value and returns this
         *
         * @param organizationName name of the organization that the dataset is attributed to
         * @return builder for future object creation
         */
        public AttributionBuilder organizationName(@NotNull final String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        /**
         * Sets field isProducer value and returns this
         *
         * @param isProducer The role of the organization if producer. Valid options are:
         *                   0 or empty - Organization doesn’t have this role
         *                   1 - Organization does have this role
         *                   At least one of the fields isProducer, isOperator, or isAuthority should be set at true.
         * @return builder for future object creation
         */
        public AttributionBuilder isProducer(@Nullable final Integer isProducer) {
            this.isProducer = Objects.equals(isProducer, 1);
            return this;
        }

        /**
         * Sets field isProducer value and returns this
         *
         * @param isAuthority The role of the organization if authority. Valid options are:
         *                    0 or empty - Organization doesn’t have this role
         *                    1 - Organization does have this role
         *                    At least one of the fields isProducer, isOperator, or isAuthority should be set at true.
         * @return builder for future object creation
         */
        public AttributionBuilder isAuthority(@Nullable final Integer isAuthority) {
            this.isAuthority = Objects.equals(isAuthority, 1);
            return this;
        }

        /**
         * Sets field isOperator value and returns this
         *
         * @param isOperator The role of the organization if operator. Valid options are:
         *                   0 or empty - Organization doesn’t have this role
         *                   1 - Organization does have this role
         *                   At least one of the fields isProducer, isOperator, or isAuthority should be set at true.
         * @return builder for future object creation
         */
        public AttributionBuilder isOperator(@Nullable final Integer isOperator) {
            this.isOperator = Objects.equals(isOperator, 1);
            return this;
        }

        /**
         * Sets field attributionUrl value and returns this
         *
         * @param attributionUrl URL of the organization.
         * @return builder for future object creation
         */
        public AttributionBuilder attributionUrl(@Nullable final String attributionUrl) {
            this.attributionUrl = attributionUrl;
            return this;
        }

        /**
         * Sets field attributionEmail value and returns this
         *
         * @param attributionEmail Email of the organization.
         * @return builder for future object creation
         */
        public AttributionBuilder attributionEmail(@Nullable final String attributionEmail) {
            this.attributionEmail = attributionEmail;
            return this;
        }

        /**
         * Sets field attributionPhone value and returns this
         *
         * @param attributionPhone Phone number of the organization.
         * @return builder for future object creation
         */
        public AttributionBuilder attributionPhone(@Nullable final String attributionPhone) {
            this.attributionPhone = attributionPhone;
            return this;
        }

        /**
         * Creates a {@link Attribution} objects from fields provided via {@link AttributionBuilder} methods. Throws
         * {@link IllegalArgumentException} if fields isProducer, isAuthority, isOperator are set to false at the same
         * time. Throws {@link IllegalArgumentException} is fields marked as @NotNull are passed with null value.
         *
         * @return Entity representing a row from attributions.txt
         * @throws IllegalArgumentException if fields isProducer, isAuthority, isOperator are set to false at the
         *                                  same time. Throws {@link IllegalArgumentException} is fields marked as
         *                                  not null are passed with null value.
         */
        @SuppressWarnings("ConstantConditions")
        public Attribution build() throws IllegalArgumentException {

            if (organizationName == null) {
                throw new IllegalArgumentException("organizationName can not be null");
            }
            if ((isProducer == isAuthority == isOperator) && (!isProducer)) {
                throw new IllegalArgumentException("values for isProducer, isAuthority and isOperator can not " +
                        "all be false");
            }
            return new Attribution(attributionId, agencyId, routeId, tripId, organizationName, isProducer, isOperator,
                    isAuthority, attributionUrl, attributionEmail, attributionPhone);
        }
    }
}