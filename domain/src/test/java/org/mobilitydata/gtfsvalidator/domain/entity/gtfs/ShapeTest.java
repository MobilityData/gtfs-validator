/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShapeTest {

    // Field shapeId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
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