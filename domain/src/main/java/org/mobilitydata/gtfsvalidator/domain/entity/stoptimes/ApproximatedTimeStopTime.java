package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ApproximatedTimeStopTime extends TimepointBase {

    private ApproximatedTimeStopTime(@NotNull String tripId,
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

    public static class ApproximatedTimeStopTimeBuilder extends TimepointBaseBuilder {

        public ApproximatedTimeStopTimeBuilder(@NotNull String tripId,
                                               @Nullable String arrivalTime,
                                               @Nullable String departureTime,
                                               @NotNull String stopId,
                                               int stopSequence) {
            super(tripId, arrivalTime, departureTime, stopId, stopSequence);
        }

        public ApproximatedTimeStopTime build() {
            return new ApproximatedTimeStopTime(tripId, arrivalTime, departureTime, stopId, stopSequence, stopHeadsign,
                    pickupType, dropOffType, shapeDistTraveled);
        }
    }
}