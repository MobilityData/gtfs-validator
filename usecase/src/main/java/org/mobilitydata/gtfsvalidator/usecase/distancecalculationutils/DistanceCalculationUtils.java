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

package org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;

import java.util.Map;

/**
 * Interface for distance computations. To be implemented  in different flavours if needed.
 */
public interface DistanceCalculationUtils {
    int KILOMETER_TO_METER_CONVERSION_FACTOR = 1000; // conversion factor from kilometers to meters

    /**
     * Return the distance between two points given there lat/lon positions in the specified unit. The distance is
     * computed following the haversine formula. See https://locationtech.github.io/spatial4j/apidocs/org/locationtech/spatial4j/context/SpatialContext.html
     * Note that points of origin and destination can be swapped.
     *
     * @param originLatitude        latitude of the origin point
     * @param originLongitude       longitude of the origin point
     * @param destinationLatitude   latitude of the destination point
     * @param destinationLongitude  longitude of the destination point
     * @param distanceUnit          unit of the desired result of computation
     * @return the distance between two points given there lat/lon positions in the specified unit
     */
    double distanceBetweenTwoPoints(final float originLatitude, final float destinationLatitude,
                                           final float originLongitude, final float destinationLongitude,
                                           final DistanceUnit distanceUnit);

    /**
     * Method returns the total length of a shape. As a reminder, a shape is a collection of {@code ShapePoint} ordered
     * by shape_pt_sequence. The result is expressed in the specified unit.
     *
     * @param shape          the collection of {@link ShapePoint} whose total length is to be computed
     * @param distanceUnit   the {@code DistanceUnit} of the result (meter, kilometers)
     * @return the total length of a shape in the specified unit
     */
    double getShapeTotalDistance(final Map<Integer, ShapePoint> shape, final DistanceUnit distanceUnit);
}
