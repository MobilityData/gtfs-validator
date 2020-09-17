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
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.BoardingArea;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stops.StopOrPlatform;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShapeNotice;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import java.util.*;

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

/**
 * Utility class to carry out operations related to geospatial data
 */
public class GeospatialUtilsImpl implements GeospatialUtils {
    private static GeospatialUtilsImpl GEO_UTILS = null;

    // Spatial operation buffer values
    public static final double TRIP_BUFFER_DEGREES = DistanceUtils.KM_TO_DEG * (TRIP_BUFFER_METERS / 1000.0d);

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
     * @return the calculation result in meters
     */
    public int distanceBetweenMeter(double fromLat, double fromLng, double toLat, double toLng) {
        final ShapeFactory shapeFactory = getShapeFactory();
        final DistanceCalculator distanceCalculator = getDistanceCalculator();
        final Point origin = shapeFactory.pointXY(fromLng, fromLat);
        final Point destination = shapeFactory.pointXY(toLng, toLat);
        return (int) (DistanceUtils.DEG_TO_KM * distanceCalculator.distance(origin, destination)
                * KILOMETER_TO_METER_CONVERSION_FACTOR);
    }

    /**
     * Returns a list of E052 errors for the given input, one for each stop that is too far from the trip shapePoints
     *
     * @param trip        Trip for this GTFS trip
     * @param stopTimes   a map of StopTimes for a trip, sorted by stop_sequence
     * @param shapePoints a map of ShapePoints for a trip, sorted by shape_pt_sequence
     * @param stops       a map of all stops (keyed on stop_id), needed to obtain the latitude and longitude for each stop
     * @param testedCache a cache for previously tested shape_id and stop_id pairs (keyed on shape_id+stop_id). If the
     *                    combination of shape_id and stop_id appears in this set, we shouldn't test it again. Shapes
     *                    and stops tested in this method execution will be added to this testedCache.
     * @return a list of E052 errors, one for each stop that is too far from the trip shapePoints
     */
    public List<StopTooFarFromTripShapeNotice> checkStopsWithinTripShape(final Trip trip,
                                                                         final SortedMap<Integer, StopTime> stopTimes,
                                                                         final SortedMap<Integer, ShapePoint> shapePoints,
                                                                         final Map<String, LocationBase> stops,
                                                                         final Set<String> testedCache) {
        List<StopTooFarFromTripShapeNotice> errors = new ArrayList<>();
        if (trip == null || stopTimes == null || stopTimes.isEmpty() || shapePoints == null || shapePoints.isEmpty()) {
            // Nothing to do - return empty list
            return errors;
        }

        // Create a polyline from the GTFS shapes data
        ShapeFactory.LineStringBuilder lineBuilder = getShapeFactory().lineString();
        shapePoints.forEach((integer, shapePoint) -> lineBuilder.pointXY(shapePoint.getShapePtLon(), shapePoint.getShapePtLat()));
        Shape shapeLine = lineBuilder.build();

        // Create the buffered version of the trip as a polygon
        Shape shapeBuffer = shapeLine.getBuffered(TRIP_BUFFER_DEGREES, shapeLine.getContext());

        // Check if each stop is within the buffer polygon
        stopTimes.forEach((integer, stopTime) -> {
            LocationBase stop = stops.get(stopTime.getStopId());
            if (stop == null || stop.getStopLat() == null || stop.getStopLon() == null) {
                // Lat/lon are optional for location_type 4 - skip to the next stop if they aren't provided
                return;
            }
            if (!(stop instanceof StopOrPlatform) && !(stop instanceof BoardingArea)) {
                // This rule only applies to stops of location_type 0 and 4 - skip to next stop
                return;
            }
            if (testedCache.contains(trip.getShapeId() + stop.getStopId())) {
                // We've already tested this combination of shape ID and stop ID - skip to next stop
                return;
            }
            testedCache.add(trip.getShapeId() + stop.getStopId());
            org.locationtech.spatial4j.shape.Point p = getShapeFactory().pointXY(stop.getStopLon(), stop.getStopLat());
            if (!shapeBuffer.relate(p).equals(SpatialRelation.CONTAINS)) {
                errors.add(new StopTooFarFromTripShapeNotice(
                        "shapes.txt",
                        stopTime.getStopId(),
                        stopTime.getStopSequence(),
                        trip.getTripId(),
                        trip.getShapeId(),
                        TRIP_BUFFER_METERS));
            }
        });

        return errors;
    }

    /**
     * Method returning a {@code ShapeFactory}
     *
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
