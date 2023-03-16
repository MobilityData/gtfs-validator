package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.validator.DateTripsValidator.*;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.CalendarUtilTest;

@RunWith(JUnit4.class)
public class DateTripsValidatorTest {
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2022, 12, 1, 8, 30, 0, 0, ZoneOffset.UTC);

  @Test
  public void serviceWindowEndingBefore7DaysFromNowShouldGenerateNotice() {

    var serviceWindowStart = TEST_NOW.toLocalDate();
    var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(6);

    var notices = validateSimpleServiceWindow(serviceWindowStart, serviceWindowEnd);
    assertThat(notices)
        .containsExactly(
            new TripDataShouldBeValidForNext7DaysNotice(
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(serviceWindowStart),
                GtfsDate.fromLocalDate(serviceWindowEnd)));
  }

  @Test
  public void serviceWindowStartingAfterNowShouldGenerateNotice() {

    var serviceWindowStart = TEST_NOW.toLocalDate().plusDays(1);
    var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(7);
    var notices = validateSimpleServiceWindow(serviceWindowStart, serviceWindowEnd);
    assertThat(notices)
        .containsExactly(
            new TripDataShouldBeValidForNext7DaysNotice(
                GtfsDate.fromLocalDate(TEST_NOW.toLocalDate()),
                GtfsDate.fromLocalDate(serviceWindowStart),
                GtfsDate.fromLocalDate(serviceWindowEnd)));
  }

  @Test
  public void serviceWindowStartingNowAndEndingIn7DaysShouldNotGenerateNotice() {

    var serviceWindowStart = TEST_NOW.toLocalDate();
    var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(7);
    var notices = validateSimpleServiceWindow(serviceWindowStart, serviceWindowEnd);
    assertThat(notices).isEmpty();
  }

  @Test
  public void serviceWindowStartingBeforeNowAndEndingAfter7DaysShouldNotGenerateNotice() {

    var serviceWindowStart = TEST_NOW.toLocalDate().minusDays(1);
    var serviceWindowEnd = TEST_NOW.toLocalDate().plusDays(8);
    var notices = validateSimpleServiceWindow(serviceWindowStart, serviceWindowEnd);
    assertThat(notices).isEmpty();
  }

  private List<ValidationNotice> validateSimpleServiceWindow(
      LocalDate serviceWindowStart, LocalDate serviceWindowEnd) {
    var serviceId = "s1";
    var noticeContainer = new NoticeContainer();
    var calendar =
        CalendarUtilTest.createGtfsCalendar(
            serviceId,
            serviceWindowStart,
            serviceWindowEnd,
            ImmutableSet.copyOf(DayOfWeek.values()));
    var calendarTable =
        GtfsCalendarTableContainer.forEntities(ImmutableList.of(calendar), noticeContainer);
    var dateTable = new GtfsCalendarDateTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);
    var frequencyTable = new GtfsFrequencyTableContainer(GtfsTableContainer.TableStatus.EMPTY_FILE);

    var tripBlock = createTripBlock(serviceId, 6, "b1");
    var tripContainer = GtfsTripTableContainer.forEntities(tripBlock, noticeContainer);

    var validator =
        new DateTripsValidator(
            new CurrentDateTime(TEST_NOW), dateTable, calendarTable, tripContainer, frequencyTable);

    validator.validate(noticeContainer);

    return noticeContainer.getValidationNotices();
  }

  private static ArrayList<GtfsTrip> createTripBlock(
      String serviceId, int tripsPerDay, String blockId) {
    ArrayList<GtfsTrip> trips = new ArrayList<>();
    for (int i = 0; i < tripsPerDay; i++) {
      trips.add(
          new GtfsTrip.Builder()
              .setCsvRowNumber(i + 1)
              .setTripId("t" + i)
              .setServiceId(serviceId)
              .setBlockId(blockId)
              .build());
    }
    return trips;
  }
}
