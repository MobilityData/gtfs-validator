package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecParamTest {
    private static final String KEY = "key";
    private static final String SHORT_NAME = "short_name";
    private static final String LONG_NAME = "long_name";
    private static final String DESCRIPTION = "description";
    private static final String VALUE = "value";
    private static final String DEFAULT_VALUE = "default value";

    @Test
    public void getShortNameShouldReturnShortName() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertEquals(SHORT_NAME, underTest.getShortName());
    }

    @Test
    public void getLongNameShouldReturnLongName() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertEquals(LONG_NAME, underTest.getLongName());
    }

    @Test
    public void getDescriptionShouldReturnDescription() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertEquals(DESCRIPTION, underTest.getDescription());
    }

    @Test
    public void hasArgumentWithNoFalseArgumentShouldReturnFalse() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, false, VALUE);
        assertFalse(underTest.hasArgument());
    }

    @Test
    public void hasArgumentWithTrueArgumentShouldReturnFalse() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertTrue(underTest.hasArgument());
    }

    @Test
    public void getValueShouldReturnValue() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertEquals(VALUE, underTest.getValue());
    }

    @Test
    public void getKeyShouldReturnKey() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        assertEquals(KEY, underTest.getKey());
    }

    @Test
    public void getDefaultValueShouldReturnDefaultValue() {
        final ExecParam underTest = new ExecParam(KEY, SHORT_NAME, LONG_NAME, DESCRIPTION, true, VALUE);
        underTest.setDefaultValue(DEFAULT_VALUE);
        assertEquals(DEFAULT_VALUE, underTest.getDefaultValue());
    }
}