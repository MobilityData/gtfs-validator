package org.mobilitydata.gtfsvalidator.validator;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDate;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateExceptionType;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfo;
import org.mobilitydata.gtfsvalidator.table.GtfsFeedInfoTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsTrip;
import org.mobilitydata.gtfsvalidator.table.GtfsTripTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.util.ServiceIntervalCache;
import org.mobilitydata.gtfsvalidator.validator.FeedServiceWindowValidator.FeedValidBeyondTotalServiceWindowNotice;
import org.mobilitydata.gtfsvalidator.validator.FeedServiceWindowValidator.FutureCalendarNotice;
import org.mobilitydata.gtfsvalidator.validator.FeedServiceWindowValidator.ServiceWindowExtendsPastFeedPeriodNotice;

public class FeedServiceWindowValidatorTest {

  private static final LocalDate TODAY = LocalDate.of(2024, 1, 15);
  private static final DateForValidation DATE_FOR_VALIDATION = new DateForValidation(TODAY);

  // ---------------------------------------------------------------------------
  // Builders
  // ---------------------------------------------------------------------------

  private record CalendarMetadata(String serviceId, String startDate, String endDate) {}

  private record CalendarDateMetadata(
      String serviceId, String date, GtfsCalendarDateExceptionType exceptionType) {}

  private static GtfsCalendar buildCalendar(CalendarMetadata meta, int csvRowNumber) {
    return new GtfsCalendar.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(meta.serviceId())
        .setStartDate(GtfsDate.fromString(meta.startDate()))
        .setEndDate(GtfsDate.fromString(meta.endDate()))
        .setMonday(1)
        .setTuesday(1)
        .setWednesday(1)
        .setThursday(1)
        .setFriday(1)
        .setSaturday(1)
        .setSunday(1)
        .build();
  }

  private static GtfsCalendarDate buildCalendarDate(CalendarDateMetadata meta, int csvRowNumber) {
    return new GtfsCalendarDate.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(meta.serviceId())
        .setDate(GtfsDate.fromString(meta.date()))
        .setExceptionType(meta.exceptionType())
        .build();
  }

  private static GtfsFeedInfo buildFeedInfo(String feedStartDate, String feedEndDate) {
    return new GtfsFeedInfo.Builder()
        .setCsvRowNumber(1)
        .setFeedStartDate(GtfsDate.fromString(feedStartDate))
        .setFeedEndDate(GtfsDate.fromString(feedEndDate))
        .build();
  }

  private static GtfsTrip buildTrip(String serviceId, String tripId, int csvRowNumber) {
    return new GtfsTrip.Builder()
        .setCsvRowNumber(csvRowNumber)
        .setServiceId(serviceId)
        .setTripId(tripId)
        .build();
  }

  // ---------------------------------------------------------------------------
  // Notice generator
  // ---------------------------------------------------------------------------

  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList,
      List<CalendarDateMetadata> calendarDateMetadataList,
      String feedStartDate,
      String feedEndDate,
      List<String> serviceIds,
      DateForValidation dateForValidation) {
    NoticeContainer noticeContainer = new NoticeContainer();

    ImmutableList<GtfsCalendar> calendars =
        calendarMetadataList.stream()
            .map(meta -> buildCalendar(meta, 1))
            .collect(ImmutableList.toImmutableList());

    ImmutableList<GtfsCalendarDate> calendarDates =
        calendarDateMetadataList.stream()
            .map(meta -> buildCalendarDate(meta, 1))
            .collect(ImmutableList.toImmutableList());

    ImmutableList<GtfsTrip> trips =
        serviceIds.stream()
            .map(id -> buildTrip(id, "trip_" + id, 1))
            .collect(ImmutableList.toImmutableList());

    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(calendars, noticeContainer);
    GtfsCalendarDateTableContainer calendarDateTable =
        GtfsCalendarDateTableContainer.forEntities(calendarDates, noticeContainer);
    GtfsFeedInfoTableContainer feedInfoTable =
        GtfsFeedInfoTableContainer.forEntities(
            ImmutableList.of(buildFeedInfo(feedStartDate, feedEndDate)), noticeContainer);
    GtfsTripTableContainer tripTable = GtfsTripTableContainer.forEntities(trips, noticeContainer);

    new FeedServiceWindowValidator(
            dateForValidation,
            calendarTable,
            calendarDateTable,
            feedInfoTable,
            tripTable,
            new ServiceIntervalCache())
        .validate(noticeContainer);

    return noticeContainer.getValidationNotices();
  }

  // Convenience overload: uses the fixed TODAY constant.
  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList,
      List<CalendarDateMetadata> calendarDateMetadataList,
      String feedStartDate,
      String feedEndDate,
      List<String> serviceIds) {
    return generateNotices(
        calendarMetadataList,
        calendarDateMetadataList,
        feedStartDate,
        feedEndDate,
        serviceIds,
        DATE_FOR_VALIDATION);
  }

  // Convenience overload: calendar only (no calendar_dates).
  private static List<ValidationNotice> generateNotices(
      List<CalendarMetadata> calendarMetadataList,
      String feedStartDate,
      String feedEndDate,
      List<String> serviceIds) {
    return generateNotices(
        calendarMetadataList, ImmutableList.of(), feedStartDate, feedEndDate, serviceIds);
  }

  // ---------------------------------------------------------------------------
  // No notice cases
  // ---------------------------------------------------------------------------

  @Test
  public void feedInfoMissing_noNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            ImmutableList.of(buildCalendar(new CalendarMetadata("s1", "20240101", "20240131"), 1)),
            noticeContainer);
    GtfsTripTableContainer tripTable =
        GtfsTripTableContainer.forEntities(
            ImmutableList.of(buildTrip("s1", "trip_s1", 1)), noticeContainer);

    new FeedServiceWindowValidator(
            DATE_FOR_VALIDATION,
            calendarTable,
            GtfsCalendarDateTableContainer.forEntities(ImmutableList.of(), noticeContainer),
            GtfsFeedInfoTableContainer.forEntities(ImmutableList.of(), noticeContainer),
            tripTable,
            new ServiceIntervalCache())
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void feedInfoMissingDates_noNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsFeedInfo feedInfo = new GtfsFeedInfo.Builder().setCsvRowNumber(1).build();
    GtfsCalendarTableContainer calendarTable =
        GtfsCalendarTableContainer.forEntities(
            ImmutableList.of(buildCalendar(new CalendarMetadata("s1", "20240101", "20240131"), 1)),
            noticeContainer);
    GtfsTripTableContainer tripTable =
        GtfsTripTableContainer.forEntities(
            ImmutableList.of(buildTrip("s1", "trip_s1", 1)), noticeContainer);

    new FeedServiceWindowValidator(
            DATE_FOR_VALIDATION,
            calendarTable,
            GtfsCalendarDateTableContainer.forEntities(ImmutableList.of(), noticeContainer),
            GtfsFeedInfoTableContainer.forEntities(ImmutableList.of(feedInfo), noticeContainer),
            tripTable,
            new ServiceIntervalCache())
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  @Test
  public void serviceWindowWithinFeedPeriod_noNotice() {
    // TODAY = Jan 15. Service Jan 1 – Jan 31 covers today, feed matches service window exactly.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240101", "20240131")),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));
    assertThat(notices).isEmpty();
  }

  @Test
  public void feedExtendsJustAtThreshold_noNotice() {
    // Feed starts exactly THRESHOLD_DAYS (14) before service window -> no FeedBeyond notice.
    // TODAY = Jan 15, service starts Jan 15 -> no FutureCalendar notice either.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240115", "20240131")),
            "20240101", // 14 days before service start
            "20240131",
            ImmutableList.of("s1"));
    assertThat(notices).isEmpty();
  }

  @Test
  public void emptyTripTable_noNotice() {
    NoticeContainer noticeContainer = new NoticeContainer();
    new FeedServiceWindowValidator(
            DATE_FOR_VALIDATION,
            GtfsCalendarTableContainer.forEntities(
                ImmutableList.of(
                    buildCalendar(new CalendarMetadata("s1", "20240101", "20240131"), 1)),
                noticeContainer),
            GtfsCalendarDateTableContainer.forEntities(ImmutableList.of(), noticeContainer),
            GtfsFeedInfoTableContainer.forEntities(
                ImmutableList.of(buildFeedInfo("20240101", "20240131")), noticeContainer),
            GtfsTripTableContainer.forEntities(ImmutableList.of(), noticeContainer),
            new ServiceIntervalCache())
        .validate(noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // ServiceWindowExtendsPastFeedPeriodNotice
  // ---------------------------------------------------------------------------

  @Test
  public void serviceStartsBeforeFeedStart_oneNotice() {
    // Service starts Dec 1 2023, feed starts Jan 1 2024.
    // TODAY = Jan 15 2024, service covers today -> no FutureCalendar notice.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20231201", "20240131")),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(ServiceWindowExtendsPastFeedPeriodNotice.class);
  }

  @Test
  public void serviceEndsAfterFeedEnd_oneNotice() {
    // Service ends Feb 28, feed ends Jan 31. TODAY = Jan 15 -> covered, no FutureCalendar.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240101", "20240229")),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(ServiceWindowExtendsPastFeedPeriodNotice.class);
  }

  @Test
  public void serviceBothSidesOutsideFeed_oneNoticeWithBothDays() {
    // Service Dec 1 2023 – Feb 29 2024, feed Jan 1 – Jan 31: sticks out on both sides.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20231201", "20240229")),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(ServiceWindowExtendsPastFeedPeriodNotice.class);
  }

  @Test
  public void multipleServices_onlyOutlierTriggersNotice() {
    // s1 is within the feed period, s2 starts before it.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("s1", "20240101", "20240131"),
                new CalendarMetadata("s2", "20231201", "20240131")),
            "20240101",
            "20240131",
            ImmutableList.of("s1", "s2"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(ServiceWindowExtendsPastFeedPeriodNotice.class);
  }

  @Test
  public void multipleServices_bothOutside_twoNotices() {
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("s1", "20231201", "20240131"),
                new CalendarMetadata("s2", "20240101", "20240229")),
            "20240101",
            "20240131",
            ImmutableList.of("s1", "s2"));

    assertThat(notices).hasSize(2);
  }

  @Test
  public void calendarDatesOnlyService_outsideFeedPeriod_oneNotice() {
    // Service defined only via SERVICE_ADDED in calendar_dates.txt, date before feed start.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(),
            ImmutableList.of(
                new CalendarDateMetadata(
                    "s1", "20231201", GtfsCalendarDateExceptionType.SERVICE_ADDED)),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    // ServiceWindowExtendsPastFeedPeriodNotice + FeedValidBeyondTotalServiceWindowNotice.
    assertThat(notices).hasSize(2);
    assertThat(notices.get(0)).isInstanceOf(ServiceWindowExtendsPastFeedPeriodNotice.class);
    assertThat(notices.get(1)).isInstanceOf(FeedValidBeyondTotalServiceWindowNotice.class);
  }

  @Test
  public void serviceRemovedDatesIgnored_noNotice() {
    // SERVICE_REMOVED dates outside the feed period should not trigger a notice.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240101", "20240131")),
            ImmutableList.of(
                new CalendarDateMetadata(
                    "s1", "20231201", GtfsCalendarDateExceptionType.SERVICE_REMOVED)),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).isEmpty();
  }

  // ---------------------------------------------------------------------------
  // FeedValidBeyondTotalServiceWindowNotice
  // ---------------------------------------------------------------------------

  @Test
  public void feedStartsTooEarly_beyondThreshold_oneNotice() {
    // Service Jan 15 – Jan 31, feed starts Dec 31 (15 days before) -> triggers FeedBeyond.
    // TODAY = Jan 15, service starts on today -> no FutureCalendar.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240115", "20240131")),
            "20231231", // 15 days before service start
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FeedValidBeyondTotalServiceWindowNotice.class);
  }

  @Test
  public void feedEndsTooLate_beyondThreshold_oneNotice() {
    // Service Jan 1 – Jan 15, feed ends Feb 1 (17 days after service end).
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240101", "20240115")),
            "20240101",
            "20240201", // 17 days after service end
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FeedValidBeyondTotalServiceWindowNotice.class);
  }

  @Test
  public void feedExceedsBothSides_oneNotice() {
    // Feed extends beyond THRESHOLD_DAYS on both sides -> still only one notice.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240115", "20240115")),
            "20231231", // 15 days before
            "20240201", // 17 days after
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FeedValidBeyondTotalServiceWindowNotice.class);
  }

  @Test
  public void multipleServices_totalWindowUsedForCheck2() {
    // s1: Jan 1 – Jan 31. s2: Feb 1 – Feb 29.
    // Total window: Jan 1 – Feb 29. Feed: Dec 1 – Feb 29 (31 days before total window start).
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("s1", "20240101", "20240131"),
                new CalendarMetadata("s2", "20240201", "20240229")),
            "20231201",
            "20240229",
            ImmutableList.of("s1", "s2"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FeedValidBeyondTotalServiceWindowNotice.class);
  }

  @Test
  public void bothNoticesCanCoexist() {
    // s1: Dec 1 2023 – Jan 31 2024, extends before feed start (ServiceWindow notice).
    // Feed: Jan 1 – Mar 31, extends far beyond service window end Jan 31 (FeedBeyond notice).
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20231201", "20240131")),
            "20240101",
            "20240331", // 60 days after service end
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(2);
  }

  @Test
  public void allServicesStartInFuture_futureCalendarNotice() {
    // TODAY = Jan 15. All services start Jan 16 or later -> FutureCalendar triggered.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240116", "20240131")),
            "20240116",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FutureCalendarNotice.class);
  }

  @Test
  public void serviceStartsExactlyToday_noFutureCalendarNotice() {
    // TODAY = Jan 15. Service starts Jan 15 -> isAfter(today) is false, no notice.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240115", "20240131")),
            "20240115",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).isEmpty();
  }

  @Test
  public void serviceStartedInPast_noFutureCalendarNotice() {
    // TODAY = Jan 15. Service started Jan 1 -> covers today, no FutureCalendar.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240101", "20240131")),
            "20240101",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).isEmpty();
  }

  @Test
  public void multipleServicesBothFuture_futureCalendarNotice() {
    // TODAY = Jan 15. Both services start in the future; minServiceStartDate = Jan 16.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("s1", "20240116", "20240131"),
                new CalendarMetadata("s2", "20240120", "20240229")),
            "20240116",
            "20240229",
            ImmutableList.of("s1", "s2"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FutureCalendarNotice.class);
  }

  @Test
  public void oneServicePastOneServiceFuture_noFutureCalendarNotice() {
    // TODAY = Jan 15. s1 starts Jan 1 (past), s2 starts Jan 20 (future).
    // totalWindowStart = Jan 1 -> not after today, no FutureCalendar.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(
                new CalendarMetadata("s1", "20240101", "20240131"),
                new CalendarMetadata("s2", "20240120", "20240229")),
            "20240101",
            "20240229",
            ImmutableList.of("s1", "s2"));

    assertThat(notices).isEmpty();
  }

  @Test
  public void futureCalendarAndFeedBeyondNotices_canCoexist() {
    // TODAY = Jan 15. Service starts Feb 1 (future) -> FutureCalendar.
    // Feed starts Nov 1 (>14 days before service start) -> FeedBeyond.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20240201", "20240229")),
            "20231101", // far before service window
            "20240229",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(2);
    assertThat(notices.stream().anyMatch(n -> n instanceof FutureCalendarNotice)).isTrue();
    assertThat(notices.stream().anyMatch(n -> n instanceof FeedValidBeyondTotalServiceWindowNotice))
        .isTrue();
  }

  @Test
  public void futureCalendarViaCalendarDatesOnly_futureCalendarNotice() {
    // Service defined only via SERVICE_ADDED on a future date.
    // TODAY = Jan 15. Date = Jan 20 -> FutureCalendar triggered.
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(),
            ImmutableList.of(
                new CalendarDateMetadata(
                    "s1", "20240120", GtfsCalendarDateExceptionType.SERVICE_ADDED)),
            "20240120",
            "20240131",
            ImmutableList.of("s1"));

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FutureCalendarNotice.class);
  }

  @Test
  public void customDate_serviceInFutureRelativeToCustomDate_futureCalendarNotice() {
    // Use a custom DateForValidation to verify the injected date is used, not LocalDate.now().
    DateForValidation customDate = new DateForValidation(LocalDate.of(2025, 6, 1));
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20250701", "20250731")),
            ImmutableList.of(),
            "20250701",
            "20250731",
            ImmutableList.of("s1"),
            customDate);

    assertThat(notices).hasSize(1);
    assertThat(notices.get(0)).isInstanceOf(FutureCalendarNotice.class);
  }

  @Test
  public void customDate_serviceCoversCustomDate_noFutureCalendarNotice() {
    // Same service, but custom date is within the service window.
    DateForValidation customDate = new DateForValidation(LocalDate.of(2025, 7, 15));
    List<ValidationNotice> notices =
        generateNotices(
            ImmutableList.of(new CalendarMetadata("s1", "20250701", "20250731")),
            ImmutableList.of(),
            "20250701",
            "20250731",
            ImmutableList.of("s1"),
            customDate);

    assertThat(notices).isEmpty();
  }
}
