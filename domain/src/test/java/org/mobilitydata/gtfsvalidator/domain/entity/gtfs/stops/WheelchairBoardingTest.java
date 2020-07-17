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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.stops.WheelchairBoarding.*;

class WheelchairBoardingTest {

    @Test
    void createEnumWheelchairBoardingIdWithInvalidValueShouldInvalidValue() {
        assertEquals(INVALID_VALUE, WheelchairBoarding.fromInt(3));
    }

    @Test
    void createEnumWheelchairBoardingIdWithNullValueShouldReturnUnknownWheelchairBoarding() {
        assertEquals(UNKNOWN_WHEELCHAIR_BOARDING, WheelchairBoarding.fromInt(null));
    }

    @Test
    void createEnumWheelchairBoardingWithValidValue0ShouldReturnUnknownWheelchairBoarding() {
        assertEquals(UNKNOWN_WHEELCHAIR_BOARDING, WheelchairBoarding.fromInt(0));
    }

    @Test
    void createEnumWheelchairBoardingWithValidValue1ShouldReturnWheelchairAccessible() {
        assertEquals(WHEELCHAIR_ACCESSIBLE, WheelchairBoarding.fromInt(1));
    }

    @Test
    void createEnumWheelchairBoardingWithValidValue2ShouldReturnNotWheelchairAccessible() {
        assertEquals(NOT_WHEELCHAIR_ACCESSIBLE, WheelchairBoarding.fromInt(2));
    }
}