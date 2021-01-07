package org.mobilitydata.gtfsvalidator.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimaps;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

/**
 * Provides a collection of functions to work with service dates in <i>calendar.txt</i> and <i>calendar_dates.txt</i>.
 */
public final class CalendarUtil {
    private CalendarUtil() {
    }

    /**
     * Extracts the service period information of a service from GTFS schema.
     *
     * @param calendar      the row in <i>calendar.txt</i> or null
     * @param calendarDates the list of rows in <i>calendar_dates.txt</i>, may
     *                      be empty
     * @return a {@link ServicePeriod} object
     */
    public static ServicePeriod
    createServicePeriod(@Nullable GtfsCalendar calendar,
                        @Nonnull List<GtfsCalendarDate> calendarDates) {
        // Store service period from calendar.txt, if provided.
        LocalDate serviceStart;
        LocalDate serviceEnd;
        byte weeklyPattern;
        if (calendar != null) {
            serviceStart = calendar.startDate().getLocalDate();
            serviceEnd = calendar.endDate().getLocalDate();
            weeklyPattern = ServicePeriod.weeklyPatternFromMTWTFSS(
                    calendar.mondayValue(), calendar.tuesdayValue(),
                    calendar.wednesdayValue(), calendar.thursdayValue(),
                    calendar.fridayValue(), calendar.saturdayValue(),
                    calendar.sundayValue());
        } else {
            serviceStart = LocalDate.EPOCH;
            serviceEnd = LocalDate.EPOCH;
            weeklyPattern = 0;
        }

        // Store exception days from calendar_dates.txt, if any.
        Set<LocalDate> addedDays = new HashSet<>();
        Set<LocalDate> removedDays = new HashSet<>();
        for (GtfsCalendarDate calendarDate : calendarDates) {
            (calendarDate.exceptionType() ==
                    GtfsCalendarDateExceptionType.SERVICE_ADDED
                    ? addedDays
                    : removedDays)
                    .add(calendarDate.date().getLocalDate());
        }

        return new ServicePeriod(serviceStart, serviceEnd, weeklyPattern,
                addedDays, removedDays);
    }

    /**
     * Builds a service id to {@code ServicePeriod} mapping using the given
     * <i>calendar.txt</i> and <i>calendar_dates.txt</i> tables. <p> If either
     * table is missing, a empty container should be passed in.
     *
     * @param calendarTable     the <i>calendar.txt</i> table container, may be
     *                          empty
     * @param calendarDateTable the <i>calendar_dates.txt</i> table container,
     *                          may be empty
     * @return mapping from service id to {@link ServicePeriod} object
     */
    public static Map<String, ServicePeriod> buildServicePeriodMap(
            @Nonnull GtfsCalendarTableContainer calendarTable,
            @Nonnull GtfsCalendarDateTableContainer calendarDateTable) {
        Preconditions.checkNotNull(calendarTable);
        Preconditions.checkNotNull(calendarDateTable);

        Map<String, ServicePeriod> servicePeriods = new HashMap<>();
        for (GtfsCalendar calendar : calendarTable.getEntities()) {
            servicePeriods.put(
                    calendar.serviceId(),
                    createServicePeriod(calendar, calendarDateTable.byServiceId(
                            calendar.serviceId())));
        }
        for (List<GtfsCalendarDate> calendarDates :
                Multimaps.asMap(calendarDateTable.byServiceIdMap()).values()) {
            if (!servicePeriods.containsKey(calendarDates.get(0).serviceId())) {
                servicePeriods.put(calendarDates.get(0).serviceId(),
                        createServicePeriod(null, calendarDates));
            }
        }
        return servicePeriods;
    }

    /**
     * Converts a map {service id -> ServicePeriod} to map {service id -> set of dates}.
     *
     * @param servicePeriods mapping from service id to {@link ServicePeriod}
     * @return mapping from service id to a set of included days
     */
    public static Map<String, SortedSet<LocalDate>> servicePeriodToServiceDatesMap(
            @Nonnull Map<String, ServicePeriod> servicePeriods) {
        Map<String, SortedSet<LocalDate>> serviceDates = new HashMap<>();
        for (Map.Entry<String, ServicePeriod> kv : servicePeriods.entrySet()) {
            serviceDates.put(kv.getKey(), kv.getValue().toDates());
        }
        return serviceDates;
    }

    /**
     * Finds the first intersecting date in the given sets, if any. Returns {@code Optional.empty()} if there is no
     * intersection.
     * <p>
     * If either set is empty, then the method always returns false, even if both sets are empty.
     *
     * @param dates1 the first sorted set of service dates
     * @param dates2 the second sorted set of service dates
     * @return the first intersecting date or {@code Optional.empty()}
     */
    public static Optional<LocalDate> firstIntersectingDate(@Nonnull SortedSet<LocalDate> dates1,
                                                            @Nonnull SortedSet<LocalDate> dates2) {
        if (dates1.isEmpty() || dates2.isEmpty()) {
            return Optional.empty();
        }
        Iterator<LocalDate> it1 = dates1.iterator();
        Iterator<LocalDate> it2 = dates2.iterator();
        LocalDate date1 = it1.next();
        LocalDate date2 = it2.next();
        for (; ; ) {
            final int compare = date1.compareTo(date2);
            if (compare == 0) {
                return Optional.of(date1);
            } else if (compare < 0) {
                if (!it1.hasNext()) {
                    return Optional.empty();
                }
                date1 = it1.next();
            } else {
                if (!it2.hasNext()) {
                    return Optional.empty();
                }
                date2 = it2.next();
            }
        }
    }
}
