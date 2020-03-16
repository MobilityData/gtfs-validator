package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum BikesAllowedStatus {
    UNKNOWN_BIKES_ALLOWANCE(0),
    BIKES_ALLOWED(1),
    NO_BIKES_ALLOWED(2);

    private int value;

    BikesAllowedStatus(int value) {
        this.value = value;
    }

    static public BikesAllowedStatus fromInt(int fromValue) {
        return Stream.of(BikesAllowedStatus.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get();
    }
}
