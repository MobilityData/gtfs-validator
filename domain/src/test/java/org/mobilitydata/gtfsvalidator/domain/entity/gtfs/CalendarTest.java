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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CalendarTest {

    private static final String FILENAME = "calendar.txt";
    private static final String SERVICE_ID = "service_id";

    @Test
    public void createCalendarWithNullServiceIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId(null)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals(SERVICE_ID, noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullStartDateShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(null)
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("start_date", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullEndDateShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(null);

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("end_date", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidMondayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(3)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("monday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createCalendarWithNullMondayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(null)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("monday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidTuesdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(3)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("tuesday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    void createCalendarWithNullTuesdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(null)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("tuesday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidWednesdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(3)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("wednesday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullWednesdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(null)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("wednesday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidThursdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(3)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("thursday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullThursdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(null)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("thursday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidFridayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(3)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("friday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullFridayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(null)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("friday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidSaturdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(3)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("saturday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(buildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullSaturdayValueShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(null)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> buildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("saturday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(buildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithInvalidSundayValueShouldThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(3)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<IntegerFieldValueOutOfRangeNotice> captor =
                ArgumentCaptor.forClass(IntegerFieldValueOutOfRangeNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<IntegerFieldValueOutOfRangeNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("sunday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());
        assertEquals(3, noticeList.get(0).getActualValue());
        assertEquals(1, noticeList.get(0).getRangeMax());
        assertEquals(0, noticeList.get(0).getRangeMin());

        assertTrue(entityBuildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithNullSundayValueShouldThrowException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(null)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals(FILENAME, noticeList.get(0).getFilename());
        assertEquals("sunday", noticeList.get(0).getFieldName());
        assertEquals(SERVICE_ID, noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);

        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createCalendarWithValidValuesShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(ArrayList.class);
        final Calendar.CalendarBuilder underTest = new Calendar.CalendarBuilder(mockNoticeCollection);

        underTest.serviceId(SERVICE_ID)
                .monday(0)
                .tuesday(0)
                .wednesday(0)
                .thursday(0)
                .friday(0)
                .saturday(0)
                .sunday(0)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now());

        assertTrue(underTest.build().getData() instanceof Calendar);
        verify(mockNoticeCollection, times(1)).clear();
        verifyNoMoreInteractions(mockNoticeCollection);
    }
}