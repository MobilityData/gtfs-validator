package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PathwayModeTest {

    @Test
    public void createPathwayModeWithNullValueShouldReturnNull() {
        final Integer unexpectedEnumValue = null;
        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithUnexpectedValue11ShouldReturnNull() {
        final Integer unexpectedEnumValue = 11;
        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithUnexpectedValue13ShouldReturnNull() {
        final Integer unexpectedEnumValue = 13;
        assertNull(PathwayMode.fromInt(unexpectedEnumValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue1ShouldReturnWalkway() {
        final Integer expectedValue = 1;
        assertEquals(PathwayMode.WALKWAY, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue2ShouldReturnWalkway() {
        final Integer expectedValue = 2;
        assertEquals(PathwayMode.STAIRS, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue3ShouldReturnWalkway() {
        final Integer expectedValue = 3;
        assertEquals(PathwayMode.MOVING_SIDEWALK_TRAVELATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue4ShouldReturnWalkway() {
        final Integer expectedValue = 4;
        assertEquals(PathwayMode.ESCALATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpectedValue5ShouldReturnWalkway() {
        final Integer expectedValue = 5;
        assertEquals(PathwayMode.ELEVATOR, PathwayMode.fromInt(expectedValue));
    }

    @Test
    public void createPathwayModeWithExpected11ValueShouldReturnWalkway() {
        final Integer expectedValue = 6;
        assertEquals(PathwayMode.FARE_GATE, PathwayMode.fromInt(expectedValue));
    }
}