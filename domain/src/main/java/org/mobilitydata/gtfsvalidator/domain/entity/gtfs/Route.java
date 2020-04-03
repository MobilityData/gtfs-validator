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

        public RouteBuilder routeId(@NotNull final String routeId) {
            this.routeId = routeId;
            return this;
        }

        public RouteBuilder agencyId(@Nullable final String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public RouteBuilder routeShortName(@Nullable final String routeShortName) {
            this.routeShortName = routeShortName;
            return this;
        }

        public RouteBuilder routeLongName(@Nullable final String routeLongName) {
            this.routeLongName = routeLongName;
            return this;
        }

        public RouteBuilder routeDesc(@Nullable final String routeDesc) {
            this.routeDesc = routeDesc;
            return this;
        }

        public RouteBuilder routeType(final int routeType) {
            this.routeType = RouteType.fromInt(routeType);
            return this;
        }

        public RouteBuilder routeUrl(@Nullable final String routeUrl) {
            this.routeUrl = routeUrl;
            return this;
        }

        public RouteBuilder routeColor(@Nullable final String routeColor) {
            this.routeColor = routeColor;
            return this;
        }

        public RouteBuilder routeTextColor(@Nullable final String routeTextColor) {
            this.routeTextColor = routeTextColor;
            return this;
        }

        public RouteBuilder routeSortOrder(@Nullable final Integer routeSortOrder) {
            this.routeSortOrder = routeSortOrder;
            return this;
        }

        public Route build() throws NullPointerException {

            if (routeId == null) {
                throw new NullPointerException("route_id can not be null in routes.txt");
            }
            return new Route(routeId, agencyId, routeShortName, routeLongName, routeDesc, routeType,
                    routeUrl, routeColor, routeTextColor, routeSortOrder);
        }
    }
}