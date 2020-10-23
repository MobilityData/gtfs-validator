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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates.CalendarDate;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.trips.Trip;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.ServiceIdNotFoundNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_UNKNOWN_SERVICE_ID;
import static org.mockito.Mockito.*;

class ValidateTripServiceIdTest {

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void serviceIdInCalendarShouldNotGenerateNotice() {
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getServiceId()).thenReturn("service id");
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getServiceId()).thenReturn("service id");
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final Map<String, CalendarDate> innerMap = new HashMap<>();
        innerMap.put("date", mockCalendarDate);
        mockCalendarDateCollection.put("other service id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripServiceId underTest = new ValidateTripServiceId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule E036 - `service_id` not found");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getCalendarDateAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip, times(1)).getServiceId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockCalendarDate, mockTrip, mockDataRepo, mockLogger);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void serviceIdInCalendarDateShouldNotGenerateNotice() {
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getServiceId()).thenReturn("other service id");
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getServiceId()).thenReturn("service id");
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("other service id", mockCalendar);

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        final Map<String, CalendarDate> innerMap = new HashMap<>();
        innerMap.put("date", mockCalendarDate);
        mockCalendarDateCollection.put("service id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripServiceId underTest = new ValidateTripServiceId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule E036 - `service_id` not found");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getCalendarDateAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip, times(2)).getServiceId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockCalendarDate, mockTrip, mockDataRepo, mockLogger);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void serviceIdInBothCalendarDateAndCalendarShouldNotGenerateNotice() {
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getServiceId()).thenReturn("service id");
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getServiceId()).thenReturn("service id");
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        final Map<String, CalendarDate> innerMap = new HashMap<>();
        innerMap.put("date", mockCalendarDate);
        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);

        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        mockCalendarDateCollection.put("service id", innerMap);


        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripServiceId underTest = new ValidateTripServiceId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule E036 - `service_id` not found");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getCalendarDateAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip, times(1)).getServiceId();

        verifyNoInteractions(mockResultRepo);
        verifyNoMoreInteractions(mockCalendar, mockCalendarDate, mockTrip, mockDataRepo, mockLogger);
    }

    // suppress warning regarding ignored result of method since it is not necessary here.
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    void serviceIdNotInCalendarNorCalendarDateShouldGenerateNotice() {
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getServiceId()).thenReturn("service id");
        final Trip mockTrip = mock(Trip.class);
        when(mockTrip.getTripId()).thenReturn("trip id");
        when(mockTrip.getServiceId()).thenReturn("not found service id");
        final CalendarDate mockCalendarDate = mock(CalendarDate.class);

        final Map<String, Calendar> mockCalendarCollection = new HashMap<>();
        mockCalendarCollection.put("service id", mockCalendar);

        final Map<String, Trip> mockTripCollection = new HashMap<>();
        mockTripCollection.put("trip id", mockTrip);

        final Map<String, CalendarDate> innerMap = new HashMap<>();
        innerMap.put("date", mockCalendarDate);
        final Map<String, Map<String, CalendarDate>> mockCalendarDateCollection = new HashMap<>();
        mockCalendarDateCollection.put("service id", innerMap);

        final GtfsDataRepository mockDataRepo = mock(GtfsDataRepository.class);
        when(mockDataRepo.getCalendarAll()).thenReturn(mockCalendarCollection);
        when(mockDataRepo.getCalendarDateAll()).thenReturn(mockCalendarDateCollection);
        when(mockDataRepo.getTripAll()).thenReturn(mockTripCollection);

        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final Logger mockLogger = mock(Logger.class);

        final ValidateTripServiceId underTest = new ValidateTripServiceId(mockDataRepo, mockResultRepo, mockLogger);

        underTest.execute();

        verify(mockLogger, times(1)).info("Validating rule E036 - `service_id` not found");

        verify(mockDataRepo, times(1)).getCalendarAll();
        verify(mockDataRepo, times(1)).getCalendarDateAll();
        verify(mockDataRepo, times(1)).getTripAll();

        verify(mockTrip, times(1)).getTripId();
        verify(mockTrip, times(3)).getServiceId();

        final ArgumentCaptor<ServiceIdNotFoundNotice> captor = ArgumentCaptor.forClass(ServiceIdNotFoundNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<ServiceIdNotFoundNotice> noticeList = captor.getAllValues();

        assertEquals("trips.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("trip id", noticeList.get(0).getEntityId());
        assertEquals("not found service id", noticeList.get(0).getNoticeSpecific(KEY_UNKNOWN_SERVICE_ID));
        verifyNoMoreInteractions(mockCalendar, mockCalendarDate, mockTrip, mockDataRepo, mockLogger);
    }
}