package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;
import java.util.concurrent.ExecutionException;

/**
 * Describes an ExecutionException during multithreaded validation.
 *
 * <p>{@link java.util.concurrent.ExecutionException} is thrown when attempting to retrieve the
 * result of a task that aborted by throwing an exception.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class ThreadExecutionError extends SystemError {
  private final String exception;
  private final String message;

  public ThreadExecutionError(ExecutionException exception) {
    this.exception = exception.getCause().getClass().getCanonicalName();
    // Throwable.getMessage() may return null.
    this.message = Strings.nullToEmpty(exception.getCause().getMessage());
  }
}
