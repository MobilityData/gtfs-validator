package org.mobilitydata.gtfsvalidator.usecase.notice.base;

import org.mobilitydata.gtfsvalidator.usecase.port.ValidationResultRepository;

//TODO: use those to track progress (no error in file xxx, took xxms) maybe also have verbose level
public class InfoNotice extends Notice {
    public InfoNotice(final String filename,
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
