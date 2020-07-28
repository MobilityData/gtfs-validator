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

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentMatchers;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

class ValidateTripRouteIdTest {
    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void tripWithInvalidReferenceToRouteShouldGenerateNotice() {
        final Trip mockTrip00 = mock(Trip.class);
        when(mockTrip00.getTripId()).thenReturn("trip id 00");
        when(mockTrip00.getRouteId()).thenReturn("route id 00");

        final Trip mockTrip01 = mock(Trip.class);
        when(mockTrip01.getTripId()).thenReturn("trip id 01");
        when(mockTrip01.getRouteId()).thenReturn("route id 01");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id 00", mockTrip00);
        mockTripCollection.put("trip id 01", mockTrip01);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);
        when(mockDataRepo.getRouteAll()).thenReturn(new HashMap<>());

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripRouteId underTest = new ValidateTripRouteId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E033 - `route_id` not found"));

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip00, times(2)).getRouteId();
        verify(mockTrip00, times(1)).getTripId();
        verify(mockTrip01, times(2)).getRouteId();
        verify(mockTrip01, times(1)).getTripId();
        verify(mockResultRepo, times(2)).addNotice(any(RouteIdNotFoundNotice.class));

        verifyNoMoreInteractions(mockTrip00, mockTrip01, mockDataRepo, mockResultRepo, mockLogger);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void tripWithValidReferenceToRouteShouldNotGenerateNotice() {
        final Trip mockTrip00 = mock(Trip.class);
        when(mockTrip00.getTripId()).thenReturn("trip id 00");
        when(mockTrip00.getRouteId()).thenReturn("route id 00");

        final Trip mockTrip01 = mock(Trip.class);
        when(mockTrip01.getTripId()).thenReturn("trip id 01");
        when(mockTrip01.getRouteId()).thenReturn("route id 01");

        final Route mockRoute00 = mock(Route.class);
        when(mockRoute00.getRouteId()).thenReturn("route id 00");

        final Route mockRoute01 = mock(Route.class);
        when(mockRoute01.getRouteId()).thenReturn("route id 01");

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id 00", mockTrip00);
        mockTripCollection.put("trip id 01", mockTrip01);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id 00", mockRoute00);
        mockRouteCollection.put("route id 01", mockRoute01);

        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripRouteId underTest = new ValidateTripRouteId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info(ArgumentMatchers.eq(
                "Validating rule E033 - `route_id` not found"));

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip00, times(1)).getRouteId();
        verify(mockTrip01, times(1)).getRouteId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockTrip00, mockTrip01, mockRoute00, mockRoute01, mockDataRepo, mockLogger);
    }
}
