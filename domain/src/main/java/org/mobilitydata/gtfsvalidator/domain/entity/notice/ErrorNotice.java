package org.mobilitydata.gtfsvalidator.domain.entity.notice;

public class ErrorNotice extends Notice {

    public ErrorNotice(final String filename,
                       final String noticeId,
                       final String title,
                       final String description) {
        super(filename, noticeId, title, description);
    }
}
