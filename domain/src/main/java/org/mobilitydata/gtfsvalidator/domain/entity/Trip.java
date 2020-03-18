package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Trip {

    @NotNull
    final String routeId;
    @NotNull
    final String serviceId;
    @NotNull
    final String tripId;

    private final String tripHeadsign;
    private final String tripShortName;
    private final Direction directionId;
    private final String blockId;
    private final String shapeId;
    private final WheelchairAccessibleStatus wheelchairAccessibleStatus;
    private final BikesAllowedStatus bikesAllowedStatus;

    public Trip(@NotNull String routeId, @NotNull String serviceId, @NotNull String tripId, String tripHeadsign,
                String tripShortName, Direction directionId, String blockId, String shapeId,
                WheelchairAccessibleStatus wheelchairAccessibleStatus, BikesAllowedStatus bikesAllowedStatus) {
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

    public String getTripHeadsign() {
        return tripHeadsign;
    }

    public String getTripShortName() {
        return tripShortName;
    }

    public Direction getDirectionId() {
        return directionId;
    }

    public String getBlockId() {
        return blockId;
    }

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

        private String routeId;
        private String serviceId;
        private String tripId;
        private String tripHeadsign;
        private String tripShortName;
        private Direction directionId;
        private String blockId;
        private String shapeId;
        private WheelchairAccessibleStatus wheelchairAccessibleStatus;
        private BikesAllowedStatus bikesAllowedStatus;

        public TripBuilder routeId(@NotNull String routeId) {
            this.routeId = routeId;
            return this;
        }

        public TripBuilder serviceId(@NotNull String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public TripBuilder tripId(@NotNull String tripId) {
            this.tripId = tripId;
            return this;
        }

        public TripBuilder tripHeadsign(@Nullable String tripHeadsign) {
            this.tripHeadsign = tripHeadsign;
            return this;
        }

        public TripBuilder tripShortName(@Nullable String tripShortName) {
            this.tripShortName = tripShortName;
            return this;
        }

        public TripBuilder directionId(@Nullable Direction directionId) {
            this.directionId = directionId;
            return this;
        }

        public TripBuilder blockId(@Nullable String blockId) {
            this.blockId = blockId;
            return this;
        }

        public TripBuilder shapeId(@Nullable String shapeId) {
            this.shapeId = shapeId;
            return this;
        }

        public TripBuilder wheelchairAccessible(@Nullable WheelchairAccessibleStatus wheelchairAccessibleStatus) {
            this.wheelchairAccessibleStatus = wheelchairAccessibleStatus;
            return this;
        }

        public TripBuilder bikesAllowed(@Nullable BikesAllowedStatus bikesAllowedStatus) {
            this.bikesAllowedStatus = bikesAllowedStatus;
            return this;
        }

        public Trip build() {
            return new Trip(routeId, serviceId, tripId, tripHeadsign, tripShortName, directionId, blockId,
                    shapeId, wheelchairAccessibleStatus, bikesAllowedStatus);
        }
    }
}
