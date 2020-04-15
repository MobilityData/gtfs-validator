package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecParamTest {

    @Test
    public void getShortNameShouldReturnShortName() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", true, "value");

        assertEquals("short_name", underTest.getShortName());
    }

    @Test
    public void getLongNameShouldReturnLongName() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", true, "value");

        assertEquals("long_name", underTest.getLongName());
    }

    @Test
    public void getDescriptionShouldReturnDescription() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", true, "value");

        assertEquals("description", underTest.getDescription());
    }

    @Test
    public void hasArgumentWithNoFalseArgumentShouldReturnFalse() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", false, "value");

        assertFalse(underTest.hasArgument());
    }

    @Test
    public void hasArgumentWithTrueArgumentShouldReturnFalse() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", true, "value");

        assertTrue(underTest.hasArgument());
    }

    @Test
    public void getValueShouldReturnValue() {
        ExecParam underTest = new ExecParam("short_name",
                "long_name", "description", true, "value");

        assertEquals("value", underTest.getValue());
    }
}