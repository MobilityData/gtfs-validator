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

public class Trip {

    @NotNull
    private final String routeId;
    @NotNull
    private final String serviceId;
    @NotNull
    private final String tripId;

    @Nullable
    private final String tripHeadsign;

    @Nullable
    private final String tripShortName;

    @Nullable
    private final Direction directionId;

    @Nullable
    private final String blockId;

    @Nullable
    private final String shapeId;

    private final WheelchairAccessibleStatus wheelchairAccessibleStatus;
    private final BikesAllowedStatus bikesAllowedStatus;

    private Trip(@NotNull final String routeId,
                 @NotNull final String serviceId,
                 @NotNull final String tripId,
                 @Nullable final String tripHeadsign,
                 @Nullable final String tripShortName,
                 @Nullable final Direction directionId,
                 @Nullable final String blockId,
                 @Nullable final String shapeId,
                 final WheelchairAccessibleStatus wheelchairAccessibleStatus,
                 final BikesAllowedStatus bikesAllowedStatus) {
        this.routeId = routeId;
        this.serviceId = serviceId;
        this.tripId = tripId;
        this.tripHeadsign = tripHeadsign;
        this.tripShortName = tripShortName;
        this.directionId = directionId;
        this.blockId = blockId;
        this.shapeId = shapeId;
        this.wheelchairAccessibleStatus = wheelchairAccessibleStatus;
        this.bikesAllowedStatus = bikesAllowedStatus;
    }

    @NotNull
    public String getRouteId() {
        return routeId;
    }

    @NotNull
    public String getServiceId() {
        return serviceId;
    }

    @NotNull
    public String getTripId() {
        return tripId;
    }

    @Nullable
    public String getTripHeadsign() {
        return tripHeadsign;
    }

    @Nullable
    public String getTripShortName() {
        return tripShortName;
    }

    @Nullable
    public Direction getDirectionId() {
        return directionId;
    }

    @Nullable
    public String getBlockId() {
        return blockId;
    }

    @Nullable
    public String getShapeId() {
        return shapeId;
    }

    public WheelchairAccessibleStatus getWheelchairAccessibleStatus() {
        return wheelchairAccessibleStatus;
    }

    public BikesAllowedStatus getBikesAllowedStatus() {
        return bikesAllowedStatus;
    }

    public static class TripBuilder {

        @NotNull
        private String routeId;
        @NotNull
        private String serviceId;
        @NotNull
        private String tripId;
        @Nullable
        private String tripHeadsign;
        @Nullable
        private String tripShortName;
        @Nullable
        private Direction directionId;
        @Nullable
        private String blockId;
        @Nullable
        private String shapeId;
        @Nullable
        private WheelchairAccessibleStatus wheelchairAccessibleStatus;
        @Nullable
        private BikesAllowedStatus bikesAllowedStatus;

        public TripBuilder(@NotNull final String routeId,
                           @NotNull final String serviceId,
                           @NotNull final String tripId) {
            this.routeId = routeId;
            this.serviceId = serviceId;
            this.tripId = tripId;
        }

        public TripBuilder routeId(@NotNull final String routeId) {
            this.routeId = routeId;
            return this;
        }

        public TripBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public TripBuilder tripId(@NotNull final String tripId) {
            this.tripId = tripId;
            return this;
        }

        public TripBuilder tripHeadsign(@Nullable final String tripHeadsign) {
            this.tripHeadsign = tripHeadsign;
            return this;
        }

        public TripBuilder tripShortName(@Nullable final String tripShortName) {
            this.tripShortName = tripShortName;
            return this;
        }

        public TripBuilder directionId(@Nullable final Direction directionId) {
            this.directionId = directionId;
            return this;
        }

        public TripBuilder blockId(@Nullable final String blockId) {
            this.blockId = blockId;
            return this;
        }

        public TripBuilder shapeId(@Nullable final String shapeId) {
            this.shapeId = shapeId;
            return this;
        }

        public TripBuilder wheelchairAccessible(@Nullable final WheelchairAccessibleStatus wheelchairAccessibleStatus) {
            this.wheelchairAccessibleStatus = wheelchairAccessibleStatus;
            return this;
        }

        public TripBuilder bikesAllowed(@Nullable final BikesAllowedStatus bikesAllowedStatus) {
            this.bikesAllowedStatus = bikesAllowedStatus;
            return this;
        }

        public Trip build() {
            return new Trip(routeId, serviceId, tripId, tripHeadsign, tripShortName, directionId, blockId,
                    shapeId, wheelchairAccessibleStatus, bikesAllowedStatus);
        }
    }
}
