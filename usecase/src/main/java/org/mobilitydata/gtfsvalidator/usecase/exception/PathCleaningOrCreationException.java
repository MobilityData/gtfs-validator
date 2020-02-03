package org.mobilitydata.gtfsvalidator.usecase.exception;

import java.io.IOException;

public class PathCleaningOrCreationException extends RuntimeException {

    private final IOException originalException;

    public PathCleaningOrCreationException(IOException e) {
        this.originalException = e;
    }
}
