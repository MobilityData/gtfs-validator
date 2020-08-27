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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingShortAndLongNameForRouteNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteLongNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ValidateShortAndLongNameForRoutePresenceTest {

    @Test
    void presentRouteShortAndLongNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void blankShortNameAndNullLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("");
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(2)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();

        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verify(mockResultRepo, times(1)).addNotice(any(MissingShortAndLongNameForRouteNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void presentShortNameAndNullLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();

        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteLongNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void presentShortNameAndBlankLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn("");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteLongNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void nullShortNameAndPresentLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn(null);
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();

        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void blankShortNameAndPresentLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("");
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        Map<String, Route> mockRouteCollection = new HashMap<>();
        mockRouteCollection.put("route id", mockRoute);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();

        verify(mockLogger, times(1)).info("Validating rule 'E027 - Missing route short name " +
                "and long name'");
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }
}
