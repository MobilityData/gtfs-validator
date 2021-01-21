package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.notice.BlockTripsWithOverlappingStopTimesNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class BlockTripsWithOverlappingStopTimesValidatorTest {

  private static GtfsCalendarTableContainer createCalendarTable(NoticeContainer noticeContainer) {
    final Set<DayOfWeek> weekDays =
            ImmutableSet.of(
                    DayOfWeek.MONDAY,
                    DayOfWeek.TUESDAY,
                    DayOfWeek.WEDNESDAY,
                    DayOfWeek.THURSDAY,
                    DayOfWeek.FRIDAY);
    return GtfsCalendarTableContainer.forEntities(
            ImmutableList.of(
                    CalendarUtilTest.createGtfsCalendar(
                            "WEEK", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 4, 4), weekDays),
                    CalendarUtilTest.createGtfsCalendar(
                            "WEEK-ALT", LocalDate.of(2021, 1, 4), LocalDate.of(2021, 4, 4), weekDays),
                    CalendarUtilTest.createGtfsCalendar(
                            "SAT",
                            LocalDate.of(2021, 1, 4),
                            LocalDate.of(2021, 4, 4),
                            ImmutableSet.of(DayOfWeek.SATURDAY)),
                    CalendarUtilTest.createGtfsCalendar(
                            "SUN",
                            LocalDate.of(2021, 1, 4),
                            LocalDate.of(2021, 4, 4),
                            ImmutableSet.of(DayOfWeek.SUNDAY))),
            noticeContainer);
  }

  public static GtfsStopTime createStopTime(
          long csvRowNumber, String tripId, String time, String stopId, int stopSequence) {
    return new GtfsStopTime.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setTripId(tripId)
            .setArrivalTime(GtfsTime.fromString(time))
            .setDepartureTime(GtfsTime.fromString(time))
            .setStopSequence(stopSequence)
            .setStopId(stopId)
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

  private static GtfsTripTableContainer createTripTable(
          String[] tripIds, String[] serviceIds, String blockId, NoticeContainer noticeContainer) {
    Preconditions.checkArgument(
            tripIds.length == serviceIds.length, "tripIds.length must be equal to serviceIds.length");

    ArrayList<GtfsTrip> trips = new ArrayList<>();
    trips.ensureCapacity(tripIds.length);
    for (int i = 0; i < tripIds.length; ++i) {
      trips.add(createTrip(trips.size() + 1, tripIds[i], serviceIds[i], blockId));
    }

    return GtfsTripTableContainer.forEntities(trips, noticeContainer);
  }

  private static GtfsStopTimeTableContainer createStopTimeTable(
          String[] tripIds, String[] stopIds, String[][] times, NoticeContainer noticeContainer) {
    Preconditions.checkArgument(
            tripIds.length == times.length, "tripIds.length must be equal to times.length");

    ArrayList<GtfsStopTime> stopTimes = new ArrayList<>();
    stopTimes.ensureCapacity(tripIds.length * stopIds.length);
    for (int i = 0; i < tripIds.length; ++i) {
      Preconditions.checkArgument(
              stopIds.length == times[i].length, "stopIds.length must be equal to times[%d].length", i);
      for (int j = 0; j < stopIds.length; ++j) {
        stopTimes.add(createStopTime(stopTimes.size() + 1, tripIds[i], times[i][j], stopIds[j], j));
      }
    }

    return GtfsStopTimeTableContainer.forEntities(stopTimes, noticeContainer);
  }

  @Test
  public void goodFeed() {
    final NoticeContainer noticeContainer = new NoticeContainer();

    BlockTripsWithOverlappingStopTimesValidator validator =
            new BlockTripsWithOverlappingStopTimesValidator();
    validator.calendarTable = createCalendarTable(noticeContainer);
    validator.calendarDateTable = GtfsCalendarDateTableContainer.forMissingFile();
    validator.tripTable =
            createTripTable(
                    new String[]{"t0", "t1", "t2", "t3", "t4", "t5"},
                    new String[]{"WEEK", "WEEK", "WEEK", "SAT", "SAT", "SAT"},
                    "b1",
                    noticeContainer);
    validator.stopTimeTable =
            createStopTimeTable(
                    new String[]{"t0", "t1", "t2", "t3", "t4", "t5"},
                    new String[]{"s0", "s1"},
                    new String[][]{
                            new String[]{"08:00:00", "09:00:00"},
                            new String[]{"10:00:00", "11:00:00"},
                            new String[]{"12:00:00", "13:00:00"},
                            new String[]{"08:00:00", "09:00:00"},
                            new String[]{"10:00:00", "11:00:00"},
                            new String[]{"12:00:00", "13:00:00"},
                    },
                    noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void overlapWithSameServiceId() {
    final NoticeContainer noticeContainer = new NoticeContainer();

    BlockTripsWithOverlappingStopTimesValidator validator =
            new BlockTripsWithOverlappingStopTimesValidator();
    validator.calendarTable = createCalendarTable(noticeContainer);
    validator.calendarDateTable = GtfsCalendarDateTableContainer.forMissingFile();
    validator.tripTable =
            createTripTable(
                    new String[]{"t0", "t1", "t2"},
                    new String[]{"WEEK", "WEEK", "WEEK"},
                    "b1",
                    noticeContainer);
    validator.stopTimeTable =
            createStopTimeTable(
                    new String[]{"t0", "t1", "t2"},
                    new String[]{"s0", "s1"},
                    new String[][]{
                            new String[]{"08:00:00", "09:00:00"},
                            new String[]{"08:30:00", "09:30:00"},
                            new String[]{"12:00:00", "13:00:00"},
                    },
                    noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
            .containsExactly(
                    new BlockTripsWithOverlappingStopTimesNotice(
                            1, "t0", "WEEK", 2, "t1", "WEEK", "b1", GtfsDate.fromString("20210104")));
  }

  @Test
  public void overlapWithDifferentServiceId() {
    final NoticeContainer noticeContainer = new NoticeContainer();

    BlockTripsWithOverlappingStopTimesValidator validator =
            new BlockTripsWithOverlappingStopTimesValidator();
    validator.calendarTable = createCalendarTable(noticeContainer);
    validator.calendarDateTable = GtfsCalendarDateTableContainer.forMissingFile();
    validator.tripTable =
            createTripTable(
                    new String[]{"t0", "t1"}, new String[]{"WEEK", "WEEK-ALT"}, "b1", noticeContainer);
    validator.stopTimeTable =
            createStopTimeTable(
                    new String[]{"t0", "t1"},
                    new String[]{"s0", "s1"},
                    new String[][]{
                            new String[]{"08:00:00", "09:00:00"},
                            new String[]{"08:30:00", "09:30:00"},
                    },
                    noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices())
            .containsExactly(
                    new BlockTripsWithOverlappingStopTimesNotice(
                            1, "t0", "WEEK", 2, "t1", "WEEK-ALT", "b1", GtfsDate.fromString("20210104")));
  }

  @Test
  public void tripsWith0or1Stop() {
    // Trips with 0 or 1 stop are not useful to end users and should be
    // gracefully handled by this validator.
    final NoticeContainer noticeContainer = new NoticeContainer();

    BlockTripsWithOverlappingStopTimesValidator validator =
            new BlockTripsWithOverlappingStopTimesValidator();
    validator.calendarTable = createCalendarTable(noticeContainer);
    validator.calendarDateTable = GtfsCalendarDateTableContainer.forMissingFile();
    validator.tripTable =
            createTripTable(
                    new String[]{"t0", "t1"}, new String[]{"WEEK", "WEEK"}, "b1", noticeContainer);
    // t0 has 1 stop (s0), t1 has no stops at all.
    validator.stopTimeTable =
            createStopTimeTable(
                    new String[]{"t0"},
                    new String[]{"s0"},
                    new String[][]{new String[]{"08:00:00"}},
                    noticeContainer);

    validator.validate(noticeContainer);
    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }
}
