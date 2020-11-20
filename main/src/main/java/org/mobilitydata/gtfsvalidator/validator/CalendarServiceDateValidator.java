package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.StartAndEndDateOutOfOrder;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendar;
import org.mobilitydata.gtfsvalidator.table.GtfsCalendarTableContainer;

/**
 * Validates that start_date <= end_date for all rows in "calendar.txt".
 *
 * Generated notices:
 * * StartAndEndDateOutOfOrder
 */
@GtfsValidator
public class CalendarServiceDateValidator extends FileValidator {
    @Inject
    GtfsCalendarTableContainer calendarTable;

    @Override
    public void validate(NoticeContainer noticeContainer) {
        for (GtfsCalendar calendar : calendarTable.getEntities()) {
            if (calendar.hasStartDate() && calendar.hasEndDate() && calendar.startDate().isAfter(calendar.endDate())) {
                noticeContainer.addNotice(new StartAndEndDateOutOfOrder(calendarTable.gtfsFilename(),
                        calendar.serviceId(),
                        calendar.csvRowNumber(),
                        calendar.startDate(), calendar.endDate()));
            }
        }
    }
}
