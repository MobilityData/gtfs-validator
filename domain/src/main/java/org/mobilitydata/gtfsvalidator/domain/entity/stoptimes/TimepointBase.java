package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class TimepointBase {

    @NotNull
    private final String tripId;

    @Nullable
    private final String arrivalTime;

    @Nullable
    private final String departureTime;

    @NotNull
    private final String stopId;

    private final int stopSequence;

    @Nullable
    private final String stopHeadsign;

    @NotNull
    private final PickupType pickupType;

    @NotNull
    private final DropOffType dropOffType;

    @Nullable
    private final Float shapeDistTraveled;

    protected TimepointBase(@NotNull String tripId,
                            @Nullable String arrivalTime,
                            @Nullable String departureTime,
                            @NotNull String stopId,
                            int stopSequence,
                            @Nullable String stopHeadsign,
                            @NotNull PickupType pickupType,
                            @NotNull DropOffType dropOffType,
                            @Nullable Float shapeDistTraveled) {
        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.pickupType = pickupType;
        this.dropOffType = dropOffType;
        this.shapeDistTraveled = shapeDistTraveled;
    }

    @NotNull
    public String getTripId() {
        return tripId;
    }

    @Nullable
    public String getArrivalTime() {
        return arrivalTime;
    }

    @Nullable
    public String getDepartureTime() {
        return departureTime;
    }

    @NotNull
    public String getStopId() {
        return stopId;
    }

    public int getStopSequence() {
        return stopSequence;
    }

    @Nullable
    public String getStopHeadsign() {
        return stopHeadsign;
    }

    @NotNull
    public PickupType getPickupType() {
        return pickupType;
    }

    @NotNull
    public DropOffType getDropOffType() {
        return dropOffType;
    }

    @Nullable
    public Float getShapeDistTraveled() {
        return shapeDistTraveled;
    }

    public static abstract class TimepointBaseBuilder {

        protected String tripId;
        protected String arrivalTime;
        protected String departureTime;
        protected String stopId;
        protected int stopSequence;
        protected String stopHeadsign;
        protected PickupType pickupType;
        protected DropOffType dropOffType;
        protected Float shapeDistTraveled;

        public TimepointBaseBuilder(final @NotNull String tripId,
                                    final @Nullable String arrivalTime,
                                    final @Nullable String departureTime,
                                    final @NotNull String stopId,
                                    final int stopSequence) throws IllegalArgumentException {

            //noinspection ConstantConditions
            if (tripId == null) {
                throw new IllegalArgumentException("null value for tripId");
            }
            //noinspection ConstantConditions
            if (stopId == null) {
                throw new IllegalArgumentException("null value for stopId");
            }

            this.tripId = tripId;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.stopId = stopId;
            this.stopSequence = stopSequence;
        }

        public TimepointBaseBuilder tripId(@NotNull final String tripId) {
            this.tripId = tripId;
            return this;
        }

        public TimepointBaseBuilder arrivalTime(@Nullable final String arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        public TimepointBaseBuilder departureTime(@Nullable final String departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public TimepointBaseBuilder stopId(@NotNull final String stopId) {
            this.stopId = stopId;
            return this;
        }

        public TimepointBaseBuilder stopSequence(final int stopSequence) {
            this.stopSequence = stopSequence;
            return this;
        }

        public TimepointBaseBuilder stopHeadsign(@NotNull final String stopHeadsign) {
            this.stopHeadsign = stopHeadsign;
            return this;
        }

        public TimepointBaseBuilder pickupType(@Nullable final Integer pickupType) {
            this.pickupType = PickupType.fromInt(pickupType);
            return this;
        }

        public TimepointBaseBuilder dropOffType(@Nullable final Integer dropOffType) {
            this.dropOffType = DropOffType.fromInt(dropOffType);
            return this;
        }

        public TimepointBaseBuilder shapeDistTraveled(@Nullable final Float shapeDistTraveled) {
            this.shapeDistTraveled = shapeDistTraveled;
            return this;
        }
    }
}