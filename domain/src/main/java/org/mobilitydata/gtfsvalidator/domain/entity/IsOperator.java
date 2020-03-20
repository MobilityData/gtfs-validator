package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum IsOperator {
    ORGANIZATION_DOES_NOT_HAVE_THIS_ROLE(0),
    ORGANIZATION_HAS_THIS_ROLE(1);

    private final int value;

    IsOperator(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static IsOperator fromInt(Integer fromValue) {
        if (fromValue == null) {
            return ORGANIZATION_DOES_NOT_HAVE_THIS_ROLE;
        }
        return Stream.of(IsOperator.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get(); // TODO: implement solution to handle unexpected enum values
    }
}

