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

// todo: javadoc
public interface DistanceCalculationUtils {
    int KILOMETER_TO_METER_CONVERSION_FACTOR = 1000;

    double distanceBetweenTwoPoints(final float originLatitude, final float destinationLatitude,
                                           final float originLongitude, final float destinationLongitude,
                                           final DistanceUnit distanceUnit);

    double getShapeTotalDistance(final Map<Integer, ShapePoint> shape, final DistanceUnit distanceUnit);
}
