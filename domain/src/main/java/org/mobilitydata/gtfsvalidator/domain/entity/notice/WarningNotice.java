package org.mobilitydata.gtfsvalidator.domain.entity.notice;

public class WarningNotice extends Notice {
    public WarningNotice(final String filename,
                         final String noticeId,
                         final String title,
                         final String description) {
        super(filename, noticeId, title, description);
    }
}
