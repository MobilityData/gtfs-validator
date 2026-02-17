package org.mobilitydata.gtfsvalidator.validator;

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarService;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

@GtfsValidator
public class ServiceHasNoActiveDayOfTheWeekValidator extends FileValidator {
  private final GtfsCalendarTableContainer calendarTable;

  @Inject
  ServiceHasNoActiveDayOfTheWeekValidator(GtfsCalendarTableContainer calendarTable) {
    this.calendarTable = calendarTable;
  }

  private boolean hasNoActiveDayOfTheWeek(GtfsCalendar calendar) {
    return calendar.monday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.tuesday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.wednesday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.thursday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.friday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.saturday() == GtfsCalendarService.NOT_AVAILABLE
        && calendar.sunday() == GtfsCalendarService.NOT_AVAILABLE;
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
