package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.ServiceIntervalCache;
import org.mobilitydata.gtfsvalidator.validator.ServiceGapValidator.BigGapInServiceNotice;

public class ServiceGapValidatorTest {

  private record CalendarMetadata(
      String serviceId, String startDate, String endDate, boolean activeAllWeek) {}

  private record CalendarDateMetadata(
      String serviceId, String date, GtfsCalendarDateExceptionType exceptionType) {}

  private static GtfsCalendar buildCalendar(CalendarMetadata meta, int csvRowNumber) {
    GtfsCalendar.Builder builder =
        new GtfsCalendar.Builder()
            .setCsvRowNumber(csvRowNumber)
            .setServiceId(meta.serviceId())
            .setStartDate(GtfsDate.fromString(meta.startDate()))
            .setEndDate(GtfsDate.fromString(meta.endDate()));
    if (meta.activeAllWeek()) {
      builder
          .setMonday(1)
          .setTuesday(1)
          .setWednesday(1)
          .setThursday(1)
          .setFriday(1)
          .setSaturday(1)
          .setSunday(1);
    }
    return builder.build();
  }

  private static GtfsCalendarDate buildCalendarDate(CalendarDateMetadata meta, int csvRowNumber) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(meta.serviceId())
        .setDate(GtfsDate.fromString(meta.date()))
        .setExceptionType(meta.exceptionType())
        .build();
  }

  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList,
      List<CalendarDateMetadata> calendarDateMetadataList) {
    NoticeContainer noticeContainer = new NoticeContainer();

    ImmutableList<GtfsCalendar> calendars =
        calendarMetadataList.stream()
            .map(meta -> buildCalendar(meta, 1))
            .collect(ImmutableList.toImmutableList());

    ImmutableList<GtfsCalendarDate> calendarDates =
        calendarDateMetadataList.stream()
            .map(meta -> buildCalendarDate(meta, 1))
            .collect(ImmutableList.toImmutableList());

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, noticeContainer);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(calendarDates, noticeContainer);

    new ServiceGapValidator(new ServiceIntervalCache(), calendarTable, calendarDateTable)
        .validate(noticeContainer);

    return noticeContainer.getValidationNotices();
  }

  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList) {
    return generateNotices(calendarMetadataList, ImmutableList.of());
  }

  @Test
  public void continuousService_noNotice() {
    // Service runs every day for a full month — no gaps at all.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240131", true)));
    assertThat(notices).isEmpty();
  }

  @Test
  public void gapExactlyAtLimit_noNotice() {
    // Service active Jan 1–10, then SERVICE_REMOVED Jan 11–23 (13 days gap) → no notice.
    ImmutableList.Builder<CalendarDateMetadata> removals = ImmutableList.builder();
    for (int d = 11; d <= 23; d++) {
      removals.add(
          new CalendarDateMetadata(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
    }
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240131", true)),
            removals.build());
    assertThat(notices).isEmpty();
  }

  @Test
  public void gapJustAboveLimit_oneNotice() {
    // SERVICE_REMOVED Jan 11–24 = 14-day gap (> 13) → exactly one BigGapInServiceNotice.
    ImmutableList.Builder<CalendarDateMetadata> removals = ImmutableList.builder();
    for (int d = 11; d <= 24; d++) {
      removals.add(
          new CalendarDateMetadata(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
    }
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240131", true)),
            removals.build());

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(BigGapInServiceNotice.class);
  }

  @Test
  public void largeGap_correctNoticeEmitted() {
    // SERVICE_REMOVED Jan 11–24 = 14-day gap.
    // Expected notice: serviceId=service_1, gapStartDate=Jan 10, gapEndDate=Jan 25, duration=14.
    ImmutableList.Builder<CalendarDateMetadata> removals = ImmutableList.builder();
    for (int d = 11; d <= 24; d++) {
      removals.add(
          new CalendarDateMetadata(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
    }
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240131", true)),
            removals.build());

    assertThat(notices)
        .containsExactly(
            new BigGapInServiceNotice(
                "service_1", LocalDate.of(2024, 1, 10), LocalDate.of(2024, 1, 25), 14));
  }

  @Test
  public void multipleServices_onlyGappyServiceReportsNotice() {
    // service_1: SERVICE_REMOVED for 21 days → big gap. service_2: continuous, no removals.
    ImmutableList.Builder<CalendarDateMetadata> removals = ImmutableList.builder();
    for (int d = 11; d <= 31; d++) {
      removals.add(
          new CalendarDateMetadata(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
    }
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("service_1", "20240101", "20240229", true),
                new CalendarMetadata("service_2", "20240101", "20240229", true)),
            removals.build());

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(BigGapInServiceNotice.class);
  }

  @Test
  public void multipleServices_bothHaveLargeGap_twoNotices() {
    // service_1: SERVICE_REMOVED Jan 11–31 (21 days). service_2: SERVICE_REMOVED Mar 11–31 (21
    // days).
    ImmutableList.Builder<CalendarDateMetadata> removals = ImmutableList.builder();
    for (int d = 11; d <= 31; d++) {
      removals.add(
          new CalendarDateMetadata(
              "service_1",
              String.format("202401%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
      removals.add(
          new CalendarDateMetadata(
              "service_2",
              String.format("202403%02d", d),
              GtfsCalendarDateExceptionType.SERVICE_REMOVED));
    }
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("service_1", "20240101", "20240229", true),
                new CalendarMetadata("service_2", "20240301", "20240430", true)),
            removals.build());

    assertThat(notices).hasSize(2);
  }

  @Test
  public void serviceRemovedDateFillsGap_noNotice() {
    // calendar.txt says every day Jan 1 – Feb 28.
    // Several SERVICE_REMOVED entries in the middle don't create a gap > 13 days.
    List<CalendarDateMetadata> removals =
        ImmutableList.of(
            new CalendarDateMetadata(
                "service_1", "20240115", GtfsCalendarDateExceptionType.SERVICE_REMOVED),
            new CalendarDateMetadata(
                "service_1", "20240116", GtfsCalendarDateExceptionType.SERVICE_REMOVED),
            new CalendarDateMetadata(
                "service_1", "20240117", GtfsCalendarDateExceptionType.SERVICE_REMOVED));

    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240229", true)),
            removals);

    assertThat(notices).isEmpty();
  }

  @Test
  public void calendarDatesOnlyService_addedDatesWithSmallGap_noNotice() {
    // Service defined purely via calendar_dates.txt with SERVICE_ADDED entries.
    // Jan 1 and Jan 14 → gap of 13 days (≤ MAX_GAP_DAYS → no notice).
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(),
            ImmutableList.of(
                new CalendarDateMetadata(
                    "service_cd", "20240101", GtfsCalendarDateExceptionType.SERVICE_ADDED),
                new CalendarDateMetadata(
                    "service_cd", "20240114", GtfsCalendarDateExceptionType.SERVICE_ADDED)));

    assertThat(notices).isEmpty();
  }

  @Test
  public void emptyCalendarTable_noNotice() {
    List<ValidationNotice> notices = generateNotices(ImmutableList.of());
    assertThat(notices).isEmpty();
  }

  @Test
  public void singleDayService_noNotice() {
    // A service with only one active day cannot have a gap.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("service_1", "20240101", "20240101", true)));
    assertThat(notices).isEmpty();
  }
}
