package org.mobilitydata.gtfsvalidator.usecase.notice;

import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;

public class CannotParseFloatNotice extends ErrorNotice {
    public CannotParseFloatNotice(String filename, String fieldName, int lineNumber, String rawValue) {
        super(filename, "E006",
                "Invalid float value",
                "Value: '" + rawValue + "' of field: " + fieldName + " with type float can't be parsed in file: " + filename + " at row: " + lineNumber);
    }
}
