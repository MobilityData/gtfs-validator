package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

public class InvalidRowLengthNotice extends ErrorNotice {
    public InvalidRowLengthNotice(String filename, int rowIndex, int expectedLength, int actualLength) {
        super(filename, "E004",
                "Invalid row length",
                "Invalid length for row with index:" + rowIndex +
                        " -- expected:" + expectedLength + " actual:" + actualLength);
    }
}
