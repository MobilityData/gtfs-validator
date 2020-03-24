package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum ExceptionType {

    ADDED_SERVICE(1),
    REMOVED_SERVICE(2);

    private final int value;


    ExceptionType(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    static public ExceptionType fromInt(Integer fromValue) {
        return Stream.of(ExceptionType.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get(); // TODO: implement solution to handle unexpected enum values
    }
}
