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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.DuplicateRouteLongNameRouteShortNameCombinationNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.*;
import static org.mockito.Mockito.*;

class ValidateUniqueRouteLongNameRouteShortNameCombinationTest {

    // suppressed warning regarding ignored result of method because method is called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void uniqueRouteNameCombinationShouldNotGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Map<String, Route> mockRouteCollection = new HashMap<>();

        final Route mockRoute0 = mock(Route.class);
        when(mockRoute0.getRouteLongName()).thenReturn("route 0 long name");
        when(mockRoute0.getRouteShortName()).thenReturn("route 0 short name");

        final Route mockRoute1 = mock(Route.class);
        when(mockRoute1.getRouteId()).thenReturn("route id1");
        when(mockRoute1.getRouteLongName()).thenReturn("route 1 long name");
        when(mockRoute1.getRouteShortName()).thenReturn("route 1 short name");

        final Route mockRoute2 = mock(Route.class);
        when(mockRoute2.getRouteId()).thenReturn("route id2");
        when(mockRoute2.getRouteLongName()).thenReturn("route 2 long name");
        when(mockRoute2.getRouteShortName()).thenReturn("route 2 short name");

        final Route mockRoute3 = mock(Route.class);
        when(mockRoute3.getRouteLongName()).thenReturn("route 3 long name");
        when(mockRoute3.getRouteShortName()).thenReturn("route 3 short name");

        mockRouteCollection.put("route id0", mockRoute0);
        mockRouteCollection.put("route id1", mockRoute1);
        mockRouteCollection.put("route id2", mockRoute2);
        mockRouteCollection.put("route id3", mockRoute3);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final Logger mockLogger = mock(Logger.class);
        final ValidateUniqueRouteLongNameRouteShortNameCombination underTest =
                new ValidateUniqueRouteLongNameRouteShortNameCombination(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'W016 - Duplicate combination od fields`route_short_name` and `route_long_name`'");
        verify(mockDataRepo, times(1)).getRouteAll();

        verify(mockRoute0, times(1)).getRouteLongName();
        verify(mockRoute0, times(1)).getRouteShortName();
        verify(mockRoute1, times(1)).getRouteLongName();
        verify(mockRoute1, times(1)).getRouteShortName();
        verify(mockRoute2, times(1)).getRouteLongName();
        verify(mockRoute2, times(1)).getRouteShortName();
        verify(mockRoute3, times(1)).getRouteLongName();
        verify(mockRoute3, times(1)).getRouteShortName();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockRoute0, mockRoute1, mockRoute2, mockRoute3, mockLogger, mockDataRepo);
    }

    // suppressed warning regarding ignored result of method because method is called in assertions
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void duplicateRouteNameCombinationShouldGenerateNotice() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Map<String, Route> mockRouteCollection = new HashMap<>();

        final Route mockRoute0 = mock(Route.class);
        when(mockRoute0.getRouteLongName()).thenReturn("route 0 long name");
        when(mockRoute0.getRouteShortName()).thenReturn("route 0 short name");

        final Route mockRoute1 = mock(Route.class);
        when(mockRoute1.getRouteId()).thenReturn("route id1");
        when(mockRoute1.getRouteLongName()).thenReturn("duplicate route long name");
        when(mockRoute1.getRouteShortName()).thenReturn("duplicate route short name");
        when(mockRoute1.getRouteId()).thenReturn("route id1");

        final Route mockRoute2 = mock(Route.class);
        when(mockRoute2.getRouteId()).thenReturn("route id2");
        when(mockRoute2.getRouteLongName()).thenReturn("duplicate route long name");
        when(mockRoute2.getRouteShortName()).thenReturn("duplicate route short name");
        when(mockRoute2.getRouteId()).thenReturn("route id2");

        final Route mockRoute3 = mock(Route.class);
        when(mockRoute3.getRouteLongName()).thenReturn("route 3 long name");
        when(mockRoute3.getRouteShortName()).thenReturn("route 3 short name");

        mockRouteCollection.put("route id0", mockRoute0);
        mockRouteCollection.put("route id1", mockRoute1);
        mockRouteCollection.put("route id2", mockRoute2);
        mockRouteCollection.put("route id3", mockRoute3);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getRouteAll()).thenReturn(mockRouteCollection);

        final Logger mockLogger = mock(Logger.class);
        final ValidateUniqueRouteLongNameRouteShortNameCombination underTest =
                new ValidateUniqueRouteLongNameRouteShortNameCombination(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1))
                .info("Validating rule 'W016 - Duplicate combination od fields`route_short_name` and `route_long_name`'");
        verify(mockDataRepo, times(1)).getRouteAll();

        verify(mockRoute0, times(1)).getRouteLongName();
        verify(mockRoute0, times(1)).getRouteShortName();
        verify(mockRoute1, times(1)).getRouteLongName();
        verify(mockRoute1, times(1)).getRouteShortName();
        verify(mockRoute2, times(1)).getRouteLongName();
        verify(mockRoute2, times(1)).getRouteId();
        verify(mockRoute2, times(1)).getRouteShortName();
        verify(mockRoute3, times(1)).getRouteLongName();
        verify(mockRoute3, times(1)).getRouteShortName();

        final ArgumentCaptor<DuplicateRouteLongNameRouteShortNameCombinationNotice> captor =
                ArgumentCaptor.forClass(DuplicateRouteLongNameRouteShortNameCombinationNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicateRouteLongNameRouteShortNameCombinationNotice> noticeList = captor.getAllValues();

        assertEquals("routes.txt", noticeList.get(0).getFilename());
        assertEquals("route id1", noticeList.get(0).getEntityId());
        assertEquals("route id2", noticeList.get(0).getNoticeSpecific(KEY_ROUTE_CONFLICTING_ROUTE_ID));
        assertEquals("duplicate route long name",
                noticeList.get(0).getNoticeSpecific(KEY_ROUTE_DUPLICATE_ROUTE_LONG_NAME));
        assertEquals("duplicate route short name",
                noticeList.get(0).getNoticeSpecific(KEY_ROUTE_DUPLICATE_ROUTE_SHORT_NAME));

        verifyNoMoreInteractions(mockRoute0, mockRoute1, mockRoute2, mockRoute3, mockResultRepo, mockLogger,
                mockDataRepo);
    }
}
