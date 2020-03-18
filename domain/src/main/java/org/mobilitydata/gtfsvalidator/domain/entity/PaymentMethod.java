package org.mobilitydata.gtfsvalidator.domain.entity;

import java.util.stream.Stream;

public enum PaymentMethod {
    ON_BOARD(0),
    BEF0RE_BOARDING(1),
    ERROR(-1);

    private int value;

    PaymentMethod(int value) {
        this.value = value;
    }

    static public PaymentMethod fromInt(Integer fromValue) {
        return Stream.of(PaymentMethod.values())
                .filter(enumItem -> enumItem.value == fromValue)
                .findAny()
                .orElse(ERROR);
    }
}
