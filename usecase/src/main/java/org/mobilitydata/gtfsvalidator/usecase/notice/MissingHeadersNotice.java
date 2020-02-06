package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

import java.util.Collection;

public class MissingHeadersNotice extends ErrorNotice {
    public MissingHeadersNotice(String filename, Collection<String> expected, Collection<String> actual) {
        super(filename, "E001",
                "Invalid headers",
                "expected: " + expected + "  actual: " + actual);
    }
}
