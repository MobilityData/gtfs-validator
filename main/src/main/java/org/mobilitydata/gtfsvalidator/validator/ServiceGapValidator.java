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
 * Validates that no service has a gap of more than 13 days between active service dates.
 *
 * <p>A gap is defined as a period of inactivity between two consecutive active intervals within the
 * overall service range. Dates before the first active day or after the last are not considered
 * gaps.
 *
 * <p>Generated notice: {@link ServiceWithBigGapNotice}.
 */
@GtfsValidator
public class ServiceGapValidator extends FileValidator {

  private static final int MAX_GAP_DAYS = 13;

  private final ServiceIntervalCache serviceIntervalCache;
  private final GtfsCalendarTableContainer calendarTableContainer;
  private final GtfsCalendarDateTableContainer calendarDateTableContainer;

  @Inject
  ServiceGapValidator(
      ServiceIntervalCache serviceIntervalCache,
      GtfsCalendarTableContainer calendarTableContainer,
      GtfsCalendarDateTableContainer calendarDateTableContainer) {
    this.serviceIntervalCache = serviceIntervalCache;
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
              ServiceInterval interval =
                  serviceIntervalCache.getIntervals(
                      serviceId, calendarTableContainer, calendarDateTableContainer);
              if (interval == null) {
                return;
              }
              for (DateInterval gap : interval.getGaps()) {
                if (gap.lengthInDays() > MAX_GAP_DAYS) {
                  noticeContainer.addValidationNotice(
                      new ServiceWithBigGapNotice(
                          serviceId, gap.getStart(), gap.getEnd(), gap.lengthInDays()));
                }
              }
            });
  }

  /** A service has a gap of more than 13 days between active service dates. */
  @GtfsValidationNotice(
      severity = INFO,
      files = @FileRefs({GtfsCalendarSchema.class, GtfsCalendarDateSchema.class}))
  static class ServiceWithBigGapNotice extends ValidationNotice {

    /** The service_id that has the gap. */
    private final String serviceId;

    //    /** The first day of the gap. */
    //    private final LocalDate gapStartDate;
    //
    //    /** The last day of the gap. */
    //    private final LocalDate gapEndDate;
    //
    //    /** The number of days in the gap. */
    //    private final long gapDurationDays;
    //
    ServiceWithBigGapNotice(
        String serviceId, LocalDate gapStartDate, LocalDate gapEndDate, long gapDurationDays) {
      this.serviceId = serviceId;
      //      this.gapStartDate = gapStartDate;
      //      this.gapEndDate = gapEndDate;
      //      this.gapDurationDays = gapDurationDays;
    }
  }
}
