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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.RouteType;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.InvalidRouteTypeNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

import static org.mockito.Mockito.*;

public class ValidateRouteTypeIsInTypeOptionsTest {

    @Test
    void notNullRouteTypeShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteType()).thenReturn(RouteType.SUBWAY);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteTypeIsInTypeOptions underTest = new ValidateRouteTypeIsInTypeOptions(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteType();
        verify(mockLogger, times(1)).info("Validating: E026 - Invalid route type\n");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void nullRouteTypeShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteType()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        Logger mockLogger = mock(Logger.class);
        ValidateRouteTypeIsInTypeOptions underTest = new ValidateRouteTypeIsInTypeOptions(
                mockDataRepo,
                mockResultRepo,
                mockLogger
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteType();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(InvalidRouteTypeNotice.class));
        verify(mockLogger, times(1)).info("Validating: E026 - Invalid route type\n");

        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo, mockLogger);
    }
}
