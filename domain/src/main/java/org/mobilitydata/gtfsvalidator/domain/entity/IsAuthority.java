package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum IsAuthority {
    ORGANIZATION_DOES_NOT_HAVE_THIS_ROLE(0),
    ORGANIZATION_HAS_THIS_ROLE(1);

    private final int value;

    IsAuthority(int value) {
        this.value = value;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public static IsAuthority fromInt(Integer fromValue) {
        if (fromValue == null) {
            return ORGANIZATION_DOES_NOT_HAVE_THIS_ROLE;
        }
        return Stream.of(IsAuthority.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .get(); // TODO: implement solution to handle unexpected enum values
    }
}

