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

package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Entity representing a row from stop_times.txt.
 * See http://gtfs.org/reference/static#stop_timestxt
 * <p>
 * This class can not be directly instantiated. User must use {@link StopTimeBuilder} to create a {@link StopTime}
 * object.
 */
public class StopTime {

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

    @NotNull
    private final Timepoint timePoint;

    private StopTime(@NotNull String tripId,
                     @Nullable String arrivalTime,
                     @Nullable String departureTime,
                     @NotNull String stopId,
                     int stopSequence,
                     @Nullable String stopHeadsign,
                     @NotNull PickupType pickupType,
                     @NotNull DropOffType dropOffType,
                     @Nullable Float shapeDistTraveled,
                     @NotNull Timepoint timepoint) {
        this.tripId = tripId;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopId = stopId;
        this.stopSequence = stopSequence;
        this.stopHeadsign = stopHeadsign;
        this.pickupType = pickupType;
        this.dropOffType = dropOffType;
        this.shapeDistTraveled = shapeDistTraveled;
        this.timePoint = timepoint;
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

    @NotNull
    public Timepoint getTimePoint() {
        return timePoint;
    }

    /**
     * Builder class to create {@link StopTime} objects.
     */
    public static class StopTimeBuilder {

        private String tripId;
        private String arrivalTime;
        private String departureTime;
        private String stopId;
        private int stopSequence;
        private String stopHeadsign;
        private PickupType pickupType;
        private DropOffType dropOffType;
        private Float shapeDistTraveled;
        private Timepoint timepoint;

        /**
         * Builder class constructor. Throws {@link IllegalArgumentException} in case one of the required parameters
         * is null.
         *
         * @param tripId        identifies a trip
         * @param arrivalTime   arrival time at a specific stop for a specific trip on a route
         * @param departureTime departure time at a specific stop for a specific trip on a route
         * @param stopId        identifies the serviced stop
         * @param stopSequence  order of stops for a particular trip
         * @param timepoint     Indicates if arrival and departure times for a stop are strictly adhered to by the
         *                      vehicle or if they are instead approximate and/or interpolated times
         * @throws IllegalArgumentException if parameters annotated `@NotNull` are null
         */
        public StopTimeBuilder(@NotNull final String tripId,
                               @Nullable final String arrivalTime,
                               @Nullable final String departureTime,
                               @NotNull final String stopId,
                               final int stopSequence,
                               @NotNull final Timepoint timepoint) throws IllegalArgumentException {

            this.tripId = tripId;
            this.arrivalTime = arrivalTime;
            this.departureTime = departureTime;
            this.stopId = stopId;
            this.stopSequence = stopSequence;
        }

        /**
         * Sets field tripId value and returns this
         *
         * @param tripId identifies a trip
         * @return builder for future object creation
         */
        public StopTimeBuilder tripId(@NotNull final String tripId) {
            this.tripId = tripId;
            return this;
        }

        /**
         * Sets field arrivalTime value and returns this
         *
         * @param arrivalTime arrival time at a specific stop for a specific trip on a route
         * @return builder for future object creation
         */
        public StopTimeBuilder arrivalTime(@Nullable final String arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        /**
         * Sets field departureTime value and returns this
         *
         * @param departureTime departure time at a specific stop for a specific trip on a route
         * @return builder for future object creation
         */
        public StopTimeBuilder departureTime(@Nullable final String departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        /**
         * Sets field stopId value and returns this
         *
         * @param stopId identifies the serviced stop
         * @return builder for future object creation
         */
        public StopTimeBuilder stopId(@NotNull final String stopId) {
            this.stopId = stopId;
            return this;
        }

        /**
         * Sets field stopSequence value and returns this
         *
         * @param stopSequence order of stops for a particular trip
         * @return builder for future object creation
         */
        public StopTimeBuilder stopSequence(final int stopSequence) {
            this.stopSequence = stopSequence;
            return this;
        }

        /**
         * Sets field stopHeadsign value and returns this
         *
         * @param stopHeadsign text that appears on signage identifying the trip's destination to riders
         * @return builder for future object creation
         */
        public StopTimeBuilder stopHeadsign(@NotNull final String stopHeadsign) {
            this.stopHeadsign = stopHeadsign;
            return this;
        }

        /**
         * Sets field pickupType value and returns this
         *
         * @param pickupType Indicates drop off method.
         * @return builder for future object creation
         */
        public StopTimeBuilder pickupType(@Nullable final Integer pickupType) {
            this.pickupType = PickupType.fromInt(pickupType);
            return this;
        }

        /**
         * Sets field dropOffType value and returns this
         *
         * @param dropOffType Indicates drop off method.
         * @return builder for future object creation
         */
        public StopTimeBuilder dropOffType(@Nullable final Integer dropOffType) {
            this.dropOffType = DropOffType.fromInt(dropOffType);
            return this;
        }

        /**
         * Sets field shapeDistTraveled value and returns this
         *
         * @param shapeDistTraveled actual distance traveled along the associated shape
         * @return builder for future object creation
         */
        public StopTimeBuilder shapeDistTraveled(@Nullable final Float shapeDistTraveled) {
            this.shapeDistTraveled = shapeDistTraveled;
            return this;
        }

        /**
         * Sets field timepoint value and returns this
         *
         * @param timepoint Indicates if arrival and departure times for a stop are strictly adhered to by the
         *                  vehicle or if they are instead approximate and/or interpolated times
         * @return builder for future object creation
         */
        public StopTimeBuilder timepoint(@NotNull final Timepoint timepoint) {
            this.timepoint = timepoint;
            return this;
        }

        /**
         * Creates a {@link StopTime} objects from fields provided via {@link StopTimeBuilder} methods. Throws
         * {@link IllegalArgumentException} if fields arrivalTime, departureTime, are not defined when field timepoint
         * is set as {@link Timepoint#EXACT_TIMES}
         *
         * @return Entity representing a row from stop_times.txt
         * @throws IllegalArgumentException if fields arrivalTime, departureTime, are not defined when field timepoint
         *                                  is set as {@link Timepoint#EXACT_TIMES}
         */
        public StopTime build() {
            if (tripId == null) {
                throw new IllegalArgumentException("null value for tripId");
            }
            if (stopId == null) {
                throw new IllegalArgumentException("null value for stopId");
            }
            if (timepoint == Timepoint.EXACT_TIMES) {
                if (arrivalTime == null) {
                    throw new IllegalArgumentException("arrivalTime can not be null for timepoint");
                }
                if (departureTime == null) {
                    throw new IllegalArgumentException("departureTime can not be null for timepoint");
                }
            }
            return new StopTime(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign, pickupType,
                    dropOffType, shapeDistTraveled, timepoint);
        }
    }
}