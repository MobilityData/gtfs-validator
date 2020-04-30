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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.BikesAllowedStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.WheelchairAccessibleStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;

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
    private final DirectionId directionId;

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
                 @Nullable final DirectionId directionId,
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
    public DirectionId getDirectionId() {
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

    @NotNull
    public WheelchairAccessibleStatus getWheelchairAccessibleStatus() {
        return wheelchairAccessibleStatus;
    }

    @NotNull
    public BikesAllowedStatus getBikesAllowedStatus() {
        return bikesAllowedStatus;
    }

    public static class TripBuilder {
        private String routeId;
        private String serviceId;
        private String tripId;

        @NotNull
        private final ArrayList<Notice> noticeCollection;

        public TripBuilder(@NotNull final ArrayList<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
        }

        @Nullable
        private String tripHeadsign;

        @Nullable
        private String tripShortName;

        @Nullable
        private DirectionId directionId;

        @Nullable
        private String blockId;

        @Nullable
        private String shapeId;

        @Nullable
        private WheelchairAccessibleStatus wheelchairAccessibleStatus;

        @Nullable
        private BikesAllowedStatus bikesAllowedStatus;

        @Nullable
        private Integer originalWheelchairAccessibleStatusInteger;
        @Nullable
        private Integer originalBikesAllowedStatusInteger;
        @Nullable
        private Integer originalDirectionIdInteger;

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

        public TripBuilder directionId(@Nullable final Integer directionId) {
            this.directionId = DirectionId.fromInt(directionId);
            this.originalDirectionIdInteger = directionId;
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

        public TripBuilder wheelchairAccessible(@NotNull final Integer wheelchairAccessibleStatus) {
            this.wheelchairAccessibleStatus = WheelchairAccessibleStatus.fromInt(wheelchairAccessibleStatus);
            this.originalWheelchairAccessibleStatusInteger = wheelchairAccessibleStatus;
            return this;
        }

        public TripBuilder bikesAllowed(@NotNull final Integer bikesAllowedStatus) {
            this.bikesAllowedStatus = BikesAllowedStatus.fromInt(bikesAllowedStatus);
            this.originalBikesAllowedStatusInteger = bikesAllowedStatus;
            return this;
        }

        public EntityBuildResult<?> build() {
            noticeCollection.clear();

            if (routeId == null || serviceId == null || tripId == null ||
                    !DirectionId.isEnumValueValid(originalDirectionIdInteger) ||
                    !WheelchairAccessibleStatus.isEnumValueValid(originalWheelchairAccessibleStatusInteger) ||
                    !BikesAllowedStatus.isEnumValueValid(originalBikesAllowedStatusInteger)) {

                if (routeId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "route_id", tripId));
                }
                if (serviceId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "service_id",
                            tripId));
                }
                if (tripId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "trip_id", tripId));
                }
                if (!DirectionId.isEnumValueValid(originalDirectionIdInteger)) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("trips.txt",
                            "direction_id", tripId, originalDirectionIdInteger));
                }
                if (!WheelchairAccessibleStatus.isEnumValueValid(originalWheelchairAccessibleStatusInteger)) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("trips.txt",
                            "wheelchair_accessible", tripId, originalWheelchairAccessibleStatusInteger));
                }
                if (!BikesAllowedStatus.isEnumValueValid(originalBikesAllowedStatusInteger)) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("trips.txt", "bikes_allowed",
                            tripId, originalBikesAllowedStatusInteger));
                }
                return new EntityBuildResult<>(noticeCollection, EntityBuildResult.Status.FAILURE);
            } else {
                return new EntityBuildResult<>(new Trip(routeId, serviceId, tripId, tripHeadsign, tripShortName,
                        directionId, blockId, shapeId, wheelchairAccessibleStatus, bikesAllowedStatus),
                        EntityBuildResult.Status.SUCCESS);
            }
        }
    }
}
