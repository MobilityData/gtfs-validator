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

package org.mobilitydata.gtfsvalidator.usecase.crossvalidation;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.ValidateShapeUsage;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.mockito.Mockito.*;

class ShapeBasedCrossValidatorTest {

    // suppressed warning regarding unused result of method, since this behavior is wanted
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void allShapeCrossValidationUseCasesShouldBeCalled() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn("shape id");
        final ShapePoint mockShapePoint = mock(ShapePoint.class);
        when(mockShapePoint.getShapeId()).thenReturn("shape id");

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);

        final Map<String, SortedMap<Integer, ShapePoint>> mockShapeCollection = new HashMap<>();
        final SortedMap<Integer, ShapePoint> innerMap = new TreeMap<>();
        innerMap.put(1, mockShapePoint);
        mockShapeCollection.put("shape id", innerMap);

        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);
        when(mockDataRepo.getShapeAll()).thenReturn(mockShapeCollection);

        final Set<String> mockTripShapeIdCollection = new HashSet<>(List.of("shape id"));

        final Logger mockLogger = mock(Logger.class);
        final ValidateShapeUsage mockE038 = mock(ValidateShapeUsage.class);

        final ShapeBasedCrossValidator underTest =
                new ShapeBasedCrossValidator(mockDataRepo, mockResultRepo, mockLogger, mockE038);

        underTest.execute();

        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip, times(1)).getShapeId();

        verify(mockDataRepo, times(1)).getShapeAll();

        verify(mockShapePoint, times(1)).getShapeId();

        verify(mockE038, times(1)).execute(ArgumentMatchers.eq(mockResultRepo),
                ArgumentMatchers.eq("shape id"), ArgumentMatchers.eq(mockTripShapeIdCollection));
    }
}
