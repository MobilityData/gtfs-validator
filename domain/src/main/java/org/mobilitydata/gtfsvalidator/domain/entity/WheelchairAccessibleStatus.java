package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum WheelchairAccessibleStatus {
    UNKNOWN_WHEELCHAIR_ACCESSIBILITY(0),
    WHEELCHAIR_ACCESSIBLE(1),
    NOT_WHEELCHAIR_ACCESSIBLE(2);

    private int value;

    WheelchairAccessibleStatus(int value) {
        this.value = value;
    }

    static public WheelchairAccessibleStatus fromInt(int fromValue) {
        return Stream.of(WheelchairAccessibleStatus.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get();
    }
}
