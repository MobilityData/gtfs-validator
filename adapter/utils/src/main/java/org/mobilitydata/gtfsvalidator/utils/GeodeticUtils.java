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

package org.mobilitydata.gtfsvalidator.utils;

import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.ShapeFactory;

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceCalculationUtils;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceUnit;

import java.util.Map;

/**
 * Interface implementation using external library org.locationtech.spatial4j. This is used to compute distance between
 * two {@code ShapePoint}.
 */
public class GeodeticUtils implements DistanceCalculationUtils {
    private static GeodeticUtils DISTANCE_CALCULATION_UTILS = null;

    private GeodeticUtils(){}

    /**
     * Implement singleton pattern
     *
     * @return a unique instance of {@code GeodeticUtils}
     */
    public static GeodeticUtils getInstance() {
        if (DISTANCE_CALCULATION_UTILS == null) {
            DISTANCE_CALCULATION_UTILS = new GeodeticUtils();
        }
        return DISTANCE_CALCULATION_UTILS;
    }

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
    @Override
    public double distanceBetweenTwoPoints(final float originLatitude, final float originLongitude,
                                           final float destinationLatitude, final float destinationLongitude,
                                           final DistanceUnit distanceUnit) {
        final ShapeFactory shapeFactory = getShapeFactory();
        final DistanceCalculator distanceCalculator = getDistanceCalculator();

        final Point origin = shapeFactory.pointXY(originLongitude, originLatitude);
        final Point destination = shapeFactory.pointXY(destinationLongitude, destinationLatitude);
        // implementation uses haversine formula which determines the great-circle distance between two points on a
        // sphere given their longitudes and latitudes. Note that the elevation of both points is not taken into
        // consideration.
        final double distance = DistanceUtils.DEG_TO_KM * distanceCalculator.distance(origin, destination);
        if (distanceUnit == DistanceUnit.KILOMETER) {
                return distance;
        } else {
                return distance * KILOMETER_TO_METER_CONVERSION_FACTOR;
        }
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

    /**
     * Method returns the total length of a shape. As a reminder, a shape is a collection of {@code ShapePoint} ordered
     * by shape_pt_sequence. The result is expressed in the specified unit.
     *
     * @param shape          the collection of {@link ShapePoint} sorted by `shape_pt_sequence` whose total length is to
     *                       be computed
     * @param distanceUnit   the {@code DistanceUnit} of the result (meter, kilometers)
     * @return the total length of a shape in the specified unit
     */
    @Override
    public double getShapeTotalDistance(final Map<Integer, ShapePoint> shape, final DistanceUnit distanceUnit) {
        double shapeTotalDistance = 0;
        ShapePoint origin = shape.values().stream().findFirst().isPresent() ?
                shape.values().stream().findFirst().get() :
                null;
        if (origin!= null) {
            for (Map.Entry<Integer, ShapePoint> integerShapePointEntry : shape.entrySet()) {
                final ShapePoint destination = integerShapePointEntry.getValue();
                shapeTotalDistance += distanceBetweenTwoPoints(origin.getShapePtLat(), origin.getShapePtLon(),
                        destination.getShapePtLat(), destination.getShapePtLon(), distanceUnit);
                origin = destination;
            }
        }
        return shapeTotalDistance;
    }
}
