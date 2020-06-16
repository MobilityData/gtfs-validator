package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;
import java.time.LocalDate;

public class CalendarEndDateBeforeStartDateNotice extends ErrorNotice {

    public CalendarEndDateBeforeStartDateNotice(final String filename, final String entityId, final LocalDate startDate,
                                                final LocalDate endDate) {
        super(filename, E_032,
                "calendar.txt end_date is before start_date",
                "Then end_date of " + endDate + " occurs before the start_date of " +
                        startDate + " for service_id: " + entityId + " in file: " + filename + ".",
                entityId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}