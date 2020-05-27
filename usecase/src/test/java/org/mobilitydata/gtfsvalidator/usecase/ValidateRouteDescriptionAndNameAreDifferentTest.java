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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

import static org.mockito.Mockito.*;

class ValidateRouteDescriptionAndNameAreDifferentTest {

    @Test
    void nullRouteDescriptionShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteDesc()).thenReturn(null);
        when(mockRoute.getRouteShortName()).thenReturn("route_short_name");
        when(mockRoute.getRouteLongName()).thenReturn("route_long_name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateRouteDescriptionAndNameAreDifferent underTest = new ValidateRouteDescriptionAndNameAreDifferent(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteDesc();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteLongName();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void differentRouteDescriptionAndNamesShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteDesc()).thenReturn("route_description");
        when(mockRoute.getRouteShortName()).thenReturn("route_short_name");
        when(mockRoute.getRouteLongName()).thenReturn("route_long_name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateRouteDescriptionAndNameAreDifferent underTest = new ValidateRouteDescriptionAndNameAreDifferent(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteDesc();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteLongName();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void sameRouteDescriptionAndShortNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteDesc()).thenReturn("route_short_name");
        when(mockRoute.getRouteShortName()).thenReturn("route_short_name");
        when(mockRoute.getRouteLongName()).thenReturn("route_long_name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateRouteDescriptionAndNameAreDifferent underTest = new ValidateRouteDescriptionAndNameAreDifferent(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteDesc();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void sameRouteDescriptionAndLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteDesc()).thenReturn("route_long_name");
        when(mockRoute.getRouteShortName()).thenReturn("route_short_name");
        when(mockRoute.getRouteLongName()).thenReturn("route_long_name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);

        ValidateRouteDescriptionAndNameAreDifferent underTest = new ValidateRouteDescriptionAndNameAreDifferent(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteDesc();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }
}