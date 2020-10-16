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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.Calendar;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_ENUM_VALUE;
import static org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice.KEY_FIELD_NAME;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CalendarDateTest {

    @Test
    void createCalendarDateWithValidValueShouldNotGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        assertTrue(entityBuildResult.getData() instanceof CalendarDate);
    }

    // Field serviceId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullServiceIdShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId(null)
                .date(LocalDate.now())
                .exceptionType(1)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // This test is designed so that call to .getData () method returns a list of notices. Therefore , there is no
        // need for cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("service_id", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("no id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field date is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullDateShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(null)
                .exceptionType(1)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);

        // This test is designed so that call to .getData () method returns a list of notices. Therefore , there is no
        // need for cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("date", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("service_id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    // Field exceptionType is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    void createCalendarDateWithNullExceptionTypeShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDate.now())
                .exceptionType(null)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);

        // This test is designed so that call to .getData () method returns a list of notices. Therefore , there is no
        // need for cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("exception_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("service_id", notice.getEntityId());
        assertEquals(1, noticeCollection.size());
    }

    @Test
    void createCalendarDateWithInvalidExceptionTypeShouldGenerateNotice() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();

        final EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id")
                .date(LocalDate.now())
                .exceptionType(5)
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);

        // This test is designed so that call to .getData () method returns a list of notices. Therefore , there is no
        // need for cast check
        //noinspection unchecked
        final List<UnexpectedEnumValueNotice> noticeCollection =
                (List<UnexpectedEnumValueNotice>) entityBuildResult.getData();
        final UnexpectedEnumValueNotice notice = noticeCollection.get(0);

        assertEquals("calendar_dates.txt", notice.getFilename());
        assertEquals("exception_type", notice.getNoticeSpecific(KEY_FIELD_NAME));
        assertEquals("service_id", notice.getEntityId());
        assertEquals(5, notice.getNoticeSpecific(KEY_ENUM_VALUE));
        assertEquals(1, noticeCollection.size());
    }

    // suppressed warning, will keep the current implementation for legibility
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void equalsShouldReturnFalseWhenCalendarDatesHaveDifferentServiceIdAndSameDate() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        final CalendarDate firstCalendarDate = (CalendarDate) entityBuildResult.getData();

        entityBuildResult = underTest.serviceId("different service id")
                .date(LocalDate.now())
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();

        assertFalse(firstCalendarDate.equals(secondCalendarDate));
        assertFalse(secondCalendarDate.equals(firstCalendarDate));
    }

    // suppressed warning, will keep the current implementation for legibility
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void equalsShouldReturnFalseWhenCalendarDatesHaveSameServiceIdAndDifferentDate() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        final CalendarDate firstCalendarDate = (CalendarDate) entityBuildResult.getData();

        entityBuildResult = underTest.serviceId("service id 0")
                .date(LocalDate.now().plusDays(5))
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();

        assertFalse(firstCalendarDate.equals(secondCalendarDate));
        assertFalse(secondCalendarDate.equals(firstCalendarDate));
    }

    // suppressed warning, will keep the current implementation for legibility
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void equalsShouldReturnFalseWhenCalendarDatesHaveDifferentServiceIdAndDate() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        final CalendarDate firstCalendarDate = (CalendarDate) entityBuildResult.getData();

        entityBuildResult = underTest.serviceId("different service id 0")
                .date(LocalDate.now().plusDays(5))
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();

        assertFalse(firstCalendarDate.equals(secondCalendarDate));
        assertFalse(secondCalendarDate.equals(firstCalendarDate));
    }

    // suppressed warning, will keep the current implementation for legibility
    @SuppressWarnings("SimplifiableAssertion")
    @Test
    void equalsShouldReturnTrueWhenCalendarDateHaeSameServiceIdAndDate() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        final CalendarDate firstCalendarDate = (CalendarDate) entityBuildResult.getData();

        entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();

        assertTrue(firstCalendarDate.equals(secondCalendarDate));
        assertTrue(secondCalendarDate.equals(firstCalendarDate));
    }

    // suppressed warning, will keep the current implementation for legibility
    @SuppressWarnings({"EqualsWithItself", "SimplifiableAssertion"})
    @Test
    void equalsShouldReturnTrueWhenComparingCalendarDateToItself() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId("service_id 0")
                .date(LocalDate.now())
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        assertTrue(calendarDate.equals(calendarDate));
    }

    @Test
    void calendarDateWithDifferentServiceIdAndDateShouldHaveDifferentHashCodes() {
        final String serviceIdValue = "service id";
        final String differentServiceIdValue = "different service id";

        final LocalDate date = LocalDate.now();
        final LocalDate differentDate = LocalDate.now().plusDays(3);

        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        final int firstCalendarDateHashCode = calendarDate.hashCode();

        entityBuildResult = underTest.serviceId(differentServiceIdValue)
                .date(differentDate)
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();
        final int secondCalendarDateHashCode = secondCalendarDate.hashCode();

        assertNotEquals(firstCalendarDateHashCode, secondCalendarDateHashCode);
    }

    @Test
    void calendarDatesWithDifferentServiceIdAndSameDateShouldHaveDifferentHashCodes() {
        final String serviceIdValue = "service id";
        final String differentServiceIdValue = "different service id";

        final LocalDate date = LocalDate.now();

        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        final int firstCalendarDateHashCode = calendarDate.hashCode();

        entityBuildResult = underTest.serviceId(differentServiceIdValue)
                .date(date)
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();
        final int secondCalendarDateHashCode = secondCalendarDate.hashCode();

        assertNotEquals(firstCalendarDateHashCode, secondCalendarDateHashCode);
    }

    @Test
    void calendarDatesWithSameServiceIdAndDifferentDatesShouldHaveDifferentHashCodes() {
        final String serviceIdValue = "service id";

        final LocalDate date = LocalDate.now();
        final LocalDate differentDate = LocalDate.now().plusDays(4);

        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        final int firstCalendarDateHashCode = calendarDate.hashCode();

        entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(differentDate)
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();
        final int secondCalendarDateHashCode = secondCalendarDate.hashCode();

        assertNotEquals(firstCalendarDateHashCode, secondCalendarDateHashCode);
    }

    @Test
    void calendarDatesWithSameServiceIdAndDateShouldHaveSameHashCodes() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.now();

        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        final int firstCalendarDateHashCode = calendarDate.hashCode();

        entityBuildResult = underTest.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(2)
                .build();
        final CalendarDate secondCalendarDate = (CalendarDate) entityBuildResult.getData();
        final int secondCalendarDateHashCode = secondCalendarDate.hashCode();

        assertEquals(firstCalendarDateHashCode, secondCalendarDateHashCode);
    }

    @Test
    void getDayOfWeekAsStringShouldReturnDayOfWeek() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2020, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate calendarDate = (CalendarDate) entityBuildResult.getData();

        assertEquals("friday", calendarDate.getDayOfWeekAsString());
    }

    @Test
    void isOverlappingShouldReturnFalseWhenServiceIsRemovedOnDate() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2020, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(2)
                .build();
        final CalendarDate underTest = (CalendarDate) entityBuildResult.getData();
        final Calendar mockCalendar = mock(Calendar.class);
        assertFalse(underTest.isOverlapping(mockCalendar));
    }

    @Test
    void isOverlappingShouldReturnFalseWhenServiceDayIsBeforeCalendarDateRange() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2020, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate underTest = (CalendarDate) entityBuildResult.getData();
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(LocalDate.of(2020, 12, 1));
        when(mockCalendar.getEndDate()).thenReturn(LocalDate.of(2020, 12, 31));
        assertFalse(underTest.isOverlapping(mockCalendar));
    }

    @Test
    void isOverlappingShouldReturnFalseWhenServiceDayIsAfterCalendarDateRange() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2021, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate underTest = (CalendarDate) entityBuildResult.getData();
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(LocalDate.of(2020, 12, 1));
        when(mockCalendar.getEndDate()).thenReturn(LocalDate.of(2020, 12, 31));
        assertFalse(underTest.isOverlapping(mockCalendar));
    }

    @Test
    void isOverlappingShouldReturnTrueWhenCalendarOverlaps() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2020, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate underTest = (CalendarDate) entityBuildResult.getData();
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(LocalDate.of(2020, 10, 1));
        when(mockCalendar.getEndDate()).thenReturn(LocalDate.of(2020, 10, 31));
        when(mockCalendar.getDayOfServiceAvailabilityAsStringCollection()).thenReturn(Set.of("monday", "friday"));
        assertTrue(underTest.isOverlapping(mockCalendar));
    }

    @Test
    void isOverlappingShouldReturnFalseWhenCalendarDoesNotOverlap() {
        final String serviceIdValue = "service id";
        final LocalDate date = LocalDate.of(2020, 10, 16);

        final CalendarDate.CalendarDateBuilder builder = new CalendarDate.CalendarDateBuilder();
        final EntityBuildResult<?> entityBuildResult = builder.serviceId(serviceIdValue)
                .date(date)
                .exceptionType(1)
                .build();
        final CalendarDate underTest = (CalendarDate) entityBuildResult.getData();
        final Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.getStartDate()).thenReturn(LocalDate.of(2020, 10, 1));
        when(mockCalendar.getEndDate()).thenReturn(LocalDate.of(2020, 10, 31));
        when(mockCalendar.getDayOfServiceAvailabilityAsStringCollection()).thenReturn(Set.of("monday", "tuesday"));
        assertFalse(underTest.isOverlapping(mockCalendar));
    }
}
