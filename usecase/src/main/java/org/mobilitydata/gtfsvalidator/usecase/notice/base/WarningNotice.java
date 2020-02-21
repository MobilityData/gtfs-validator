package org.mobilitydata.gtfsvalidator.usecase.notice.base;

import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

public class WarningNotice extends Notice {
    public WarningNotice(final String filename,
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
