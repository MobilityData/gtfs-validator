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

import org.locationtech.spatial4j.context.jts.JtsSpatialContext;
import org.locationtech.spatial4j.distance.DistanceCalculator;
import org.locationtech.spatial4j.distance.DistanceUtils;
import org.locationtech.spatial4j.shape.Point;
import org.locationtech.spatial4j.shape.Shape;
import org.locationtech.spatial4j.shape.ShapeFactory;
import org.locationtech.spatial4j.shape.SpatialRelation;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShape;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase;
import org.mobilitydata.gtfsvalidator.usecase.utils.GeospatialUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static org.locationtech.spatial4j.context.SpatialContext.GEO;

/**
 * Utility class to carry out operations related to geospatial data
 */
public class GeospatialUtilsImpl implements GeospatialUtils {
    private static GeospatialUtilsImpl GEO_UTILS = null;

    // Spatial operation buffer values
    public static final double TRIP_BUFFER_METERS = 100; // Per GTFS Best Practices (https://gtfs.org/best-practices/#shapestxt)
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
     * Returns a list of E047 errors for the given input, one for each stop that is too far from the trip shapePoints
     *
     * @param trip        Trip for this GTFS trip
     * @param stopTimes   a map of StopTimes for a trip, sorted by stop_sequence
     * @param shapePoints a map of ShapePoints for a trip, sorted by shape_pt_sequence
     * @param stops       a map of all stops (keyed on stop_id), needed to obtain the latitude and longitude for each stop
     * @return a list of E047 errors, one for each stop that is too far from the trip shapePoints
     */
    public List<StopTooFarFromTripShape> checkStopsWithinTripShape(Trip trip,
                                                                   SortedMap<Integer, StopTime> stopTimes,
                                                                   SortedMap<Integer, ShapePoint> shapePoints,
                                                                   Map<String, LocationBase> stops) {
        List<StopTooFarFromTripShape> errors = new ArrayList<>();
        if (trip == null || stopTimes == null || stopTimes.isEmpty() || shapePoints == null || shapePoints.isEmpty()) {
            // Nothing to do - return empty list
            return errors;
        }

        // Create a polyline from the GTFS shapes data
        ShapeFactory sf = JtsSpatialContext.GEO.getShapeFactory();
        ShapeFactory.LineStringBuilder lineBuilder = sf.lineString();
        shapePoints.forEach((integer, shapePoint) -> lineBuilder.pointXY(shapePoint.getShapePtLon(), shapePoint.getShapePtLat()));
        Shape shapeLine = lineBuilder.build();

        // Create the buffered version of the trip as a polygon
        Shape shapeBuffer = shapeLine.getBuffered(TRIP_BUFFER_DEGREES, shapeLine.getContext());

        stopTimes.forEach((integer, stopTime) -> {
            // Check if each stop is within the buffer polygon
            LocationBase stop = stops.get(stopTime.getStopId());
            // TODO - check for stop type? Some don't have lat/lon
            org.locationtech.spatial4j.shape.Point p = sf.pointXY(stop.getStopLon(), stop.getStopLat());
            if (!shapeBuffer.relate(p).equals(SpatialRelation.CONTAINS)) {
                // TODO - measure distance to shape line
                errors.add(new StopTooFarFromTripShape(
                        "shapes.txt",
                        stopTime.getStopId(),
                        stopTime.getStopSequence(),
                        trip.getTripId(),
                        trip.getShapeId(),
                        distanceToShapeLine,
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
