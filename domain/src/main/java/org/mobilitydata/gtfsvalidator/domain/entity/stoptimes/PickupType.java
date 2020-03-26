package org.mobilitydata.gtfsvalidator.domain.entity.stoptimes;

import java.util.stream.Stream;

public enum PickupType {

    REGULAR_PICKUP(0),
    NO_PICKUP(1),
    MUST_PHONE_PICKUP(2),
    MUST_ASK_DRIVER_PICKUP(3);

    private int value;

    PickupType(int value) {
        this.value = value;
    }

    static public PickupType fromInt(Integer fromValue) {
        if (fromValue == null) {
            return REGULAR_PICKUP;
        }
        return Stream.of(PickupType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(REGULAR_PICKUP);
    }
}