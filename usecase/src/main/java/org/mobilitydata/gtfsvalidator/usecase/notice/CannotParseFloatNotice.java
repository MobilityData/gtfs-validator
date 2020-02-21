package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;

public class CannotParseFloatNotice extends ErrorNotice {
    public CannotParseFloatNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, "E006",
                "Invalid float value",
                "Value: '" + rawValue + "' of field: " + fieldName + " with type float can't be parsed in file: " + filename + " at row: " + lineNumber);
    }
}
