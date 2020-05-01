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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CalendarDateTest {

    @Test
    void createCalendarDateWithValidValueShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder(mockNoticeCollection);
        underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(1);
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullServiceIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId(null)
                .date(LocalDateTime.now())
                .exceptionType(1);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("service_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field date is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullDateShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId("service_id")
                .date(null)
                .exceptionType(1);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("date", noticeList.get(0).getFieldName());
        assertEquals("service_id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    // Field exceptionType is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullExceptionTypeShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(null);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("exception_type", noticeList.get(0).getFieldName());
        assertEquals("service_id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createCalendarDateWithInvalidExceptionTypeShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder(mockNoticeCollection);

        underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(5);

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<UnexpectedEnumValueNotice> captor =
                ArgumentCaptor.forClass(UnexpectedEnumValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<UnexpectedEnumValueNotice> noticeList = captor.getAllValues();

        assertEquals("calendar_dates.txt", noticeList.get(0).getFilename());
        assertEquals("exception_type", noticeList.get(0).getFieldName());
        assertEquals("service_id", noticeList.get(0).getEntityId());
        assertEquals("5", noticeList.get(0).getEnumValue());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}