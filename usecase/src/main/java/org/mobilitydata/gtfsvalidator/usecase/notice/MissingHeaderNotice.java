package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

public class MissingHeaderNotice extends ErrorNotice {
    public MissingHeaderNotice(final String filename, final String missingHeaderName) {
        super(filename, "E001",
                "Missing required header",
                "File " + filename + " is missing required header: " + missingHeaderName);
    }
}
