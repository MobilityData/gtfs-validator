package org.mobilitydata.gtfsvalidator.usecase.exception;

import java.io.IOException;

public class DownloadException extends RuntimeException {

    private final IOException originalException;

    public DownloadException(IOException e) {
        this.originalException = e;
    }
}
