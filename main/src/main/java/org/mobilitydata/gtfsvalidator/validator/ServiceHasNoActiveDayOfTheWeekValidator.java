package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

@GtfsValidator
public class ServiceHasNoActiveDayOfTheWeekValidator extends FileValidator {
  private final GtfsCalendarTableContainer calendarTable;

  @Inject
  ServiceHasNoActiveDayOfTheWeekValidator(GtfsCalendarTableContainer calendarTable) {
    this.calendarTable = calendarTable;
  }

  private boolean hasNoActiveDayOfTheWeek(GtfsCalendar calendar) {
    return !calendar.hasMonday()
        && !calendar.hasTuesday()
        && !calendar.hasWednesday()
        && !calendar.hasThursday()
        && !calendar.hasFriday()
        && !calendar.hasSaturday()
        && !calendar.hasSunday();
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    calendarTable.getEntities().stream()
        .filter(calendar -> calendar.hasServiceId() && hasNoActiveDayOfTheWeek(calendar))
        .forEach(
            calendar ->
                noticeContainer.addValidationNotice(
                    new ServiceHasNoActiveDayOfTheWeekNotice(
                        calendar.csvRowNumber(), calendar.serviceId())));
  }

  /** A service is not valid for any day of the week. */
  @GtfsValidationNotice(
      severity = SeverityLevel.WARNING,
      files = @GtfsValidationNotice.FileRefs({GtfsCalendarSchema.class}))
  static class ServiceHasNoActiveDayOfTheWeekNotice extends ValidationNotice {
    /** The row number in calendar.txt where the error occurs. */
    private final int csvRowNumber;

    /** The service_id field value. */
    private final String serviceId;

    ServiceHasNoActiveDayOfTheWeekNotice(int csvRowNumber, String serviceId) {
      this.csvRowNumber = csvRowNumber;
      this.serviceId = serviceId;
    }
  }
}
