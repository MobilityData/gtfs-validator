package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NotTimepointStoptime extends TimepointBase {

    public NotTimepointStoptime(@NotNull String tripId,
                                @Nullable String arrivalTime,
                                @Nullable String departureTime,
                                @NotNull String stopId,
                                int stopSequence,
                                @Nullable String stopHeadsign,
                                @NotNull PickupType pickupType,
                                @NotNull DropOffType dropOffType,
                                @Nullable Float shapeDistTraveled) {
        super(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign, pickupType, dropOffType,
                shapeDistTraveled);
    }

    public static class NotTimepointStoptimeBuilder extends TimepointBaseBuilder {

        public NotTimepointStoptimeBuilder(@NotNull String tripId,
                                           @Nullable String arrivalTime,
                                           @Nullable String departureTime,
                                           @NotNull String stopId,
                                           int stopSequence) {
            super(tripId, arrivalTime, departureTime, stopId, stopSequence);
        }

        public NotTimepointStoptime build() {
            return new NotTimepointStoptime(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign,
                    pickupType, dropOffType, shapeDistTraveled);
        }
    }
}