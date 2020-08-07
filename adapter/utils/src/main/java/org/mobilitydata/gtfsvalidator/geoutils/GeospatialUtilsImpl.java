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

import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

/**
 * Utility class to carry out operations related to geospatial data
 */
public class GeospatialUtilsImpl implements GeospatialUtils {
    private static GeospatialUtilsImpl GEO_UTILS = null;

    private GeospatialUtilsImpl() {
    }

    /**
     * Implement singleton pattern
     *
     * @return a unique instance of {@code GeospatialUtilsImpl}
     */
    public static GeospatialUtilsImpl getInstance() {
        if (GEO_UTILS == null) {
            GEO_UTILS = new GeospatialUtilsImpl();
        }
        return GEO_UTILS;
    }

    /**
     * This method calculates distance between two sets of coordinates. Result is expressed in meters.
     *
     * @param fromLat latitude of the first coordinates
     * @param fromLng longitude of the first coordinates
     * @param toLat   latitude of the second coordinates
     * @param toLng   longitude of the second coordinates
     * @return the calculation result in kilometers
     */
    public int distanceBetween(double fromLat, double fromLng, double toLat, double toLng) {
        final ShapeFactory shapeFactory = getShapeFactory();
        final DistanceCalculator distanceCalculator = getDistanceCalculator();
        final Point origin = shapeFactory.pointXY(fromLng, fromLat);
        final Point destination = shapeFactory.pointXY(toLng, toLat);
        return (int) (DistanceUtils.DEG_TO_KM * distanceCalculator.distance(origin, destination)
                        * KILOMETER_TO_METER_CONVERSION_FACTOR);
    }

    /**
     * Method returning a {@code ShapeFactory}
     * @return a {@link ShapeFactory}
     */
    private static ShapeFactory getShapeFactory() {
        return GEO.getShapeFactory();
    }

    /**
     * Method returning a {@code DistanceCalculator}
     * @return a {@link DistanceCalculator}
     */
    private static DistanceCalculator getDistanceCalculator() {
        return GEO.getDistCalc();
    }
}
