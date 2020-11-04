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
                String.format("The `end_date`: `%s` occurs before `start_date`: `%s` for `service_id`: `%s` " +
                                "in file `%s`",
                        endDate,
                        startDate,
                        entityId,
                        filename),
                entityId);
    }

    @Override
    public void export(final NoticeExporter exporter) throws IOException {
        exporter.export(this);
    }
}
