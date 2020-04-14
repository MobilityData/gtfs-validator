package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PathwayModeTest {

    @Test
    public void createPathwayModeWithNullValueShouldReturnNull() {
        Integer unexpectedEnumValue = null;

        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithUnexpectedValue11ShouldReturnNull() {
        Integer unexpectedEnumValue = 11;

        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithUnexpectedValue13ShouldReturnNull() {
        Integer unexpectedEnumValue = 13;

        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue1ShouldReturnWalkway() {
        Integer expectedValue = 1;

        assertEquals(PathwayMode.WALKWAY, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue2ShouldReturnWalkway() {
        Integer expectedValue = 2;

        assertEquals(PathwayMode.STAIRS, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue3ShouldReturnWalkway() {
        Integer expectedValue = 3;

        assertEquals(PathwayMode.MOVING_SIDEWALK_TRAVELATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue4ShouldReturnWalkway() {
        Integer expectedValue = 4;

        assertEquals(PathwayMode.ESCALATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue5ShouldReturnWalkway() {
        Integer expectedValue = 5;

        assertEquals(PathwayMode.ELEVATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpected11ValueShouldReturnWalkway() {
        Integer expectedValue = 6;

        assertEquals(PathwayMode.FARE_GATE, PathwayMode.fromInt(expectedValue));
    }
}