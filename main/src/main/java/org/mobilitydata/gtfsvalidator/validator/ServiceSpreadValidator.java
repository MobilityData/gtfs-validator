/*
 * Copyright 2024 MobilityData IO
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
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.input.DateForValidation;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarDateTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;
import org.mobilitydata.gtfsvalidator.util.DateInterval;
import org.mobilitydata.gtfsvalidator.util.ServiceInterval;
import org.mobilitydata.gtfsvalidator.util.ServiceIntervalCache;

/**
 * Validates data related to the calendar spread of a service. Checks that no service has a gap of
 * more than 13 days between active service dates. Also checks the end date of a service is too far
 * in the future.
 *
 * <p>A gap is defined as a period of inactivity between two consecutive active intervals within the
 * overall service range. Dates before the first active day or after the last are not considered
 * gaps.
 *
 * <p>Generated notices: {@link BigGapInServiceNotice}, {@link ServiceExtendsFarInTheFutureNotice}
 */
@GtfsValidator
public class ServiceSpreadValidator extends FileValidator {

  private static final int MAX_GAP_DAYS = 13;
  private static final int MAX_FUTURE_EXTENT_DAYS = 2 * 365;

  private final ServiceIntervalCache serviceIntervalCache;
  private final DateForValidation dateForValidation;
  private final GtfsCalendarTableContainer calendarTableContainer;
  private final GtfsCalendarDateTableContainer calendarDateTableContainer;

  @Inject
  ServiceSpreadValidator(
      ServiceIntervalCache serviceIntervalCache,
      DateForValidation dateForValidation,
      GtfsCalendarTableContainer calendarTableContainer,
      GtfsCalendarDateTableContainer calendarDateTableContainer) {
    this.serviceIntervalCache = serviceIntervalCache;
    this.dateForValidation = dateForValidation;
    this.calendarTableContainer = calendarTableContainer;
    this.calendarDateTableContainer = calendarDateTableContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    calendarTableContainer
        .getEntities()
        .forEach(
            calendar -> {
              String serviceId = calendar.serviceId();
              ServiceInterval intervals =
                  serviceIntervalCache.getIntervals(
                      serviceId, calendarTableContainer, calendarDateTableContainer);
              if (intervals == null) {
                return;
              }
              for (DateInterval gap : intervals.getGaps()) {
                if (gap.lengthInDays() > MAX_GAP_DAYS) {
                  noticeContainer.addValidationNotice(
                      new BigGapInServiceNotice(
                          serviceId,
                          gap.start().minusDays(1), // Last active day before the gap
                          gap.end().plusDays(1), // First active day after the gap
                          gap.lengthInDays()));
                }
              }
              LocalDate lastActiveDate = intervals.lastActiveDate();
              LocalDate now = dateForValidation.getDate();
              int diff = (int) (lastActiveDate.toEpochDay() - now.toEpochDay());
              if (diff > MAX_FUTURE_EXTENT_DAYS) {
                noticeContainer.addValidationNotice(
                    new ServiceExtendsFarInTheFutureNotice(serviceId, lastActiveDate));
              }
            });
  }

  /** A service has a gap of more than 13 days between active service dates. */
  @GtfsValidationNotice(
      severity = INFO,
      files = @FileRefs({GtfsCalendarSchema.class, GtfsCalendarDateSchema.class}))
  static class BigGapInServiceNotice extends ValidationNotice {

    /** The service_id that has the gap. */
    private final String serviceId;

    /** The first day of the gap. */
    private final String gapStartDate;

    /** The last day of the gap. */
    private final String gapEndDate;

    /** The number of days in the gap. */
    private final long gapDurationDays;

    BigGapInServiceNotice(
        String serviceId, LocalDate gapStartDate, LocalDate gapEndDate, long gapDurationDays) {
      this.serviceId = serviceId;
      this.gapStartDate = gapStartDate.toString();
      this.gapEndDate = gapEndDate.toString();
      this.gapDurationDays = gapDurationDays;
    }
  }

  /** A service end date is more than 2 years in the future. */
  @GtfsValidationNotice(
      severity = INFO,
      files = @FileRefs({GtfsCalendarSchema.class, GtfsCalendarDateSchema.class}))
  static class ServiceExtendsFarInTheFutureNotice extends ValidationNotice {

    /** The service_id that ends far in the future. */
    private final String serviceId;

    /** The end date of the service (YYYY-MM-DD format). */
    private final String serviceWindowEndDate;

    ServiceExtendsFarInTheFutureNotice(String serviceId, LocalDate serviceWindowEndDate) {
      this.serviceId = serviceId;
      this.serviceWindowEndDate = serviceWindowEndDate.toString();
    }
  }
}
