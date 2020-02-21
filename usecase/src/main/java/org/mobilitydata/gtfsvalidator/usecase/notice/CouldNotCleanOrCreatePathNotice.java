package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

public class CouldNotCleanOrCreatePathNotice extends ErrorNotice {
    public CouldNotCleanOrCreatePathNotice(final String pathToCleanOrCreate) {
        super("",
                "E009",
                "Path cleaning or creation error",
                "An error occurred while trying clean or create path: " + pathToCleanOrCreate);
    }
}