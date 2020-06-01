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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteLongNameNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.MissingRouteShortNameNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ValidateShortAndLongNameForRoutePresenceTest {

    @Test
    void presentRouteShortAndLongNameShouldNotGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(1)).getRouteLongName();
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void nullShortNameAndNullLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn(null);
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(2)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void blankShortNameAndNullLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("");
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(2)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void nullShortNameAndBlankLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn(null);
        when(mockRoute.getRouteLongName()).thenReturn("");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(2)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(ErrorNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void presentShortNameAndNullLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn(null);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteLongNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void presentShortNameAndBlankLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("short name");
        when(mockRoute.getRouteLongName()).thenReturn("");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteLongNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void nullShortNameAndPresentLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn(null);
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }

    @Test
    void blankShortNameAndPresentLongNameShouldGenerateNotice() {

        Route mockRoute = mock(Route.class);
        when(mockRoute.getRouteShortName()).thenReturn("");
        when(mockRoute.getRouteLongName()).thenReturn("long name");

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(List.of(mockRoute));

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateShortAndLongNameForRoutePresence underTest = new ValidateShortAndLongNameForRoutePresence(
                mockDataRepo,
                mockResultRepo
        );

        underTest.execute();

        verify(mockDataRepo, times(1)).getRouteAll();
        verify(mockRoute, times(1)).getRouteShortName();
        verify(mockRoute, times(3)).getRouteLongName();
        verify(mockRoute, times(1)).getRouteId();
        verify(mockResultRepo, times(1)).addNotice(any(MissingRouteShortNameNotice.class));
        verifyNoMoreInteractions(mockRoute, mockDataRepo, mockResultRepo);
    }
}
