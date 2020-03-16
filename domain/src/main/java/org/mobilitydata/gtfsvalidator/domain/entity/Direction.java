package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum Direction {
    OUTBOUND(0),
    INBOUND(1);

    private int value;

    Direction(int value) {
        this.value = value;
    }

    static public Direction fromInt(int fromValue) {
        return Stream.of(Direction.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(null);
    }
}
