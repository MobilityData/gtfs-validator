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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests to validate behavior of use case for "E032 - calendar.txt end_date is before start_date"
 */
class ValidateCalendarEndDateBeforeStartDateNoticeTest {

    @Test
    void startDateBeforeEndDateShouldNotGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDateTime.of(2020, 1, 1, 12, 35, 59));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDateTime.of(2020, 2, 1, 12, 35, 59)
        );

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(List.of(mockCalendar));

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
                "before start_date'" + System.lineSeparator());
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void sameStartAndEndDateShouldNotGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDateTime.of(2020, 1, 1, 12, 35, 59));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDateTime.of(2020, 1, 1, 12, 35, 59)
        );

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(List.of(mockCalendar));

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
                "before start_date'" + System.lineSeparator());
        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }

    @Test
    void endDateBeforeStartDateShouldGenerateNotice() {
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(
                LocalDateTime.of(2020, 2, 1, 12, 35, 59));
        when(mockCalendar.getEndDate()).thenReturn(
                LocalDateTime.of(2020, 1, 1, 12, 35, 59)
        );

        GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(List.of(mockCalendar));

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
                "before start_date'" + System.lineSeparator());
        verifyNoMoreInteractions(mockCalendar, mockDataRepo, mockResultRepo, mockLogger);
    }
}
