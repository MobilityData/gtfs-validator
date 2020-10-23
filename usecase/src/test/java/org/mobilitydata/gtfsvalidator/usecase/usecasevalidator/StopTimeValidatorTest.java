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

package org.mobilitydata.gtfsvalidator.usecase.usecasevalidator;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.ShapePoint;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.stoptimes.StopTime;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.usecase.ValidateBackwardsTimeTravelForStops;
import org.mobilitydata.gtfsvalidator.usecase.ValidateShapeIdReferenceInStopTime;
import org.mobilitydata.gtfsvalidator.usecase.ValidateStopTimeIncreasingDistance;
import org.mobilitydata.gtfsvalidator.usecase.ValidateStopTimeTripId;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mobilitydata.gtfsvalidator.usecase.utils.TimeUtils;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.mockito.Mockito.*;

@SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
class StopTimeValidatorTest {

    @Test
    void allStopTimeCrossValidationUseCasesShouldBeCalled() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);

        final StopTime mockStopTime = mock(StopTime.class);
        final Map<String, TreeMap<Integer, StopTime>> mockStopTimeCollection = new HashMap<>();
        final TreeMap<Integer, StopTime> innerMap = new TreeMap<>();
        innerMap.put(1, mockStopTime);
        mockStopTimeCollection.put("trip id", innerMap);
        when(mockStopTime.getTripId()).thenReturn("trip id");

        final SortedMap<Integer, ShapePoint> mockShape = mock(TreeMap.class);
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getShapeId()).thenReturn("shape id");

        final Map<String, Trip> mockTripCollection = mock(HashMap.class);

        when(mockDataRepo.getStopTimeAll()).thenReturn(mockStopTimeCollection);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);
        when(mockDataRepo.getTripById("trip id")).thenReturn(mockTrip);
        when(mockDataRepo.getShapeById("shape id")).thenReturn(mockShape);

        final Logger mockLogger = mock(Logger.class);
        final ValidateShapeIdReferenceInStopTime mockE034 = mock(ValidateShapeIdReferenceInStopTime.class);
        final ValidateStopTimeTripId mockE037 = mock(ValidateStopTimeTripId.class);
        final ValidateBackwardsTimeTravelForStops mockE046 = mock(ValidateBackwardsTimeTravelForStops.class);
        final ValidateStopTimeIncreasingDistance mockE054AndW013 = mock(ValidateStopTimeIncreasingDistance.class);

        final TimeUtils mockTimeUtils = mock(TimeUtils.class);

        final StopTimeValidator underTest =
                spy(new StopTimeValidator(mockDataRepo, mockResultRepo, mockLogger, mockTimeUtils, mockE034,
                        mockE037, mockE046, mockE054AndW013));

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rules: 'E049 - Bad combination of stoptime arrival and departure times`");
        verify(mockLogger, times(1)).info("                  'E034 - `shape_id` not found");
        verify(mockLogger, times(1)).info("                  'E037 - `trip_id` not found");
        verify(mockLogger, times(1))
                .info("                  'E054 & W013 - Decreasing travelled distance");

        verify(mockDataRepo, times(1)).getStopTimeAll();
        verify(mockStopTime, times(1)).getTripId();
        verify(mockDataRepo, times(1)).getShapeById(ArgumentMatchers.eq("shape id"));
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockE046, times(1)).execute(mockResultRepo, innerMap, mockTimeUtils);
        verify(mockE034, times(1)).execute(ArgumentMatchers.eq(mockResultRepo),
                ArgumentMatchers.eq(mockStopTime), ArgumentMatchers.eq(mockShape), ArgumentMatchers.eq(mockTrip));

        verify(mockE037, times(1)).execute(ArgumentMatchers.eq(mockResultRepo),
                ArgumentMatchers.eq(mockStopTime), ArgumentMatchers.eq(mockTripCollection));
        verify(mockE054AndW013, times(1)).execute(innerMap);
    }
}
