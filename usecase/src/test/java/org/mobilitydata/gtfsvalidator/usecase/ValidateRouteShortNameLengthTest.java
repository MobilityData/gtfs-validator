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

package org.mobilitydata.gtfsvalidator.usecase;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.RouteShortNameTooLongNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ValidateRouteShortNameLengthTest {

    @Test
    void nullRouteColorShouldNotGenerateNotice() {
        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn(null);

        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteShortNameLength underTest = new ValidateRouteShortNameLength(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockLogger, times(1)).info("Validating rule 'W005 - Route short name " +
                "too long'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void lessThan12CharRouteShortNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short_name");

        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteShortNameLength underTest = new ValidateRouteShortNameLength(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockLogger, times(1)).info("Validating rule 'W005 - Route short name " +
                "too long'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void exact12CharRouteShortNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("a_short_name");

        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteShortNameLength underTest = new ValidateRouteShortNameLength(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockLogger, times(1)).info("Validating rule 'W005 - Route short name " +
                "too long'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void moreThan12CharRouteShortNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("a_very_long_name");

        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteShortNameLength underTest = new ValidateRouteShortNameLength(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(2)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockLogger, times(1)).info("Validating rule 'W005 - Route short name " +
                "too long'");
        verify(mockResultRepo, times(1)).addNotice(any(RouteShortNameTooLongNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }
}
