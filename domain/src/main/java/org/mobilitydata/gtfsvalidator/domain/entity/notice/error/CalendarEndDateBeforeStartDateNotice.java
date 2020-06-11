package org.mobilitydata.gtfsvalidator.domain.entity.notice.error;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.NoticeExporter;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.ErrorNotice;

import java.io.IOException;
import java.time.LocalDateTime;

public class CalendarEndDateBeforeStartDateNotice extends ErrorNotice {

    public CalendarEndDateBeforeStartDateNotice(final String filename, final String entityId, final LocalDateTime startDate, final LocalDateTime endDate) {
        super(filename, E_032,
                "calendar.txt end_date is before start_date",
                "Then end_date of " + endDate.toString() + " occurs before the start_date of " + startDate.toString() +
                        " for service_id:" + entityId + " in file:" + filename + ".",
                entityId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}