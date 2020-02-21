package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class CannotUnzipInputArchiveNotice extends ErrorNotice {
    public CannotUnzipInputArchiveNotice(final String filename) {
        super(filename,
                "E008",
                "Unzipping error",
                "An error occurred while trying to unzip archive: " + filename);
    }
}