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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InvalidAgencyIdNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingShortAndLongNameForRouteNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in routes.txt. Can not be directly instantiated: user must use the
 * {@link RouteBuilder} to create this.
 */
public class Route extends GtfsEntity {
    @NotNull
    private final String routeId;
    @Nullable
    private final String agencyId;
    @Nullable
    private final String routeShortName;
    @Nullable
    private final String routeLongName;
    @Nullable
    private final String routeDesc;
    @NotNull
    private final RouteType routeType;
    @Nullable
    private final String routeUrl;
    @Nullable
    private final String routeColor;
    @Nullable
    private final String routeTextColor;
    @Nullable
    private final Integer routeSortOrder;

    /**
     * @param routeId        identifies a route
     * @param agencyId       agency for the specified route
     * @param routeShortName short name of a route
     * @param routeLongName  full name of a route
     * @param routeDesc      Description of a route that provides useful, quality information.
     * @param routeType      Indicates the type of transportation used on a route
     * @param routeUrl       URL of a web page about the particular route
     * @param routeColor     Route color designation that matches public facing material.
     * @param routeTextColor Legible color to use for text drawn against a background of route_color.
     * @param routeSortOrder orders the routes in a way which is ideal for presentation to customers
     */
    private Route(@NotNull final String routeId,
                  @Nullable final String agencyId,
                  @Nullable final String routeShortName,
                  @Nullable final String routeLongName,
                  @Nullable final String routeDesc,
                  @NotNull final RouteType routeType,
                  @Nullable final String routeUrl,
                  @Nullable final String routeColor,
                  @Nullable final String routeTextColor,
                  @Nullable final Integer routeSortOrder) {
        this.routeId = routeId;
        this.agencyId = agencyId;
        this.routeShortName = routeShortName;
        this.routeLongName = routeLongName;
        this.routeDesc = routeDesc;
        this.routeType = routeType;
        this.routeUrl = routeUrl;
        this.routeColor = routeColor;
        this.routeTextColor = routeTextColor;
        this.routeSortOrder = routeSortOrder;
    }

    @NotNull
    public String getRouteId() {
        return routeId;
    }

    @Nullable
    public String getAgencyId() {
        return agencyId;
    }

    @Nullable
    public String getRouteShortName() {
        return routeShortName;
    }

    @Nullable
    public String getRouteLongName() {
        return routeLongName;
    }

    @Nullable
    public String getRouteDesc() {
        return routeDesc;
    }

    @NotNull
    public RouteType getRouteType() {
        return routeType;
    }

    @Nullable
    public String getRouteUrl() {
        return routeUrl;
    }

    @Nullable
    public String getRouteColor() {
        return routeColor;
    }

    @Nullable
    public String getRouteTextColor() {
        return routeTextColor;
    }

    @Nullable
    public Integer getRouteSortOrder() {
        return routeSortOrder;
    }

    /**
     * Builder class to create {@link Route} objects. Allows an unordered definition of the different attributes of
     * {@link Route}.
     */
    @SuppressWarnings("rawtypes")
    public static class RouteBuilder {
        private String routeId;
        private String agencyId;
        private String routeShortName;
        private String routeLongName;
        private String routeDesc;
        private RouteType routeType;
        private String routeUrl;
        private String routeColor;
        private String routeTextColor;
        private Integer routeSortOrder;
        private Integer originalRouteTypeInteger;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field routeId value and returns this
         *
         * @param routeId identifies a route
         * @return builder for future object creation
         */
        public RouteBuilder routeId(@NotNull final String routeId) {
            this.routeId = routeId;
            return this;
        }

        /**
         * Sets field agencyId value and returns this
         *
         * @param agencyId agency for the specified route
         * @return builder for future object creation
         */
        public RouteBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        /**
         * Sets field routeShortName value and returns this
         *
         * @param routeShortName short name of a route
         * @return builder for future object creation
         */
        public RouteBuilder routeShortName(@Nullable final String routeShortName) {
            this.routeShortName = routeShortName;
            return this;
        }

        /**
         * Sets field routeLongName value and returns this
         *
         * @param routeLongName full name of a route
         * @return builder for future object creation
         */
        public RouteBuilder routeLongName(@Nullable final String routeLongName) {
            this.routeLongName = routeLongName;
            return this;
        }

        /**
         * Sets field routeDesc value and returns this
         *
         * @param routeDesc Description of a route that provides useful, quality information.
         * @return builder for future object creation
         */
        public RouteBuilder routeDesc(@Nullable final String routeDesc) {
            this.routeDesc = routeDesc;
            return this;
        }

        /**
         * Sets fields routeType, originalRouteTypeInteger and returns this
         *
         * @param routeType Indicates the type of transportation used on a route
         * @return builder for future object creation
         */
        public RouteBuilder routeType(final Integer routeType) {
            this.routeType = RouteType.fromInt(routeType);
            this.originalRouteTypeInteger = routeType;
            return this;
        }

        /**
         * Sets field routeUrl value and returns this
         *
         * @param routeUrl URL of a web page about the particular route
         * @return builder for future object creation
         */
        public RouteBuilder routeUrl(@Nullable final String routeUrl) {
            this.routeUrl = routeUrl;
            return this;
        }

        /**
         * Sets field routeColor value and returns this
         *
         * @param routeColor Route color designation that matches public facing material.
         * @return builder for future object creation
         */
        public RouteBuilder routeColor(@Nullable final String routeColor) {
            this.routeColor = routeColor;
            return this;
        }

        /**
         * Sets field routeTextColor value and returns this
         *
         * @param routeTextColor Legible color to use for text drawn against a background of route_color.
         * @return builder for future object creation
         */
        public RouteBuilder routeTextColor(@Nullable final String routeTextColor) {
            this.routeTextColor = routeTextColor;
            return this;
        }

        /**
         * Sets field routeSortOrder value and returns this
         *
         * @param routeSortOrder orders the routes in a way which is ideal for presentation to customers
         * @return builder for future object creation
         */
        @SuppressWarnings("UnusedReturnValue")
        public RouteBuilder routeSortOrder(@Nullable final Integer routeSortOrder) {
            this.routeSortOrder = routeSortOrder;
            return this;
        }

        /**
         * Returns an entity representing a row from route.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns an entity representing a list of notices.
         *
         * @return entity representing a row from route.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns an entity representing a list of notices.
         */
        public EntityBuildResult<?> build() throws IllegalArgumentException {
            if (routeId == null || routeType == null || !(RouteType.isEnumValueValid(originalRouteTypeInteger)) ||
                    (agencyId != null && agencyId.isBlank()) ||
                    (!isPresentName(routeLongName) && !isPresentName(routeShortName))) {
                if (routeId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("routes.txt", "route_id",
                            routeId));
                }
                if (routeType == null) {
                    if (originalRouteTypeInteger == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("routes.txt", "route_type",
                                routeId));
                    } else {
                        noticeCollection.add(new UnexpectedEnumValueNotice("routes.txt", "route_type",
                                routeId, originalRouteTypeInteger));
                    }
                }
                if (agencyId != null && agencyId.isBlank()) {
                    noticeCollection.add(
                            new InvalidAgencyIdNotice("routes.txt", "agency_id", routeId));
                }
                // checks if at least one of name fields is provided
                if (!isPresentName(routeLongName) && !isPresentName(routeShortName)) {
                    noticeCollection.add(new MissingShortAndLongNameForRouteNotice("routes.txt", routeId));
                }
                return new EntityBuildResult(noticeCollection);
            } else {
                return new EntityBuildResult(new Route(routeId, agencyId, routeShortName, routeLongName, routeDesc,
                        routeType, routeUrl, routeColor, routeTextColor, routeSortOrder));
            }
        }

        /**
         * @param routeName a name of a Route
         * @return true if this Route name is not missing or empty, false if not.
         */
        private boolean isPresentName(final String routeName) {
            return routeName != null && !routeName.isBlank();
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null;
         */
        public RouteBuilder clear() {
            routeId = null;
            agencyId = null;
            routeShortName = null;
            routeLongName = null;
            routeDesc = null;
            routeType = null;
            routeUrl = null;
            routeColor = null;
            routeTextColor = null;
            routeSortOrder = null;
            originalRouteTypeInteger = null;
            noticeCollection.clear();
            return this;
        }
    }
}
