package org.mobilitydata.gtfsvalidator.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class CalendarUtilTest {
    public static GtfsCalendar createGtfsCalendar(String serviceId,
                                                  LocalDate startDate,
                                                  LocalDate endDate,
                                                  Set<DayOfWeek> days) {
        GtfsCalendar.Builder calendar =
                new GtfsCalendar.Builder()
                        .setServiceId(serviceId)
                        .setStartDate(GtfsDate.fromLocalDate(startDate))
                        .setEndDate(GtfsDate.fromLocalDate(endDate));
        if (days.contains(DayOfWeek.MONDAY)) {
            calendar.setMonday(1);
        }
        if (days.contains(DayOfWeek.TUESDAY)) {
            calendar.setTuesday(1);
        }
        if (days.contains(DayOfWeek.WEDNESDAY)) {
            calendar.setWednesday(1);
        }
        if (days.contains(DayOfWeek.THURSDAY)) {
            calendar.setThursday(1);
        }
        if (days.contains(DayOfWeek.FRIDAY)) {
            calendar.setFriday(1);
        }
        if (days.contains(DayOfWeek.SATURDAY)) {
            calendar.setSaturday(1);
        }
        if (days.contains(DayOfWeek.SUNDAY)) {
            calendar.setSunday(1);
        }
        return calendar.build();
    }

    private static GtfsCalendarDate addedCalendarDate(String serviceId,
                                                      LocalDate date) {
        return new GtfsCalendarDate.Builder()
                .setServiceId(serviceId)
                .setDate(GtfsDate.fromLocalDate(date))
                .setExceptionType(
                        GtfsCalendarDateExceptionType.SERVICE_ADDED.getNumber())
                .build();
    }

    private static GtfsCalendarDate removedCalendarDate(String serviceId,
                                                        LocalDate date) {
        return new GtfsCalendarDate.Builder()
                .setServiceId(serviceId)
                .setDate(GtfsDate.fromLocalDate(date))
                .setExceptionType(
                        GtfsCalendarDateExceptionType.SERVICE_REMOVED.getNumber())
                .build();
    }

    @Test
    public void createServicePeriod() {
        ServicePeriod servicePeriod = CalendarUtil.createServicePeriod(
                createGtfsCalendar(
                        "s1", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 5),
                        ImmutableSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                                DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
                ImmutableList.of(addedCalendarDate("s1", LocalDate.of(2021, 1, 6)),
                        removedCalendarDate("s1", LocalDate.of(2021, 1, 7))));
        assertThat(servicePeriod.getServiceStart())
                .isEqualTo(LocalDate.of(2021, 1, 4));
        assertThat(servicePeriod.getServiceEnd())
                .isEqualTo(LocalDate.of(2021, 1, 5));
        assertThat(servicePeriod.getWeeklyPattern()).isEqualTo(0b0010111);
        assertThat(servicePeriod.getAddedDays())
                .containsExactly(LocalDate.of(2021, 1, 6));
        assertThat(servicePeriod.getRemovedDays())
                .containsExactly(LocalDate.of(2021, 1, 7));
    }

    @Test
    public void CreateServicePeriodEmpty() {
        ServicePeriod servicePeriod =
                CalendarUtil.createServicePeriod(null, ImmutableList.of());
        assertThat(servicePeriod.getServiceStart()).isEqualTo(LocalDate.EPOCH);
        assertThat(servicePeriod.getServiceEnd()).isEqualTo(LocalDate.EPOCH);
        assertThat(servicePeriod.getWeeklyPattern()).isEqualTo(0);
        assertThat(servicePeriod.getAddedDays()).isEmpty();
        assertThat(servicePeriod.getRemovedDays()).isEmpty();
    }

    @Test
    public void buildServicePeriodMap() {
        NoticeContainer noticeContainer = new NoticeContainer();
        GtfsCalendarTableContainer calendarTable =
                GtfsCalendarTableContainer.forEntities(
                        ImmutableList.of(createGtfsCalendar(
                                "s1", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 1, 5),
                                ImmutableSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
                                        DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
                                createGtfsCalendar(
                                        "s2", LocalDate.of(2021, 2, 14), LocalDate.of(2021, 2, 15),
                                        ImmutableSet.of(DayOfWeek.SUNDAY))),
                        noticeContainer);

        GtfsCalendarDateTableContainer calendarDateTable =
                GtfsCalendarDateTableContainer.forEntities(
                        ImmutableList.of(
                                addedCalendarDate("s1", LocalDate.of(2021, 1, 6)),
                                removedCalendarDate("s1", LocalDate.of(2021, 1, 7)),
                                addedCalendarDate("s3", LocalDate.of(2021, 3, 8)),
                                removedCalendarDate("s3", LocalDate.of(2021, 3, 9))),
                        noticeContainer);

        assertThat(
                CalendarUtil.buildServicePeriodMap(calendarTable, calendarDateTable))
                .containsExactly(
                        "s1",
                        new ServicePeriod(LocalDate.of(2021, 1, 4),
                                LocalDate.of(2021, 1, 5), (byte) 0b0010111,
                                ImmutableSet.of(LocalDate.of(2021, 1, 6)),
                                ImmutableSet.of(LocalDate.of(2021, 1, 7))),
                        "s2",
                        new ServicePeriod(LocalDate.of(2021, 2, 14),
                                LocalDate.of(2021, 2, 15), (byte) 0b1000000,
                                ImmutableSet.of(), ImmutableSet.of()),
                        "s3",
                        new ServicePeriod(LocalDate.EPOCH, LocalDate.EPOCH, (byte) 0,
                                ImmutableSet.of(LocalDate.of(2021, 3, 8)),
                                ImmutableSet.of(LocalDate.of(2021, 3, 9))));
    }

    @Test
    public void servicePeriodToServiceDatesMap() {
        assertThat(
                CalendarUtil.servicePeriodToServiceDatesMap(ImmutableMap.of(
                        "s1",
                        new ServicePeriod(LocalDate.of(2021, 1, 4),
                                LocalDate.of(2021, 1, 5), (byte) 0b0010111,
                                ImmutableSet.of(LocalDate.of(2021, 1, 6)),
                                ImmutableSet.of(LocalDate.of(2021, 1, 7))),
                        "s2",
                        new ServicePeriod(LocalDate.of(2021, 2, 14),
                                LocalDate.of(2021, 2, 15), (byte) 0b1000000,
                                ImmutableSet.of(), ImmutableSet.of()),
                        "s3",
                        new ServicePeriod(LocalDate.EPOCH, LocalDate.EPOCH, (byte) 0,
                                ImmutableSet.of(LocalDate.of(2021, 3, 8)),
                                ImmutableSet.of(LocalDate.of(2021, 3, 9))))))
                .containsExactly("s1",
                        ImmutableSortedSet.of(LocalDate.of(2021, 1, 4),
                                LocalDate.of(2021, 1, 5),
                                LocalDate.of(2021, 1, 6)),
                        "s2", ImmutableSortedSet.of(LocalDate.of(2021, 2, 14)),
                        "s3", ImmutableSortedSet.of(LocalDate.of(2021, 3, 8)));
    }

    @Test
    public void firstIntersectingDate() {
        assertThat(CalendarUtil.firstIntersectingDate(
                ImmutableSortedSet.of(LocalDate.of(2021, 1, 4),
                        LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 6)),
                ImmutableSortedSet.of(
                        LocalDate.of(2021, 1, 1), LocalDate.of(2021, 1, 5),
                        LocalDate.of(2021, 1, 6), LocalDate.of(2021, 1, 7))))
                .isEqualTo(Optional.of(LocalDate.of(2021, 1, 5)));

        assertThat(CalendarUtil.firstIntersectingDate(
                ImmutableSortedSet.of(LocalDate.of(2021, 1, 4),
                        LocalDate.of(2021, 1, 5)),
                ImmutableSortedSet.of(LocalDate.of(2021, 1, 6),
                        LocalDate.of(2021, 1, 7))))
                .isEqualTo(Optional.empty());

        assertThat(CalendarUtil.firstIntersectingDate(
                ImmutableSortedSet.of(LocalDate.of(2021, 1, 4)),
                ImmutableSortedSet.of()))
                .isEqualTo(Optional.empty());

        assertThat(CalendarUtil.firstIntersectingDate(ImmutableSortedSet.of(),
                ImmutableSortedSet.of()))
                .isEqualTo(Optional.empty());
    }
}
