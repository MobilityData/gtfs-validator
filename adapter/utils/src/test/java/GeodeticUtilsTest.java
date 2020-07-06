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

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceCalculationUtils;
import org.mobilitydata.gtfsvalidator.usecase.distancecalculationutils.DistanceUnit;
import org.mobilitydata.gtfsvalidator.utils.GeodeticUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeodeticUtilsTest {
    private static final float MONTREAL_LATITUDE = 45.5017f;
    private static final float MONTREAL_LONGITUDE = -73.5673f;
    private static final float NEW_YORK_LATITUDE = 40.7128f;
    private static final float NEW_YORK_LONGITUDE = -74.0060f;
    private static final float POINTE_A_PITRE_LATITUDE = 16.2650f;
    private static final float POINTE_A_PITRE_LONGITUDE = -61.5510f;
    private static final float FORT_DE_FRANCE_LATITUDE = 14.6161f;
    private static final float FORT_DE_FRANCE_LONGITUDE = -61.0588f;

    @Test
    void distanceBetweenSamePointShouldBeZero(){
        assertEquals(0, new GeodeticUtils().distanceBetweenTwoPoints(
                        30,45,30,45,
                DistanceUnit.KILOMETER));
        assertEquals(0, new GeodeticUtils().distanceBetweenTwoPoints(
                        30,45,30,45,
                DistanceUnit.METER));
    }

    @Test
    void distanceBetweenMontrealAndPointeAPitreShouldBeSufficientlyAccurate() {
        final double distance =
                new GeodeticUtils().distanceBetweenTwoPoints(POINTE_A_PITRE_LATITUDE,POINTE_A_PITRE_LONGITUDE,
                        MONTREAL_LATITUDE, MONTREAL_LONGITUDE, DistanceUnit.KILOMETER);
        // see https://www.movable-type.co.uk/scripts/latlong.html
        assertTrue(3438-.5 <= distance && distance <= 3438+.5); // unit is kilometers
    }

    @Test
    void shapeTotalDistanceShouldBeTheSumOfDistancesBetweenShapePoints() {
        final Map<Integer, ShapePoint> mockShape = new HashMap<>();
        final DistanceCalculationUtils distanceCalculationUtils = new GeodeticUtils();

        // first shape point is located at Montréal (Canada)
        final ShapePoint firstShapePointInSequence = mock(ShapePoint.class);
        when(firstShapePointInSequence.getShapePtLat()).thenReturn(MONTREAL_LATITUDE);
        when(firstShapePointInSequence.getShapePtLon()).thenReturn(MONTREAL_LONGITUDE);

        // second shape point is located at New-York (USA)
        final ShapePoint secondShapePointInSequence = mock(ShapePoint.class);
        when(secondShapePointInSequence.getShapePtLat()).thenReturn(NEW_YORK_LATITUDE);
        when(secondShapePointInSequence.getShapePtLon()).thenReturn(NEW_YORK_LONGITUDE);

        // third shape point is located at Pointe-à-Pitre (Guadeloupe)
        final ShapePoint thirdShapePointInSequence = mock(ShapePoint.class);
        when(thirdShapePointInSequence.getShapePtLat()).thenReturn(POINTE_A_PITRE_LATITUDE);
        when(thirdShapePointInSequence.getShapePtLon()).thenReturn(POINTE_A_PITRE_LONGITUDE);

        // fourth shape point is located at Fort-de-France(Martinique)
        final ShapePoint fourthShapePointInSequence = mock(ShapePoint.class);
        when(fourthShapePointInSequence.getShapePtLat()).thenReturn(FORT_DE_FRANCE_LATITUDE);
        when(fourthShapePointInSequence.getShapePtLon()).thenReturn(FORT_DE_FRANCE_LONGITUDE);

        mockShape.put(1, firstShapePointInSequence);
        mockShape.put(8, secondShapePointInSequence);
        mockShape.put(10, thirdShapePointInSequence);
        mockShape.put(11, fourthShapePointInSequence);

        final double montrealNewYorkDistance = distanceCalculationUtils.distanceBetweenTwoPoints(MONTREAL_LATITUDE,
                MONTREAL_LONGITUDE, NEW_YORK_LATITUDE, NEW_YORK_LONGITUDE, DistanceUnit.KILOMETER);
        final double newYorkPointeAPitreDistance = distanceCalculationUtils.distanceBetweenTwoPoints(NEW_YORK_LATITUDE,
                NEW_YORK_LONGITUDE, POINTE_A_PITRE_LATITUDE, POINTE_A_PITRE_LONGITUDE, DistanceUnit.KILOMETER);
        final double pointeAPitreFortDeFranceDistance =
                distanceCalculationUtils.distanceBetweenTwoPoints(POINTE_A_PITRE_LATITUDE, POINTE_A_PITRE_LONGITUDE,
                        FORT_DE_FRANCE_LATITUDE, FORT_DE_FRANCE_LONGITUDE, DistanceUnit.KILOMETER);

        final double shapeTotalDistance = montrealNewYorkDistance + newYorkPointeAPitreDistance +
                pointeAPitreFortDeFranceDistance;

        assertEquals(shapeTotalDistance, distanceCalculationUtils.getShapeTotalDistance(mockShape,
                DistanceUnit.KILOMETER));
    }

    @Test
    void computeDistanceBetweenPointWithoutSpecifyingUnitShouldReturnResultInKilometer() {
        final DistanceCalculationUtils distanceCalculationUtils = new GeodeticUtils();

        assertEquals(distanceCalculationUtils.distanceBetweenTwoPoints(
                30,45,30,45, null),
                distanceCalculationUtils.distanceBetweenTwoPoints(
                30,45,30,45, DistanceUnit.KILOMETER));
    }
}
