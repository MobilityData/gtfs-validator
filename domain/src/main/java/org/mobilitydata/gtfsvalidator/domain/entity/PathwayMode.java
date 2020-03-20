package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

public enum PathwayMode {
    WALKWAY(1),
    STAIRS(2),
    MOVING_SIDEWALK_TRAVELATOR(3),
    ESCALATOR(4),
    ELEVATOR(5),
    FARE_GATE(6),
    EXIT_GATE(7);

    private final int value;

    PathwayMode(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @NotNull
    static public PathwayMode fromInt(Integer fromValue) {
        return Stream.of(PathwayMode.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get(); // TODO: implement solution to handle unexpected enum values
    }
}