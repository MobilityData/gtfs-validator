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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PathwayTest {

    // Field pathwayId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullPathwayIdShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field pathway_id can not be null", exception.getMessage());
    }

    // Field fromStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullFromStopIdShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field from_stop_id can not be null", exception.getMessage());
    }

    // Field toStopId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToStopIdShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("field to_stop_id can not be null", exception.getMessage());
    }

    // Field pathwayMode is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createPathwayWithNullToPathwayModeShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value for field pathway_mode", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidToPathwayModeShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("unexpected value for field pathway_mode", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidIsBidirectionalShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field is_bidirectional", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidLengthBidirectionalShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field length", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidTraversalTimeShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field traversal_time", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidStairCountShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field stair_count", exception.getMessage());
    }

    @Test
    public void createPathwayWithInvalidMinWidthShouldThrowException() {
        final Pathway.PathwayBuilder underTest = new Pathway.PathwayBuilder();

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

        final Exception exception = assertThrows(IllegalArgumentException.class, underTest::build);

        assertEquals("invalid value for field min_width", exception.getMessage());
    }
}