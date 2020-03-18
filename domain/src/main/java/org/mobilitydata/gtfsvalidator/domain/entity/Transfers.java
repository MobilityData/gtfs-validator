package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum Transfers {
    NO_TRANSFERS_ALLOWED(0),
    ONE_TRANSFER_ALLOWED(1),
    UNLIMITED_TRANSFERS(2),
    ERROR(-1);

    private int value;

    Transfers(int value) {
        this.value = value;
    }

    static public Transfers fromInt(Integer fromValue) {
        if (fromValue == null) {
            return UNLIMITED_TRANSFERS;
        }
        return Stream.of(Transfers.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(ERROR);
    }
}
