/*
 *  Copyright (c) 2020. MobilityData IO.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.geoutils;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class GeospatialUtilsImplTest {

    private static final GeospatialUtilsImpl GEO_UTILS = GeospatialUtilsImpl.getInstance();

    @Test
    void distanceFromToSameCoordinateIsZero() {

        double toCheck = GEO_UTILS.distanceBetween(45.508888, -73.561668, 45.508888, -73.561668);

        assertEquals(0, toCheck, 0);
    }

    @Test
    void distanceReferenceCheck() {
        // geographic data extracted and validated with an external tool
        double toCheck = GEO_UTILS.distanceBetween(45.517351, -73.597320, 45.459554, -73.584879);

        assertEquals(6499, toCheck, 1);
    }
}
