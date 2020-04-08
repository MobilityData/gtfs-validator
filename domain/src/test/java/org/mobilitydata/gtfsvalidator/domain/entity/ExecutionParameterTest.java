package org.mobilitydata.gtfsvalidator.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionParameterTest {

    @Test
    public void getShortNameShouldReturnShortName() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", true, "value");

        assertEquals("short_name", underTest.getShortName());
    }

    @Test
    public void getLongNameShouldReturnLongName() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", true, "value");

        assertEquals("long_name", underTest.getLongName());
    }

    @Test
    public void getDescriptionShouldReturnDescription() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", true, "value");

        assertEquals("description", underTest.getDescription());
    }

    @Test
    public void hasArgumentWithNoFalseArgumentShouldReturnFalse() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", false, "value");

        assertFalse(underTest.hasArgument());
    }

    @Test
    public void hasArgumentWithTrueArgumentShouldReturnFalse() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", true, "value");

        assertTrue(underTest.hasArgument());
    }

    @Test
    public void getValueShouldReturnValue() {
        ExecutionParameter underTest = new ExecutionParameter("short_name",
                "long_name", "description", true, "value");

        assertEquals("value", underTest.getValue());
    }
}