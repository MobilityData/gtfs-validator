package org.mobilitydata.gtfsvalidator.usecase.notice.base;

import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class ErrorNotice extends Notice {

    public ErrorNotice(final String filename,
                       final String noticeId,
                       final String title,
                       final String description) {
        super(filename, noticeId, title, description);
    }

    @Override
    public Notice visit(ValidationResultRepository resultRepo) {
        return resultRepo.addNotice(this);
    }
}
