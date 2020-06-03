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

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.routes.Route;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteColorAndTextInsufficientContrastNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ValidateRouteColorAndTextContrastTest {

    @Test
    void nullRouteColorShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteColor()).thenReturn(null);
        when(mockRoute.getRouteTextColor()).thenReturn("000000");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteColorAndTextContrast underTest = new ValidateRouteColorAndTextContrast(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteColor();
        verify(mockRoute, times(1)).getRouteTextColor();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void nullRouteTextColorShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteColor()).thenReturn("ffffff");
        when(mockRoute.getRouteTextColor()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteColorAndTextContrast underTest = new ValidateRouteColorAndTextContrast(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteColor();
        verify(mockRoute, times(1)).getRouteTextColor();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void oppositeRouteColorAndTextShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteColor()).thenReturn("ffffff");
        when(mockRoute.getRouteTextColor()).thenReturn("000000");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteColorAndTextContrast underTest = new ValidateRouteColorAndTextContrast(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteColor();
        verify(mockRoute, times(1)).getRouteTextColor();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void sameRouteColorAndTextShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteColor()).thenReturn("ffffff");
        when(mockRoute.getRouteTextColor()).thenReturn("ffffff");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteColorAndTextContrast underTest = new ValidateRouteColorAndTextContrast(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(2)).getRouteColor();
        verify(mockRoute, times(2)).getRouteTextColor();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1))
                .addNotice(any(RouteColorAndTextInsufficientContrastNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void similarRouteColorAndTextShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteColor()).thenReturn("a5ff00");
        when(mockRoute.getRouteTextColor()).thenReturn("a6fe03");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteColorAndTextContrast underTest = new ValidateRouteColorAndTextContrast(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(2)).getRouteColor();
        verify(mockRoute, times(2)).getRouteTextColor();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1))
                .addNotice(any(RouteColorAndTextInsufficientContrastNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }
}
