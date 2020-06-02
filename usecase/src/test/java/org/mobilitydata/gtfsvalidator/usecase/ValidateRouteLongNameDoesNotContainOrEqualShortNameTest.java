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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.RouteLongNameEqualsShortNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.RouteLongNameContainsShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ValidateRouteLongNameDoesNotContainOrEqualShortNameTest {

    @Test
    void nullLongRouteNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteLongNameDoesNotContainOrEqualShortName underTest = new ValidateRouteLongNameDoesNotContainOrEqualShortName(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteLongName();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void longRouteNameNotContainingShortNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteLongName()).thenReturn("This is a long name for route abc");
        when(mockRoute.getRouteShortName()).thenReturn("xyz");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteLongNameDoesNotContainOrEqualShortName underTest = new ValidateRouteLongNameDoesNotContainOrEqualShortName(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(2)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteShortName();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void longRouteNameContainingShortNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteLongName()).thenReturn("This is a long name for route xyz");
        when(mockRoute.getRouteShortName()).thenReturn("xyz");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteLongNameDoesNotContainOrEqualShortName underTest = new ValidateRouteLongNameDoesNotContainOrEqualShortName(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(2)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(RouteLongNameContainsShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void longRouteNameEqualingShortNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteLongName()).thenReturn("xyz");
        when(mockRoute.getRouteShortName()).thenReturn("xyz");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateRouteLongNameDoesNotContainOrEqualShortName underTest = new ValidateRouteLongNameDoesNotContainOrEqualShortName(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(2)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(RouteLongNameEqualsShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

}
