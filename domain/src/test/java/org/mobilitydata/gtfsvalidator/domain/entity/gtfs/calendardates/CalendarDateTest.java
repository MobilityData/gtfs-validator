package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.calendardates;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CalendarDateTest {

    @Test
    void createCalendarDateWithValidValueShouldNotThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        underTest.serviceId("service_id")
                .date(LocalDateTime.now())
                .exceptionType(1);
    }

    @Test
    void createCalendarDateWithNullServiceIdShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId(null)
                .date(LocalDateTime.now())
                .exceptionType(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field service_id in calendar_dates.txt can not be null", exception.getMessage());
    }

    @Test
    void createCalendarDateWithNullDateShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId("test")
                .date(null)
                .exceptionType(1);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field date in calendar_dates.txt can not be null", exception.getMessage());
    }

    @Test
    void createCalendarDateWithNullExceptionTypeShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        //noinspection ConstantConditions
        underTest.serviceId("test")
                .date(LocalDateTime.now())
                .exceptionType(null);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());
    }

    @Test
    void createCalendarDateWithInvalidExceptionTypeShouldThrowException() {
        final CalendarDate.CalendarDateBuilder underTest = new CalendarDate.CalendarDateBuilder();
        underTest.serviceId("test")
                .date(LocalDateTime.now())
                .exceptionType(5);

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("unexpected value found for field exception_type of calendar_dates.txt",
                exception.getMessage());
    }
}