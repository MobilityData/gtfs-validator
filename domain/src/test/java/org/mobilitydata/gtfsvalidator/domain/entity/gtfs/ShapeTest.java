package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unused")
class ShapeTest {

    @Test
    public void createShapeWithNullIdShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        //noinspection ConstantConditions
        underTest.shapeId(null)
                .shapePtLat(112)
                .shapePtLon(110)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field shape_id can not be null in file shapes.txt", exception.getMessage());
    }

    @Test
    public void createShapeWithTooBigLatitudeShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(120)
                .shapePtLon(0)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_latitude", exception.getMessage());
    }

    @Test
    public void createShapeWithTooSmallLatitudeShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(-120)
                .shapePtLon(0)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_latitude", exception.getMessage());
    }

    @Test
    public void createShapeWithTooSmallLongitudeShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(0)
                .shapePtLon(-270)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_longitude", exception.getMessage());
    }

    @Test
    public void createShapeWithTooBigLongitudeShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(0)
                .shapePtLon(270)
                .shapePtSequence(2)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_longitude", exception.getMessage());
    }

    @Test
    public void createShapeWithInvalidShapePtSequenceShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(0)
                .shapePtLon(0)
                .shapePtSequence(-3)
                .shapeDistTraveled(2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_pt_sequence", exception.getMessage());
    }

    @SuppressWarnings("unused")
    @Test
    public void createShapeWithInvalidShapeDistTraveledShouldThrowException() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(0)
                .shapePtLon(0)
                .shapePtSequence(0)
                .shapeDistTraveled(-2.0f);

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field shape_dist_traveled", exception.getMessage());
    }

    @Test
    public void shapeShouldBeAbleToBeComparedToOtherShape() {
        final Shape.ShapeBuilder underTest = new Shape.ShapeBuilder();

        underTest.shapeId("test")
                .shapePtLat(0)
                .shapePtLon(0)
                .shapePtSequence(0)
                .shapeDistTraveled(0f);

        final Shape firstShapeInSequence = underTest.build();

        underTest.shapePtSequence(2);

        final Shape secondShapeInSequence = underTest.build();

        assertTrue(secondShapeInSequence.isGreaterThan(firstShapeInSequence));
    }
}