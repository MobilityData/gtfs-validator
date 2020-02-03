package org.mobilitydata.gtfsvalidator.usecase.exception;

import java.util.List;

public class MissingRequiredFileException extends RuntimeException {
    public final List<String> missingFilenameList;

    public MissingRequiredFileException(List<String> missingFilenameList) {
        this.missingFilenameList = missingFilenameList;
    }
}
