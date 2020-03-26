package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import java.util.stream.Stream;

public enum DropOffType {

    REGULAR_DROPOFF(0),
    NO_DROPOFF(1),
    MUST_PHONE_DROPOFF(2),
    MUST_ASK_DRIVER_DROPOFF(3);

    private int value;

    DropOffType(int value) {
        this.value = value;
    }

    static public DropOffType fromInt(Integer fromValue) {
        if (fromValue == null) {
            return REGULAR_DROPOFF;
        }
        return Stream.of(DropOffType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(REGULAR_DROPOFF);
    }
}