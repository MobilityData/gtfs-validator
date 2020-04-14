package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathwayTest {

    @Test
    public void createPathwayWithNullPathwayIdShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        underTest.pathwayId(null)
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field pathway_id can not be null", exception.getMessage());
    }

    @Test
    public void createPathwayWithNullFromStopIdShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        underTest.pathwayId("test")
                .fromStopId(null)
                .toStopId("test")
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field from_stop_id can not be null", exception.getMessage());
    }

    @Test
    public void createPathwayWithNullToStopIdShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId(null)
                .pathwayMode(2)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field to_stop_id can not be null", exception.getMessage());
    }

    @Test
    public void createPathwayWithNullToPathwayModeShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        //noinspection ConstantConditions
        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(null)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value for field pathway_mode", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidToPathwayModeShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(13)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value for field pathway_mode", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidIsBidirectionalShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(1)
                .isBidirectional(3)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field is_bidirectional", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidLengthBidirectionalShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(1)
                .isBidirectional(1)
                .length(-10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field length", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidTraversalTimeShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(-2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field traversal_time", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidStairCountShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(-3)
                .maxSlope(30f)
                .minWidth(30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field stair_count", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidMinWidthShouldThrowException() {
        Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

        underTest.pathwayId("test")
                .fromStopId("test")
                .toStopId("test")
                .pathwayMode(1)
                .isBidirectional(1)
                .length(10.0f)
                .traversalTime(2)
                .stairCount(3)
                .maxSlope(30f)
                .minWidth(-30f)
                .signpostedAs("test")
                .reversedSignpostedAs("test");

        Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field min_width", exception.getMessage());
    }
}