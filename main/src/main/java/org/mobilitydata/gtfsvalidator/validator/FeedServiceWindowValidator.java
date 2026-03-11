/*
 * Copyright 2026 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.annotation.Nullable;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.util.ServiceInterval;
import org.mobilitydata.gtfsvalidator.util.ServiceIntervalCache;

/**
 * Validates the relationship between the feed validity period (feed_info.txt) and the service
 * windows defined in calendar.txt and calendar_dates.txt.
 *
 * <p>Three complementary checks are performed in a single pass over all unique service IDs sourced
 * from trips.txt:
 *
 * <ol>
 *   <li>Service window extends past feed period: the feed validity period should cover every
 *       service window. A {@link ServiceWindowExtendsPastFeedPeriodNotice} is emitted once per
 *       service whose active date range extends outside the feed validity period, summarizing how
 *       many days fall before feed_start_date or after feed_end_date.
 *   <li>Feed valid beyond total service window: the feed validity period should not extend far
 *       beyond the total service window. A {@link FeedValidBeyondTotalServiceWindowNotice} is
 *       emitted when the feed start/end date exceeds the total service window bounds by more than
 *       {@link #THRESHOLD_DAYS} days.
 *   <li>Future calendar: at least one service in the feed should cover today. A {@link
 *       FutureCalendarNotice} is emitted when the minimum service start date across all services is
 *       strictly after today's date.
 * </ol>
 *
 * <p>Generated notices: {@link ServiceWindowExtendsPastFeedPeriodNotice}, {@link
 * FeedValidBeyondTotalServiceWindowNotice}, {@link FutureCalendarNotice}.
 */
@GtfsValidator
public class FeedServiceWindowValidator extends FileValidator {

  static final int THRESHOLD_DAYS = 14;

  private final GtfsCalendarTableContainer calendarTable;
  private final GtfsCalendarDateTableContainer calendarDateTable;
  private final GtfsFeedInfoTableContainer feedInfoTable;
  private final GtfsTripTableContainer tripTable;
  private final ServiceIntervalCache serviceIntervalCache;
  private final DateForValidation dateForValidation;

  @Inject
  FeedServiceWindowValidator(
      DateForValidation dateForValidation,
      GtfsCalendarTableContainer calendarTable,
      GtfsCalendarDateTableContainer calendarDateTable,
      GtfsFeedInfoTableContainer feedInfoTable,
      GtfsTripTableContainer tripTable,
      ServiceIntervalCache serviceIntervalCache) {
    this.dateForValidation = dateForValidation;
    this.calendarTable = calendarTable;
    this.calendarDateTable = calendarDateTable;
    this.feedInfoTable = feedInfoTable;
    this.tripTable = tripTable;
    this.serviceIntervalCache = serviceIntervalCache;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (feedInfoTable.getEntities().isEmpty()) {
      return;
    }
    GtfsFeedInfo feedInfo = feedInfoTable.getEntities().get(0);
    if (!feedInfo.hasFeedStartDate() || !feedInfo.hasFeedEndDate()) {
      return;
    }

    LocalDate feedStartDate = feedInfo.feedStartDate().getLocalDate();
    LocalDate feedEndDate = feedInfo.feedEndDate().getLocalDate();

    // Track total service window bounds via ServiceIntervalCache (respects SERVICE_REMOVED).
    LocalDate totalWindowStart = null;
    LocalDate totalWindowEnd = null;

    // Iterate unique service IDs from trips.txt. One entry per service, no deduplication needed.
    for (String serviceId : tripTable.byServiceIdMap().keySet()) {
      ServiceInterval interval =
          serviceIntervalCache.getIntervals(serviceId, calendarTable, calendarDateTable);

      if (interval == null || interval.isEmpty()) {
        continue;
      }

      // Check 1: one notice per service summarizing how far it extends outside the feed period.
      checkServiceWindow(serviceId, interval, feedStartDate, feedEndDate, noticeContainer);

      // Check 2 & 3: accumulate total service window.
      totalWindowStart = earliest(totalWindowStart, interval.firstActiveDate());
      totalWindowEnd = latest(totalWindowEnd, interval.lastActiveDate());
    }

    if (totalWindowStart == null || totalWindowEnd == null) {
      return;
    }

    // Check 2: emit notice if feed period extends far beyond total service window.
    if (feedStartDate.isBefore(totalWindowStart.minusDays(THRESHOLD_DAYS))
        || feedEndDate.isAfter(totalWindowEnd.plusDays(THRESHOLD_DAYS))) {
      noticeContainer.addValidationNotice(
          new FeedValidBeyondTotalServiceWindowNotice(
              feedStartDate.toString(),
              feedEndDate.toString(),
              totalWindowStart.toString(),
              totalWindowEnd.toString()));
    }

    // Check 3: emit notice if all services start in the future (none cover today).
    if (totalWindowStart.isAfter(dateForValidation.getDate())) {
      noticeContainer.addValidationNotice(
          new FutureCalendarNotice(totalWindowStart.toString(), dateForValidation.getDate().toString()));
    }
  }

  /**
   * Emits a {@link ServiceWindowExtendsPastFeedPeriodNotice} if the service active window extends
   * outside the feed validity period.
   */
  private static void checkServiceWindow(
      String serviceId,
      ServiceInterval interval,
      LocalDate feedStartDate,
      LocalDate feedEndDate,
      NoticeContainer noticeContainer) {
    LocalDate serviceStart = interval.firstActiveDate();
    LocalDate serviceEnd = interval.lastActiveDate();

    long daysBeforeFeedStart =
        serviceStart.isBefore(feedStartDate)
            ? ChronoUnit.DAYS.between(serviceStart, feedStartDate)
            : 0;
    long daysAfterFeedEnd =
        serviceEnd.isAfter(feedEndDate) ? ChronoUnit.DAYS.between(feedEndDate, serviceEnd) : 0;

    if (daysBeforeFeedStart > 0 || daysAfterFeedEnd > 0) {
      noticeContainer.addValidationNotice(
          new ServiceWindowExtendsPastFeedPeriodNotice(
              serviceId,
              serviceStart.toString(),
              serviceEnd.toString(),
              daysBeforeFeedStart,
              daysAfterFeedEnd));
    }
  }

  private static LocalDate earliest(@Nullable LocalDate current, LocalDate candidate) {
    return current == null || candidate.isBefore(current) ? candidate : current;
  }

  private static LocalDate latest(@Nullable LocalDate current, LocalDate candidate) {
    return current == null || candidate.isAfter(current) ? candidate : current;
  }

  /** A service window is not covered by the feed's validity period. */
  @GtfsValidationNotice(
      severity = INFO,
      files =
          @FileRefs({
            GtfsCalendarSchema.class,
            GtfsCalendarDateSchema.class,
            GtfsFeedInfoSchema.class
          }))
  static class ServiceWindowExtendsPastFeedPeriodNotice extends ValidationNotice {

    /** The service_id whose active window extends outside the feed validity period. */
    private final String serviceId;

    /** The first active date of the service window. */
    private final String serviceWindowStartDate;

    /** The last active date of the service window. */
    private final String serviceWindowEndDate;

    /** Number of days the service window extends before feed_start_date (0 if none). */
    private final long daysBeforeFeedStart;

    /** Number of days the service window extends after feed_end_date (0 if none). */
    private final long daysAfterFeedEnd;

    ServiceWindowExtendsPastFeedPeriodNotice(
        String serviceId,
        String serviceWindowStart,
        String serviceWindowEnd,
        long daysBeforeFeedStart,
        long daysAfterFeedEnd) {
      this.serviceId = serviceId;
      this.serviceWindowStartDate = serviceWindowStart;
      this.serviceWindowEndDate = serviceWindowEnd;
      this.daysBeforeFeedStart = daysBeforeFeedStart;
      this.daysAfterFeedEnd = daysAfterFeedEnd;
    }
  }

  /** The feed is valid 14 days beyond its total service window. */
  @GtfsValidationNotice(
      severity = INFO,
      files =
          @FileRefs({
            GtfsCalendarSchema.class,
            GtfsCalendarDateSchema.class,
            GtfsFeedInfoSchema.class
          }))
  static class FeedValidBeyondTotalServiceWindowNotice extends ValidationNotice {

    /** The feed start date from feed_info.txt. */
    private final String feedStartDate;

    /** The feed end date from feed_info.txt. */
    private final String feedEndDate;

    /** The earliest active service date across all services. */
    private final String serviceWindowStartDate;

    /** The latest active service date across all services. */
    private final String serviceWindowEndDate;

    FeedValidBeyondTotalServiceWindowNotice(
        String feedStartDate,
        String feedEndDate,
        String totalServiceWindowStartDate,
        String totalServiceWindowEndDate) {
      this.feedStartDate = feedStartDate;
      this.feedEndDate = feedEndDate;
      this.serviceWindowStartDate = totalServiceWindowStartDate;
      this.serviceWindowEndDate = totalServiceWindowEndDate;
    }
  }

  /** All services in the feed start in the future; no service covers today's date. */
  @GtfsValidationNotice(
      severity = INFO,
      files = @FileRefs({GtfsCalendarSchema.class, GtfsCalendarDateSchema.class}))
  static class FutureCalendarNotice extends ValidationNotice {

    /** The earliest service start date across all services in the feed. */
    private final String minServiceStartDate;

    /** Today's date at validation time. */
    private final String currentDate;

    FutureCalendarNotice(String minServiceStartDate, String currentDate) {
      this.minServiceStartDate = minServiceStartDate;
      this.currentDate = currentDate;
    }
  }
}
