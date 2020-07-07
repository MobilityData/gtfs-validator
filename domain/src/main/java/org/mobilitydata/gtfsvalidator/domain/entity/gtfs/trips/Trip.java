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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.WheelchairAccessibleStatus;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;

/**
 * Class for all entities defined in trips.txt. Can not be directly instantiated: user must use the
 * {@link TripBuilder} to create this.
 */
public class Trip extends GtfsEntity {
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
    @NotNull
    private final WheelchairAccessibleStatus wheelchairAccessibleStatus;
    @NotNull
    private final BikesAllowedStatus bikesAllowedStatus;

    /**
     * Class for all entities defined in trips.txt
     *
     * @param routeId                    identifies a route
     * @param serviceId                  identifies a set of dates when service is available for one or more routes
     * @param tripId                     identifies a trip
     * @param tripHeadsign               text that appears on signage identifying the trip's destination to riders
     * @param tripShortName              public facing text used to identify the trip to riders
     * @param directionId                indicates the direction of travel for a trip
     * @param blockId                    identifies the block to which the trip belongs
     * @param shapeId                    identifies a geospatial shape describing the vehicle travel path for a trip
     * @param wheelchairAccessibleStatus indicates wheelchair accessibility
     * @param bikesAllowedStatus         indicates whether bikes are allowed
     */
    private Trip(@NotNull final String routeId,
                 @NotNull final String serviceId,
                 @NotNull final String tripId,
                 @Nullable final String tripHeadsign,
                 @Nullable final String tripShortName,
                 @Nullable final DirectionId directionId,
                 @Nullable final String blockId,
                 @Nullable final String shapeId,
                 @NotNull final WheelchairAccessibleStatus wheelchairAccessibleStatus,
                 @NotNull final BikesAllowedStatus bikesAllowedStatus) {
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

    /**
     * Builder class to create {@code Trip} objects. Allows an unordered definition of the different attributes of
     * {@link Trip}.
     */
    public static class TripBuilder {
        private String routeId;
        private String serviceId;
        private String tripId;
        private String tripHeadsign;
        private String tripShortName;
        private DirectionId directionId;
        private String blockId;
        private String shapeId;
        private WheelchairAccessibleStatus wheelchairAccessibleStatus;
        private BikesAllowedStatus bikesAllowedStatus;
        private Integer originalWheelchairAccessibleStatusInteger;
        private Integer originalBikesAllowedStatusInteger;
        private Integer originalDirectionIdInteger;
        private final ArrayList<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field routeId value and returns this
         *
         * @param routeId identifies a route
         * @return builder for future object creation
         */
        public TripBuilder routeId(@NotNull final String routeId) {
            this.routeId = routeId;
            return this;
        }

        /**
         * Sets field serviceId value and returns this
         *
         * @param serviceId identifies a set of dates when service is available for one or more routes
         * @return builder for future object creation
         */
        public TripBuilder serviceId(@NotNull final String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        /**
         * Sets field tripId value and returns this
         *
         * @param tripId identifies a trip
         * @return builder for future object creation
         */
        public TripBuilder tripId(@NotNull final String tripId) {
            this.tripId = tripId;
            return this;
        }

        /**
         * Sets field tripHeadsign value and returns this
         *
         * @param tripHeadsign text that appears on signage identifying the trip's destination to riders
         * @return builder for future object creation
         */
        public TripBuilder tripHeadsign(@Nullable final String tripHeadsign) {
            this.tripHeadsign = tripHeadsign;
            return this;
        }

        /**
         * Sets field tripShortName value and returns this
         *
         * @param tripShortName public facing text used to identify the trip to riders
         * @return builder for future object creation
         */
        public TripBuilder tripShortName(@Nullable final String tripShortName) {
            this.tripShortName = tripShortName;
            return this;
        }

        /**
         * Sets fields directionId, originalDirectionIdInteger and returns this
         *
         * @param directionId indicates the direction of travel for a trip
         * @return builder for future object creation
         */
        public TripBuilder directionId(@Nullable final Integer directionId) {
            this.directionId = DirectionId.fromInt(directionId);
            this.originalDirectionIdInteger = directionId;
            return this;
        }

        /**
         * Sets field blockId and returns this
         *
         * @param blockId identifies the block to which the trip belongs
         * @return builder for future object creation
         */
        public TripBuilder blockId(@Nullable final String blockId) {
            this.blockId = blockId;
            return this;
        }

        /**
         * Sets field shapeId and returns this
         *
         * @param shapeId identifies a geospatial shape describing the vehicle travel path for a trip
         * @return builder for future object creation
         */
        public TripBuilder shapeId(@Nullable final String shapeId) {
            this.shapeId = shapeId;
            return this;
        }

        /**
         * Sets fields wheelchairAccessibleStatus, originalWheelchairAccessibleStatusInteger and returns this
         *
         * @param wheelchairAccessibleStatus indicates wheelchair accessibility
         * @return builder for future object creation
         */
        public TripBuilder wheelchairAccessible(@Nullable final Integer wheelchairAccessibleStatus) {
            this.wheelchairAccessibleStatus = WheelchairAccessibleStatus.fromInt(wheelchairAccessibleStatus);
            this.originalWheelchairAccessibleStatusInteger = wheelchairAccessibleStatus;
            return this;
        }

        /**
         * Sets fields bikesAllowedStatus, originalBikesAllowedStatusInteger  and returns this
         *
         * @param bikesAllowedStatus indicates whether bikes are allowed
         * @return builder for future object creation
         */
        public TripBuilder bikesAllowed(@Nullable final Integer bikesAllowedStatus) {
            this.bikesAllowedStatus = BikesAllowedStatus.fromInt(bikesAllowedStatus);
            this.originalBikesAllowedStatusInteger = bikesAllowedStatus;
            return this;
        }

        /**
         * Returns {@code EntityBuildResult} representing a row from trips.txt if the requirements from the official
         * GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues.
         *
         * @return {@code EntityBuildResult} representing a row from trips.txt if the requirements from the official
         * GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues.
         */
        public EntityBuildResult<?> build() {
            if (routeId == null || serviceId == null || tripId == null ||
                    !DirectionId.isEnumValueValid(originalDirectionIdInteger) ||
                    !WheelchairAccessibleStatus.isEnumValueValid(originalWheelchairAccessibleStatusInteger) ||
                    !BikesAllowedStatus.isEnumValueValid(originalBikesAllowedStatusInteger)) {

                if (routeId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "route_id",
                            tripId));
                }
                if (serviceId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "service_id",
                            tripId));
                }
                if (tripId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("trips.txt", "trip_id",
                            tripId));
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
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Trip(routeId, serviceId, tripId, tripHeadsign, tripShortName,
                        directionId, blockId, shapeId, wheelchairAccessibleStatus, bikesAllowedStatus));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         * @return builder with all fields set to null;
         */
        public TripBuilder clear() {
            routeId = null;
            serviceId = null;
            tripId = null;
            tripHeadsign = null;
            tripShortName = null;
            directionId = null;
            blockId = null;
            shapeId = null;
            wheelchairAccessibleStatus = null;
            bikesAllowedStatus = null;
            originalWheelchairAccessibleStatusInteger = null;
            originalBikesAllowedStatusInteger = null;
            originalDirectionIdInteger = null;
            noticeCollection.clear();
            return this;
        }
    }
}