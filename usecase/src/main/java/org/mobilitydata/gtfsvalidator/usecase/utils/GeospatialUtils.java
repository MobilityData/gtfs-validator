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

package org.mobilitydata.gtfsvalidator.usecase.utils;

import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.StopTooFarFromTripShape;
import org.mobilitydata.gtfsvalidator.domain.entity.stops.LocationBase;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public interface GeospatialUtils {
    int KILOMETER_TO_METER_CONVERSION_FACTOR = 1000; // conversion factor from kilometers to meters

    /**
     * Return the distance between two points given there lat/lon positions in the specified unit. The distance is
     * computed following the haversine formula. See https://locationtech.github.io/spatial4j/apidocs/org/locationtech/spatial4j/context/SpatialContext.html
     * Note that points of origin (from) and destination (to) can be swapped.
     * Result is expressed in meters.
     **/
    int distanceBetweenMeter(double fromLat, double fromLng, double toLat, double toLng);

    /**
     * Returns a list of E047 errors for the given input, one for each stop that is too far from the trip shape
     *
     * @param trip      Trip for this GTFS trip
     * @param stopTimes a map of StopTimes for a trip, sorted by stop_sequence
     * @param shape     a map of ShapePoints for a trip, sorted by shape_pt_sequence
     * @param stops     a map of all stops (keyed on stop_id), needed to obtain the latitude and longitude for each stop
     * @return a list of E047 errors, one for each stop that is too far from the trip shape
     */
    List<StopTooFarFromTripShape> checkStopsWithinTripShape(Trip trip,
                                                            SortedMap<Integer, StopTime> stopTimes,
                                                            SortedMap<Integer, ShapePoint> shape,
                                                            Map<String, LocationBase> stops);
}
