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

package org.mobilitydata.gtfsvalidator.usecase.stoptimesshapestrips;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.usecase.crossvalidationusecase.stoptimesshapestrips.ShapeStopTimeTripCrossValidator;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

class ShapeStopTimeTripCrossValidatorTest {

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void notFoundTripIdShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final StopTime mockStopTime = mock(StopTime.class);
        when(mockStopTime.getShapeDistTraveled()).thenReturn(340f);

        final Map<String, TreeMap<Integer, StopTime>> stopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(3, mockStopTime);
        stopTimeCollection.put("non existing trip id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getStopTimeAll()).thenReturn(stopTimeCollection);
        when(mockDataRepo.getTripById("non existing trip id")).thenReturn(null);

        // suppressed warning regarding unchecked type since it is not required here
        // noinspection unchecked
        final Map<Integer, ShapePoint> mockShape = mock(TreeMap.class);
        when(mockDataRepo.getShapeById("shape id")).thenReturn(mockShape);
        final Logger mockLogger = mock(Logger.class);

        final ShapeStopTimeTripCrossValidator underTest =
                new ShapeStopTimeTripCrossValidator(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule 'E034 - `shape_id` not found" +
                System.lineSeparator());

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockDataRepo, times(1)).getTripById(
                ArgumentMatchers.eq("non existing trip id"));
        verify(mockDataRepo, times(1)).getShapeById(ArgumentMatchers.eq(null));

        verify(mockStopTime, times(1)).getShapeDistTraveled();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockDataRepo, mockLogger, mockStopTime, mockShape);
    }
}