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
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.DuplicatedEntityNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.usecase.port.GtfsDataRepository;
import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.*;

class ProcessParsedCalendarTest {
    private static final String SERVICE_ID = "service_id";
    private static final String MONDAY = "monday";
    private static final String TUESDAY = "tuesday";
    private static final String WEDNESDAY = "wednesday";
    private static final String THURSDAY = "thursday";
    private static final String FRIDAY = "friday";
    private static final String SATURDAY = "saturday";
    private static final String SUNDAY = "sunday";
    private static final String START_DATE = "start_date";
    private static final String END_DATE = "end_date";

    @Test
    public void validatedParsedCalendarShouldCreateEntityAndBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Calendar.CalendarBuilder mockBuilder = mock(Calendar.CalendarBuilder.class, RETURNS_SELF);
        final Calendar mockCalendar = mock(Calendar.class);
        final ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockCalendar);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        when(mockParsedCalendar.get(SERVICE_ID)).thenReturn(SERVICE_ID);
        when(mockParsedCalendar.get(MONDAY)).thenReturn(0);
        when(mockParsedCalendar.get(TUESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(WEDNESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(THURSDAY)).thenReturn(0);
        when(mockParsedCalendar.get(FRIDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SATURDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SUNDAY)).thenReturn(0);
        when(mockParsedCalendar.get(START_DATE)).thenReturn(LocalDate.now());
        when(mockParsedCalendar.get(END_DATE)).thenReturn(LocalDate.now());

        when(mockGtfsDataRepo.addCalendar(mockCalendar)).thenReturn(mockCalendar);

        final ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        underTest.execute(mockParsedCalendar);

        verify(mockParsedCalendar, times(1)).get(SERVICE_ID);
        verify(mockParsedCalendar, times(1)).get(MONDAY);
        verify(mockParsedCalendar, times(1)).get(TUESDAY);
        verify(mockParsedCalendar, times(1)).get(WEDNESDAY);
        verify(mockParsedCalendar, times(1)).get(THURSDAY);
        verify(mockParsedCalendar, times(1)).get(FRIDAY);
        verify(mockParsedCalendar, times(1)).get(SATURDAY);
        verify(mockParsedCalendar, times(1)).get(SUNDAY);
        verify(mockParsedCalendar, times(1)).get(START_DATE);
        verify(mockParsedCalendar, times(1)).get(END_DATE);

        verify(mockBuilder, times(1)).clearFieldAll();
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId(SERVICE_ID);
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDate.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDate.class));

        final InOrder inOrder = inOrder(mockBuilder, mockGtfsDataRepo);

        inOrder.verify(mockBuilder, times(1)).build();

        inOrder.verify(mockGtfsDataRepo, times(1)).addCalendar(ArgumentMatchers.eq(mockCalendar));

        verifyNoMoreInteractions(mockBuilder, mockCalendar, mockParsedCalendar, mockGtfsDataRepo);
    }

    @Test
    public void invalidCalendarShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Calendar.CalendarBuilder mockBuilder = mock(Calendar.CalendarBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);

        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = spy(ArrayList.class);
        final MissingRequiredValueNotice mockNotice = mock(MissingRequiredValueNotice.class);
        mockNoticeCollection.add(mockNotice);

        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.isSuccess()).thenReturn(false);
        when(mockGenericObject.getData()).thenReturn(mockNoticeCollection);

        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);

        final ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo, mockBuilder);

        when(mockParsedCalendar.get(SERVICE_ID)).thenReturn(null);
        when(mockParsedCalendar.get(MONDAY)).thenReturn(0);
        when(mockParsedCalendar.get(TUESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(WEDNESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(THURSDAY)).thenReturn(0);
        when(mockParsedCalendar.get(FRIDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SATURDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SUNDAY)).thenReturn(0);
        when(mockParsedCalendar.get(START_DATE)).thenReturn(LocalDate.now());
        when(mockParsedCalendar.get(END_DATE)).thenReturn(LocalDate.now());

        underTest.execute(mockParsedCalendar);

        verify(mockParsedCalendar, times(1)).get(SERVICE_ID);
        verify(mockParsedCalendar, times(1)).get(MONDAY);
        verify(mockParsedCalendar, times(1)).get(TUESDAY);
        verify(mockParsedCalendar, times(1)).get(WEDNESDAY);
        verify(mockParsedCalendar, times(1)).get(THURSDAY);
        verify(mockParsedCalendar, times(1)).get(FRIDAY);
        verify(mockParsedCalendar, times(1)).get(SATURDAY);
        verify(mockParsedCalendar, times(1)).get(SUNDAY);
        verify(mockParsedCalendar, times(1)).get(START_DATE);
        verify(mockParsedCalendar, times(1)).get(END_DATE);

        verify(mockBuilder, times(1)).clearFieldAll();
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        //noinspection ConstantConditions
        verify(mockBuilder, times(1)).serviceId(null);
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDate.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDate.class));
        verify(mockBuilder, times(1)).build();

        verify(mockResultRepo, times(1)).addNotice(isA(Notice.class));
        verifyNoMoreInteractions(mockParsedCalendar, mockGtfsDataRepo, mockBuilder, mockResultRepo);
    }

    @Test
    public void duplicateCalendarShouldAddNoticeToResultRepoAndShouldNotBeAddedToGtfsDataRepo() {
        final ValidationResultRepository mockResultRepo = mock(ValidationResultRepository.class);
        final GtfsDataRepository mockGtfsDataRepo = mock(GtfsDataRepository.class);
        final Calendar.CalendarBuilder mockBuilder = mock(Calendar.CalendarBuilder.class, RETURNS_SELF);
        final ParsedEntity mockParsedCalendar = mock(ParsedEntity.class);
        final Calendar mockCalendar = mock(Calendar.class);
        @SuppressWarnings("rawtypes") final EntityBuildResult mockGenericObject = mock(EntityBuildResult.class);

        when(mockGenericObject.getData()).thenReturn(mockCalendar);
        when(mockGenericObject.isSuccess()).thenReturn(true);

        when(mockCalendar.getServiceId()).thenReturn(SERVICE_ID);
        //noinspection unchecked
        when(mockBuilder.build()).thenReturn(mockGenericObject);
        when(mockGtfsDataRepo.addCalendar(mockCalendar)).thenReturn(null);

        final ProcessParsedCalendar underTest = new ProcessParsedCalendar(mockResultRepo, mockGtfsDataRepo,
                mockBuilder);

        when(mockParsedCalendar.get(SERVICE_ID)).thenReturn(SERVICE_ID);
        when(mockParsedCalendar.get(MONDAY)).thenReturn(0);
        when(mockParsedCalendar.get(TUESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(WEDNESDAY)).thenReturn(0);
        when(mockParsedCalendar.get(THURSDAY)).thenReturn(0);
        when(mockParsedCalendar.get(FRIDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SATURDAY)).thenReturn(0);
        when(mockParsedCalendar.get(SUNDAY)).thenReturn(0);
        when(mockParsedCalendar.get(START_DATE)).thenReturn(LocalDate.now());
        when(mockParsedCalendar.get(END_DATE)).thenReturn(LocalDate.now());

        underTest.execute(mockParsedCalendar);

        verify(mockParsedCalendar, times(1)).get(SERVICE_ID);
        verify(mockParsedCalendar, times(1)).get(MONDAY);
        verify(mockParsedCalendar, times(1)).get(TUESDAY);
        verify(mockParsedCalendar, times(1)).get(WEDNESDAY);
        verify(mockParsedCalendar, times(1)).get(THURSDAY);
        verify(mockParsedCalendar, times(1)).get(FRIDAY);
        verify(mockParsedCalendar, times(1)).get(SATURDAY);
        verify(mockParsedCalendar, times(1)).get(SUNDAY);
        verify(mockParsedCalendar, times(1)).get(START_DATE);
        verify(mockParsedCalendar, times(1)).get(END_DATE);
        //noinspection ResultOfMethodCallIgnored
        verify(mockParsedCalendar, times(1)).getEntityId();

        verify(mockGtfsDataRepo, times(1)).addCalendar(ArgumentMatchers.eq(mockCalendar));

        verify(mockBuilder, times(1)).clearFieldAll();
        verify(mockBuilder, times(1)).monday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).tuesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).wednesday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).thursday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).friday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).saturday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).sunday(ArgumentMatchers.eq(0));
        verify(mockBuilder, times(1)).serviceId(SERVICE_ID);
        verify(mockBuilder, times(1)).startDate(ArgumentMatchers.isA(LocalDate.class));
        verify(mockBuilder, times(1)).endDate(ArgumentMatchers.isA(LocalDate.class));
        verify(mockBuilder, times(1)).build();

        verify(mockGtfsDataRepo, times(1)).addCalendar(mockCalendar);

        final ArgumentCaptor<DuplicatedEntityNotice> captor = ArgumentCaptor.forClass(DuplicatedEntityNotice.class);

        verify(mockResultRepo, times(1)).addNotice(captor.capture());

        final List<DuplicatedEntityNotice> noticeList = captor.getAllValues();

        assertEquals("calendar.txt", noticeList.get(0).getFilename());
        assertEquals(SERVICE_ID, noticeList.get(0).getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", noticeList.get(0).getEntityId());

        verifyNoMoreInteractions(mockBuilder, mockGtfsDataRepo, mockResultRepo, mockParsedCalendar, mockCalendar);
    }
}