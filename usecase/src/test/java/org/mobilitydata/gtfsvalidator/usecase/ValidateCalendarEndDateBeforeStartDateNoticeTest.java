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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.CalendarEndDateBeforeStartDateNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests to validate behavior of use case for "E032 - calendar.txt end_date is before start_date"
 */
class ValidateCalendarEndDateBeforeStartDateNoticeTest {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void startDateBeforeEndDateShouldNotGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDate.of(2020, 1, 1));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDate.of(2020, 2, 1)
        );

        Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);

        Logger mockLogger = mock(Logger.class);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateCalendarEndDateBeforeStartDate underTest = new ValidateCalendarEndDateBeforeStartDate(
                mockDataRepo,
                mockResultRepo,
                mockLogger);

        underTest.execute();

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockCalendar, times(1)).getStartDate();
        verify(mockCalendar, times(1)).getEndDate();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - calendar.txt end_date is " +
                "before start_date'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void sameStartAndEndDateShouldNotGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDate.of(2020, 1, 1));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDate.of(2020, 1, 1)
        );

        Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);

        Logger mockLogger = mock(Logger.class);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateCalendarEndDateBeforeStartDate underTest = new ValidateCalendarEndDateBeforeStartDate(
                mockDataRepo,
                mockResultRepo,
                mockLogger);

        underTest.execute();

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockCalendar, times(1)).getStartDate();
        verify(mockCalendar, times(1)).getEndDate();
        verify(mockLogger, times(1)).info("Validating rule 'E032 - calendar.txt end_date is " +
                "before start_date'");
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void endDateBeforeStartDateShouldGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDate.of(2020, 2, 1));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDate.of(2020, 1, 1)
        );

        Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);

        Logger mockLogger = mock(Logger.class);

        ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);

        ValidateCalendarEndDateBeforeStartDate underTest = new ValidateCalendarEndDateBeforeStartDate(
                mockDataRepo,
                mockResultRepo,
                mockLogger);

        underTest.execute();

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockCalendar, times(2)).getStartDate();
        verify(mockCalendar, times(2)).getEndDate();
        verify(mockCalendar, times(1)).getServiceId();
        verify(mockResultRepo, times(1))
                .addNotice(any(CalendarEndDateBeforeStartDateNotice.class));
        verify(mockLogger, times(1)).info("Validating rule 'E032 - calendar.txt end_date is " +
                "before start_date'");
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }
}
