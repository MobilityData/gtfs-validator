package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class DateTripsValidatorTest {
    static final Set<DayOfWeek> weekDays =
            ImmutableSet.of(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY);
    private static final ZonedDateTime TEST_NOW =
            ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneOffset.UTC);

    private GtfsCalendar createCalendar(LocalDate calendarEndDate) {
        return CalendarUtilTest.createGtfsCalendar(
                "WEEK", LocalDate.of(2021, 1, 14), calendarEndDate, weekDays);
    }

    @Test
    public void serviceWindowEndingBefore7DaysFromNow() {

        var serviceWindowStart = TEST_NOW.toLocalDate();
        var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(6);
        NoticeContainer noticeContainer = new NoticeContainer();

        String[] serviceIds = {"s1"};
        LocalDate[] serviceStartDates = {serviceWindowStart};
        LocalDate[] serviceEndDates = {serviceWindowEnd};
        DayOfWeek[][] serviceDays = {DayOfWeek.values()};
        int tripsPerDay = 6;

        DateTripsValidator validator = createBasicValidator(
                serviceWindowStart, serviceWindowEnd,
                noticeContainer, serviceIds,
                serviceStartDates, serviceEndDates,
                serviceDays, tripsPerDay);

        validator.validate(noticeContainer);

        var notices = noticeContainer.getValidationNotices();
        assertThat(notices).containsExactly(
                new DateTripsValidator.DateTripsValidatorValidForNextSevenDaysNotice(
                        GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                       GtfsDate.fromLocalDate(serviceWindowStart),
                        GtfsDate.fromLocalDate(serviceWindowEnd)));
    }

    @Test
    public void serviceWindowStartingAfterNow() {

        var serviceWindowStart = TEST_NOW.toLocalDate().plusDays(1);
        var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(7);
        NoticeContainer noticeContainer = new NoticeContainer();

        String[] serviceIds = {"s1"};
        LocalDate[] serviceStartDates = {serviceWindowStart};
        LocalDate[] serviceEndDates = {serviceWindowEnd};
        DayOfWeek[][] serviceDays = {DayOfWeek.values()};
        int tripsPerDay = 6;

        DateTripsValidator validator = createBasicValidator(
                serviceWindowStart, serviceWindowEnd,
                noticeContainer, serviceIds,
                serviceStartDates, serviceEndDates,
                serviceDays, tripsPerDay);

        validator.validate(noticeContainer);

        var notices = noticeContainer.getValidationNotices();
        assertThat(notices).containsExactly(
                new DateTripsValidator.DateTripsValidatorValidForNextSevenDaysNotice(
                        GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                        GtfsDate.fromLocalDate(serviceWindowStart),
                        GtfsDate.fromLocalDate(serviceWindowEnd)));
    }

    @Test
    public void serviceWindowStartingNowAndEndingIn7Days() {

        var serviceWindowStart = TEST_NOW.toLocalDate();
        var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(7);
        NoticeContainer noticeContainer = new NoticeContainer();

        String[] serviceIds = {"s1"};
        LocalDate[] serviceStartDates = {serviceWindowStart};
        LocalDate[] serviceEndDates = {serviceWindowEnd};
        DayOfWeek[][] serviceDays = {DayOfWeek.values()};
        int tripsPerDay = 6;

        DateTripsValidator validator = createBasicValidator(
                serviceWindowStart, serviceWindowEnd,
                noticeContainer, serviceIds,
                serviceStartDates, serviceEndDates,
                serviceDays, tripsPerDay);

        validator.validate(noticeContainer);

        var notices = noticeContainer.getValidationNotices();
        assertThat(notices).isEmpty();
    }

    @Test
    public void serviceWindowStartingBeforeNowAndEndingAfter7Days() {

        var serviceWindowStart = TEST_NOW.toLocalDate().minusDays(1);
        var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(8);
        NoticeContainer noticeContainer = new NoticeContainer();

        String[] serviceIds = {"s1"};
        LocalDate[] serviceStartDates = {serviceWindowStart};
        LocalDate[] serviceEndDates = {serviceWindowEnd};
        DayOfWeek[][] serviceDays = {DayOfWeek.values()};
        int tripsPerDay = 6;

        DateTripsValidator validator = createBasicValidator(
                serviceWindowStart, serviceWindowEnd,
                noticeContainer, serviceIds,
                serviceStartDates, serviceEndDates,
                serviceDays, tripsPerDay);

        validator.validate(noticeContainer);

        var notices = noticeContainer.getValidationNotices();
        assertThat(notices).isEmpty();
    }


    private static DateTripsValidator createBasicValidator(LocalDate serviceWindowStart, LocalDate serviceWindowEnd, NoticeContainer noticeContainer, String[] serviceIds, LocalDate[] serviceStartDates, LocalDate[] serviceEndDates, DayOfWeek[][] serviceDays, int tripsPerDay) {
        GtfsCalendarTableContainer calendarTable =
                createCalendarTable(serviceIds, serviceStartDates,
                        serviceEndDates, serviceDays, noticeContainer);

        GtfsCalendarDateTableContainer dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
        var feedInfoTable = GtfsFeedInfoTableContainer.forEntities(ImmutableList.of(
                createFeedInfo(GtfsDate.fromLocalDate(serviceWindowStart), GtfsDate.fromLocalDate(serviceWindowEnd))),
                noticeContainer);

        String[] tripIds = new String[tripsPerDay];
        String[] tripsServiceIds = new String[tripsPerDay];
        for(int i = 0; i < tripsPerDay; i++){
            tripIds[i] = "t" + i;
            tripsServiceIds[i] = serviceIds[0];
        }

        var tripContainer = GtfsTripTableContainer.forEntities(
                createTripTable( tripIds, tripsServiceIds, "b1"),
                noticeContainer);

        DateTripsValidator validator = new DateTripsValidator(new CurrentDateTime(TEST_NOW), dateTable,
                calendarTable, feedInfoTable, tripContainer);
        return validator;
    }

    private static GtfsCalendarTableContainer createCalendarTable(String[] serviceIds,  LocalDate[] startDates,
                                                                  LocalDate[] endDates, DayOfWeek[][] days,
                                                                  NoticeContainer noticeContainer) {
        Preconditions.checkArgument(
                serviceIds.length == startDates.length &&
                serviceIds.length == endDates.length, "serviceIds.length, startDates.length, and endDates.length must be equal");
        ArrayList<GtfsCalendar> calendars = new ArrayList<>();

        for(int i = 0; i < serviceIds.length; i++){
           calendars.add(createGtfsCalendar(serviceIds[i], startDates[i], endDates[i], ImmutableSet.copyOf(days[i])));
        }
        return GtfsCalendarTableContainer.forEntities(calendars, noticeContainer);
    }

    public static GtfsFeedInfo createFeedInfo(GtfsDate feedStartDate, GtfsDate feedEndDate){

        return new GtfsFeedInfo.Builder()
                .setCsvRowNumber(1)
                .setFeedPublisherName("feed publisher name value")
                .setFeedPublisherUrl("https://www.mobilitydata.org")
                .setFeedLang(Locale.CANADA)
                .setFeedStartDate(feedStartDate)
                .setFeedEndDate(feedEndDate)
                .build();
    }
    public static GtfsCalendar createGtfsCalendar(
            String serviceId, LocalDate startDate, LocalDate endDate, Set<DayOfWeek> days) {
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

    private static GtfsCalendarDate addedCalendarDate(String serviceId, LocalDate date) {
        return new GtfsCalendarDate.Builder()
                .setServiceId(serviceId)
                .setDate(GtfsDate.fromLocalDate(date))
                .setExceptionType(GtfsCalendarDateExceptionType.SERVICE_ADDED)
                .build();
    }

    private static GtfsCalendarDate removedCalendarDate(String serviceId, LocalDate date) {
        return new GtfsCalendarDate.Builder()
                .setServiceId(serviceId)
                .setDate(GtfsDate.fromLocalDate(date))
                .setExceptionType(GtfsCalendarDateExceptionType.SERVICE_REMOVED)
                .build();
    }


    public static GtfsTrip createTrip(
            long csvRowNumber, String tripId, String serviceId, String blockId) {
        return new GtfsTrip.Builder()
                .setCsvRowNumber(csvRowNumber)
                .setTripId(tripId)
                .setServiceId(serviceId)
                .setBlockId(blockId)
                .build();
    }

    private static List<GtfsTrip> createTripTable(
            String[] tripIds, String[] serviceIds, String blockId) {
        Preconditions.checkArgument(
                tripIds.length == serviceIds.length, "tripIds.length must be equal to serviceIds.length");

        ArrayList<GtfsTrip> trips = new ArrayList<>();
        trips.ensureCapacity(tripIds.length);
        for (int i = 0; i < tripIds.length; ++i) {
            trips.add(createTrip(trips.size() + 1, tripIds[i], serviceIds[i], blockId));
        }

        return trips;
    }
}
