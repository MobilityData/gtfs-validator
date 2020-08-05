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

import org.locationtech.spatial4j.distance.DistanceUtils;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

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
     * This method calculates distance between two sets of coordinates.
     *
     * @param fromLat latitude of the first coordinates
     * @param fromLng longitude of the first coordinates
     * @param toLat   latitude of the second coordinates
     * @param toLng   longitude of the second coordinates
     * @return the calculation result in kilometers
     */
    public double distanceBetween(double fromLat, double fromLng, double toLat, double toLng) {
        return DistanceUtils.radians2Dist(
                DistanceUtils.distHaversineRAD(
                        DistanceUtils.toRadians(fromLat),
                        DistanceUtils.toRadians(fromLng),
                        DistanceUtils.toRadians(toLat),
                        DistanceUtils.toRadians(toLng)
                ),
                DistanceUtils.EARTH_MEAN_RADIUS_KM
        );
    }
}
