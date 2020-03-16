package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Trip {

    @NotNull
    final String route_id;
    @NotNull
    final String service_id;
    @NotNull
    final String trip_id;

    private final String trip_headsign;
    private final String trip_short_name;
    private final Direction direction_id;
    private final String block_id;
    private final String shape_id;
    private final WheelchairAccessibleStatus wheelchair_accessible;
    private final BikesAllowedStatus bikes_allowed;

    public Trip(@NotNull String route_id, @NotNull String service_id, @NotNull String trip_id, String trip_headsign,
                String trip_short_name, Direction direction_id, String block_id, String shape_id,
                WheelchairAccessibleStatus wheelchair_accessible, BikesAllowedStatus bikes_allowed) {
        this.route_id = route_id;
        this.service_id = service_id;
        this.trip_id = trip_id;
        this.trip_headsign = trip_headsign;
        this.trip_short_name = trip_short_name;
        this.direction_id = direction_id;
        this.block_id = block_id;
        this.shape_id = shape_id;
        this.wheelchair_accessible = wheelchair_accessible;
        this.bikes_allowed = bikes_allowed;
    }


    public String getRoute_id() {
        return route_id;
    }

    public String getService_id() {
        return service_id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public String getTrip_short_name() {
        return trip_short_name;
    }

    public Direction getDirection_id() {
        return direction_id;
    }

    public String getBlock_id() {
        return block_id;
    }

    public String getShape_id() {
        return shape_id;
    }

    public WheelchairAccessibleStatus getWheelchair_accessible() {
        return wheelchair_accessible;
    }

    public BikesAllowedStatus getBikes_allowed() {
        return bikes_allowed;
    }

    public class TripBuilder {

        private String route_id;
        private String service_id;
        private String trip_id;
        private String trip_headsign;
        private String trip_short_name;
        private Direction direction_id;
        private String block_id;
        private String shape_id;
        private WheelchairAccessibleStatus wheelchair_accessible;
        private BikesAllowedStatus bikes_allowed;

        public TripBuilder routeId(@NotNull String route_id) {
            this.route_id = route_id;
            return this;
        }

        public TripBuilder serviceId(@NotNull String service_id) {
            this.service_id = service_id;
            return this;
        }

        public TripBuilder tripId(@NotNull String trip_id) {
            this.trip_id = trip_id;
            return this;
        }

        public TripBuilder tripHeadsign(@Nullable String trip_headsign) {
            this.trip_headsign = trip_headsign;
            return this;
        }

        public TripBuilder tripShortName(@Nullable String trip_short_name) {
            this.trip_short_name = trip_short_name;
            return this;
        }

        public TripBuilder directionId(@Nullable Direction direction_id) {
            this.direction_id = direction_id;
            return this;
        }

        public TripBuilder blockId(@Nullable String block_id) {
            this.block_id = block_id;
            return this;
        }

        public TripBuilder shapeId(@Nullable String shape_id) {
            this.shape_id = shape_id;
            return this;
        }

        public TripBuilder wheelchairAccessible(@Nullable WheelchairAccessibleStatus wheelchair_accessible) {
            this.wheelchair_accessible = wheelchair_accessible;
            return this;
        }

        public TripBuilder bikesAllowed(@Nullable BikesAllowedStatus bikes_allowed) {
            this.bikes_allowed = bikes_allowed;
            return this;
        }

        public Trip build() {
            return new Trip(route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, block_id,
                    shape_id, wheelchair_accessible, bikes_allowed);
        }
    }
}
