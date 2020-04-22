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