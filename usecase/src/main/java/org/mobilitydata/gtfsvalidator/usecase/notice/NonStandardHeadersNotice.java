package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.WarningNotice;

import java.util.Collection;

public class NonStandardHeadersNotice extends WarningNotice {
    public NonStandardHeadersNotice(String filename, Collection<String> extra) {
        super(filename, "W001",
                "Non standard headers",
                "extra: " + extra);
    }
}
