package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimepointStopTime extends TimepointBase {

    private TimepointStopTime(@NotNull String tripId,
                              @NotNull String arrivalTime,
                              @NotNull String departureTime,
                              @NotNull String stopId,
                              int stopSequence,
                              @Nullable String stopHeadsign,
                              @NotNull PickupType pickupType,
                              @NotNull DropOffType dropOffType,
                              @Nullable Float shapeDistTraveled) {
        super(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign, pickupType, dropOffType,
                shapeDistTraveled);
    }

    public static class TimepointStopTimeBuilder extends TimepointBaseBuilder {

        public TimepointStopTimeBuilder(@NotNull String tripId,
                                        @NotNull String arrivalTime,
                                        @NotNull String departureTime,
                                        @NotNull String stopId,
                                        int stopSequence) throws IllegalArgumentException {
            super(tripId, arrivalTime, departureTime, stopId, stopSequence);

            //noinspection ConstantConditions
            if (arrivalTime == null) {
                throw new IllegalArgumentException("null value for arrivalTime");
            }

            //noinspection ConstantConditions
            if (departureTime == null) {
                throw new IllegalArgumentException("null value for departureTime");
            }
        }

        public TimepointStopTime build() {
            return new TimepointStopTime(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign,
                    pickupType, dropOffType, shapeDistTraveled);
        }
    }
}