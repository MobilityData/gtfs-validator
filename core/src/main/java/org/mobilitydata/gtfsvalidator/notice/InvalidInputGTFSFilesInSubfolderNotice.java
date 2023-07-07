package org.mobilitydata.gtfsvalidator.notice;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

@GtfsValidationNotice(severity = ERROR)
public class InvalidInputGTFSFilesInSubfolderNotice extends ValidationNotice{
    /** The error message that explains the reason for the exception. */
    private final String message;

    public InvalidInputGTFSFilesInSubfolderNotice(String message) {
        this.message = message;
    }
}
