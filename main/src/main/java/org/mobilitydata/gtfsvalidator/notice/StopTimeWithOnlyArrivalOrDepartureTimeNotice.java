package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

public class StopTimeWithOnlyArrivalOrDepartureTimeNotice extends Notice {
    public StopTimeWithOnlyArrivalOrDepartureTimeNotice(long csvRowNumber, String tripId, int stopSequence, String specifiedField) {
        super(ImmutableMap.of(
                "csvRowNumber", csvRowNumber,
                "tripId", tripId,
                "stopSequence", stopSequence,
                "specifiedField", specifiedField
        ));
    }

    @Override
    public String getCode() {
        return "stop_time_with_only_arrival_or_departure_time";
    }
}

