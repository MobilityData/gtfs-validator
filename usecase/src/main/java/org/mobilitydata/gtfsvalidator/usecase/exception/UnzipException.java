package org.mobilitydata.gtfsvalidator.usecase.exception;

import java.io.IOException;

public class UnzipException extends RuntimeException {

    private final IOException originalException;

    public UnzipException(IOException e) {
        this.originalException = e;
    }
}
