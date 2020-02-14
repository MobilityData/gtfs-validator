package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

public class CannotParseIntegerNotice extends ErrorNotice {
    public CannotParseIntegerNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, "E005",
                "Invalid integer value",
                "Value: '" + rawValue + "' of field: " + fieldName + " with type integer can't be parsed in file: " + filename + " at row: " + lineNumber);
    }
}
