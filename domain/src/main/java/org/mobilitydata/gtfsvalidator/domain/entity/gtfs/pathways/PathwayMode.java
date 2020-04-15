package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

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

    PathwayMode(final int value) {
        this.value = value;
    }

    static public PathwayMode fromInt(final Integer fromValue) {
        if (fromValue == null) {
            return null;
        }
        return Stream.of(PathwayMode.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}