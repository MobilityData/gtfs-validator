package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class CannotConstructDataProviderNotice extends ErrorNotice {
    public CannotConstructDataProviderNotice(String filename) {
        super(filename, "E002",
                "Data provider error",
                "An error occurred while trying to access raw data for file: " + filename);
    }
}
