package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public enum IsBidirectional {
    UNIDIRECTIONAL(0),
    BIDIRECTIONAL(1);


    private final int value;

    IsBidirectional(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @NotNull
    static public IsBidirectional fromInt(Integer fromValue) {
        return Stream.of(IsBidirectional.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get(); // TODO: implement solution to handle unexpected enum values
    }
}
