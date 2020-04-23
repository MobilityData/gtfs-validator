package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExecParamTest {
    private static final String KEY = "key";
    private static final String VALUE = "value";

    @Test
    public void getValueShouldReturnValue() {
        final ExecParam underTest = new ExecParam(KEY, VALUE);
        assertEquals(VALUE, underTest.getValue());
    }

    @Test
    public void getKeyShouldReturnKey() {
        final ExecParam underTest = new ExecParam(KEY, VALUE);
        assertEquals(KEY, underTest.getKey());
    }
}