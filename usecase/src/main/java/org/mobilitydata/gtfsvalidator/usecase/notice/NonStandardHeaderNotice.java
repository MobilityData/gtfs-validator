package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;

public class NonStandardHeaderNotice extends WarningNotice {
    public NonStandardHeaderNotice(String filename, String extra) {
        super(filename, "W001",
                "Non standard header",
                "Unexpected header: " + extra + " in file: " + filename);
    }
}
