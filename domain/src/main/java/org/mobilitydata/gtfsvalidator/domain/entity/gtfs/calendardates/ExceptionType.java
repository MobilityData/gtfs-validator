package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import java.util.stream.Stream;

public enum ExceptionType {

    ADDED_SERVICE(1),
    REMOVED_SERVICE(2);

    private final int value;

    ExceptionType(final int value) {
        this.value = value;
    }

    static public ExceptionType fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return null;
        }
        return Stream.of(ExceptionType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}
