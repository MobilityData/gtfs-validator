package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TimepointStoptime extends TimepointBase {

    public TimepointStoptime(@NotNull String tripId,
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

    public static class TimepointStoptimeBuilder extends TimepointBaseBuilder {

        public TimepointStoptimeBuilder(@NotNull String tripId,
                                        @NotNull String arrivalTime,
                                        @NotNull String departureTime,
                                        @NotNull String stopId,
                                        int stopSequence) {
            super(tripId, arrivalTime, departureTime, stopId, stopSequence);

        }

        public TimepointStoptime build() {
            return new TimepointStoptime(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign,
                    pickupType, dropOffType, shapeDistTraveled);
        }
    }
}