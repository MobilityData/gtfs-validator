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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ContinuousDropOff;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ContinuousPickup;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IllegalFieldValueCombinationNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a row from stop_times.txt.
 * See http://gtfs.org/reference/static#stop_timestxt
 * <p>
 * This class can not be directly instantiated. User must use {@link StopTimeBuilder} to create a {@link StopTime}
 * object.
 */
public class StopTime extends GtfsEntity implements Comparable<StopTime> {
    @NotNull
    private final String tripId;
    @Nullable
    private final Integer arrivalTime;
    @Nullable
    private final Integer departureTime;
    @NotNull
    private final String stopId;
    @NotNull
    private final Integer stopSequence;
    @Nullable
    private final String stopHeadsign;
    @NotNull
    private final PickupType pickupType;
    @NotNull
    private final DropOffType dropOffType;
    @NotNull
    private final ContinuousPickup continuousPickup;
    @NotNull
    private final ContinuousDropOff continuousDropOff;
    @Nullable
    private final Float shapeDistTraveled;
    @NotNull
    private final Timepoint timePoint;

    /**
     * @param tripId            identifies a trip
     * @param arrivalTime       arrival time at a specific stop for a specific trip on a route
     * @param departureTime     departure time at a specific stop for a specific trip on a route
     * @param stopId            identifies the serviced stop
     * @param stopSequence      order of stops for a particular trip
     * @param stopHeadsign      text that appears on signage identifying the trip's destination to riders
     * @param pickupType        indicates pickup method
     * @param dropOffType       indicates drop off method.
     * @param continuousPickup  indicates that the rider can board the transit vehicle at any point along the
     *                          vehicle’s travel path as described by shapes.txt, from this {@link StopTime} to the
     *                          next {@link StopTime} in the trip’s stop_sequence
     * @param continuousDropOff indicates that the rider can alight the transit vehicle at any point along the
     *                          vehicle’s travel path as described by shapes.txt, from this {@link StopTime} to the
     *                          next {@link StopTime} in the trip’s stop_sequence
     * @param shapeDistTraveled actual distance traveled along the associated shape
     * @param timepoint         indicates if arrival and departure times for a stop are strictly adhered to by the
     *                          vehicle or if they are instead approximate and/or interpolated times
     */
    private StopTime(@NotNull String tripId,
                     @Nullable Integer arrivalTime,
                     @Nullable Integer departureTime,
                     @NotNull String stopId,
                     @NotNull Integer stopSequence,
                     @Nullable String stopHeadsign,
                     @NotNull PickupType pickupType,
                     @NotNull DropOffType dropOffType,
                     @NotNull ContinuousPickup continuousPickup,
                     @NotNull ContinuousDropOff continuousDropOff,
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
        this.continuousPickup = continuousPickup;
        this.continuousDropOff = continuousDropOff;
        this.shapeDistTraveled = shapeDistTraveled;
        this.timePoint = timepoint;
    }

    @NotNull
    public String getTripId() {
        return tripId;
    }

    @Nullable
    public Integer getArrivalTime() {
        return arrivalTime;
    }

    @Nullable
    public Integer getDepartureTime() {
        return departureTime;
    }

    @NotNull
    public String getStopId() {
        return stopId;
    }

    @NotNull
    public Integer getStopSequence() {
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

    @NotNull
    public ContinuousPickup getContinuousPickup() {
        return continuousPickup;
    }

    @NotNull
    public ContinuousDropOff getContinuousDropOff() {
        return continuousDropOff;
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
     * Implement compareTo method from {@code Comparable}. Return the value {@code 0} if this {@code StopTime} field
     * stop_sequence is equal to field stop_sequence of argument; a value less than {@code 0} if this field
     * stop_sequence is numerically less than field stop_sequence of argument; and a value greater
     * than {@code 0} if this field stop_sequence is numerically greater than field stop_sequence of argument
     *
     * @param stopTime stop_time to compare to
     * @return the value {@code 0} if this {@link StopTime} field stop_sequence is equal to field
     * stop_sequence of argument; a value less than {@code 0} if this field stop_sequence is numerically less
     * than field stop_sequence of argument; and a value greater than {@code 0} if this field stop_sequence is
     * numerically greater than field stop_sequence of argument
     */
    @Override
    public int compareTo(@NotNull final StopTime stopTime) {
        return getStopSequence().compareTo(stopTime.getStopSequence());
    }

    /**
     * Return true if this {@link StopTime} has field stop_sequence greater than {@param otherStopTime}
     * field stop_sequence; otherwise return false
     *
     * @param otherStopTime stop_time to compare
     * @return true if this {@link StopTime} has field stop_sequence greater than {@param otherStopTime}
     * field stop_sequence; otherwise return false
     */
    public boolean isGreaterThan(final StopTime otherStopTime) {
        return compareTo(otherStopTime) > 0;
    }

    /**
     * Builder class to create {@link StopTime} objects.
     */
    public static class StopTimeBuilder {
        private String tripId;
        private Integer arrivalTime;
        private Integer departureTime;
        private String stopId;
        private Integer stopSequence;
        private String stopHeadsign;
        private PickupType pickupType;
        private Integer originalPickupType;
        private DropOffType dropOffType;
        private Integer originalDropOffType;
        private ContinuousPickup continuousPickup;
        private Integer originalContinuousPickup;
        private ContinuousDropOff continuousDropOff;
        private Integer originalContinuousDropOff;
        private Float shapeDistTraveled;
        private Timepoint timepoint;
        private Integer originalTimepoint;
        private final List<Notice> noticeCollection = new ArrayList<>();

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
        public StopTimeBuilder arrivalTime(@Nullable final Integer arrivalTime) {
            this.arrivalTime = arrivalTime;
            return this;
        }

        /**
         * Sets field departureTime value and returns this
         *
         * @param departureTime departure time at a specific stop for a specific trip on a route
         * @return builder for future object creation
         */
        public StopTimeBuilder departureTime(@Nullable final Integer departureTime) {
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
        public StopTimeBuilder stopSequence(final Integer stopSequence) {
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
         * @param pickupType indicates pickup method.
         * @return builder for future object creation
         */
        public StopTimeBuilder pickupType(@Nullable final Integer pickupType) {
            this.pickupType = PickupType.fromInt(pickupType);
            this.originalPickupType = pickupType;
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
            this.originalDropOffType = dropOffType;
            return this;
        }

        /**
         * Sets field continuousPickup value and returns this
         *
         * @param continuousPickup indicates that the rider can board the transit vehicle at any point along the
         *                         vehicle’s travel path as described by shapes.txt, from this {@link StopTime} to the
         *                         next {@link StopTime} in the trip’s stop_sequence
         * @return builder for future object creation
         */
        public StopTimeBuilder continuousPickup(@Nullable final Integer continuousPickup) {
            this.continuousPickup = ContinuousPickup.fromInt(continuousPickup);
            this.originalContinuousPickup = continuousPickup;
            return this;
        }

        /**
         * @param continuousDropOff indicates that the rider can alight the transit vehicle at any point along the
         *                          vehicle’s travel path as described by shapes.txt, from this {@link StopTime} to the
         *                          next {@link StopTime} in the trip’s stop_sequence
         * @return builder for future object creation
         */
        public StopTimeBuilder continuousDropOff(@Nullable final Integer continuousDropOff) {
            this.continuousDropOff = ContinuousDropOff.fromInt(continuousDropOff);
            this.originalContinuousDropOff = continuousDropOff;
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
        public StopTimeBuilder timepoint(@NotNull final Integer timepoint) {
            this.timepoint = Timepoint.fromInt(timepoint);
            this.originalTimepoint = timepoint;
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
        public EntityBuildResult<?> build() {
            if (tripId == null || stopId == null ||
                    (timepoint == Timepoint.EXACT_TIMES && (arrivalTime == null || departureTime == null)) ||
                    pickupType == null || dropOffType == null || continuousPickup == null ||
                    continuousDropOff == null || (shapeDistTraveled != null && shapeDistTraveled < 0) ||
                    timepoint == null || stopSequence == null) {
                if (tripId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stop_times.txt", "trip_id",
                            "trip_id", "stop_sequence", tripId, stopSequence));
                }
                if (timepoint == Timepoint.EXACT_TIMES) {
                    if (arrivalTime == null) {
                        noticeCollection.add((new IllegalFieldValueCombinationNotice("stop_times.txt",
                                "arrival_time", "timepoint", "trip_id",
                                "stop_sequence", tripId, stopSequence)));
                    }
                    if (departureTime == null) {
                        noticeCollection.add((new IllegalFieldValueCombinationNotice("stop_times.txt",
                                "departure_time", "timepoint",
                                "trip_id", "stop_sequence", tripId,
                                stopSequence)));

                    }
                }
                if (stopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stop_times.txt", "stop_id",
                            "trip_id", "stop_sequence", tripId, stopSequence));
                }
                if (stopSequence == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("stop_times.txt",
                            "stop_sequence", "trip_id",
                            "stop_sequence", tripId, stopSequence));
                }
                if (pickupType == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stop_times.txt", "pickup_type",
                            originalPickupType, "trip_id", "stop_sequence",
                            tripId, stopSequence));
                }
                if (dropOffType == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stop_times.txt", "drop_off_type",
                            originalDropOffType, "trip_id", "stop_sequence",
                            tripId, stopSequence));
                }
                if (continuousPickup == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stop_times.txt",
                            "continuous_pickup", originalContinuousPickup, "trip_id",
                            "stop_sequence", tripId, stopSequence));
                }
                if (continuousDropOff == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stop_times.txt",
                            "continuous_drop_off", originalContinuousDropOff, "trip_id",
                            "stop_sequence", tripId, stopSequence));
                }
                if (shapeDistTraveled != null && shapeDistTraveled < 0) {
                    noticeCollection.add(new FloatFieldValueOutOfRangeNotice("stop_times.txt",
                            "shape_dist_traveled", 0, Float.MAX_VALUE, shapeDistTraveled,
                            "trip_id", "stop_sequence", tripId, stopSequence));
                }
                if (timepoint == null) {
                    noticeCollection.add(new UnexpectedEnumValueNotice("stop_times.txt", "timepoint",
                            originalTimepoint, "trip_id", "stop_sequence",
                            tripId, stopSequence));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new StopTime(tripId, arrivalTime, departureTime, stopId, stopSequence,
                        stopHeadsign, pickupType, dropOffType, continuousPickup, continuousDropOff, shapeDistTraveled,
                        timepoint));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null;
         */
        public StopTimeBuilder clear() {
            tripId = null;
            arrivalTime = null;
            departureTime = null;
            stopId = null;
            stopSequence = null;
            stopHeadsign = null;
            pickupType = null;
            originalPickupType = null;
            dropOffType = null;
            originalDropOffType = null;
            continuousPickup = null;
            originalContinuousPickup = null;
            continuousDropOff = null;
            originalContinuousDropOff = null;
            shapeDistTraveled = null;
            timepoint = null;
            originalTimepoint = null;
            noticeCollection.clear();
            return this;
        }
    }

    /**
     * Returns the key corresponding to this {@link StopTime}
     *
     * @return the key corresponding to this {@link StopTime}
     */
    public static String getStopTimeMappingKey(final String tripId, final Integer stopSequence) {
        return tripId + stopSequence;
    }

    public String getStopTimeMappingKey() {
        return getStopTimeMappingKey(getTripId(), getStopSequence());
    }
}