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

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalPosition;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceCalculationUtils;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceUnit;

import java.util.Map;


// todo: javadoc
public class GavaghanDistanceCalculationUtils implements DistanceCalculationUtils {
    final static Ellipsoid reference = Ellipsoid.WGS84;

    // todo: javadoc
    @Override
    public double distanceBetweenTwoPoints(final float originLatitude, final float destinationLatitude,
                                                  final float originLongitude, final float destinationLongitude,
                                                  final DistanceUnit distanceUnit) {
        final GlobalPosition origin = new GlobalPosition(originLatitude, originLongitude, 0.0);
        final GlobalPosition destination = new GlobalPosition(destinationLatitude, destinationLongitude, 0.0);

        if (distanceUnit == DistanceUnit.METER) {
                return getGeodeticCalculator().calculateGeodeticCurve(reference, origin, destination)
                        .getEllipsoidalDistance();
        } else {
                return getGeodeticCalculator().calculateGeodeticCurve(reference, origin, destination)
                        .getEllipsoidalDistance()/KILOMETER_TO_METER_CONVERSION_FACTOR;
        }
    }

    // todo: javadoc
    private static GeodeticCalculator getGeodeticCalculator() {
        return new GeodeticCalculator();
    }

    // todo: javadoc
    @Override
    public double getShapeTotalDistance(final Map<Integer, ShapePoint> shape, final DistanceUnit unit) {
        double shapeTotalDistance = 0;
        ShapePoint origin = shape.values().stream().findFirst().isPresent() ?
                shape.values().stream().findFirst().get() :
                null;
        if (origin!= null) {
            for (Map.Entry<Integer, ShapePoint> integerShapePointEntry : shape.entrySet()) {
                final ShapePoint destination = integerShapePointEntry.getValue();
                shapeTotalDistance += distanceBetweenTwoPoints(origin.getShapePtLat(), destination.getShapePtLat(),
                        origin.getShapePtLon(), destination.getShapePtLon(), unit);
                origin = destination;
            }
        }
        return shapeTotalDistance;
    }
}
