package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.base.Strings;
import java.util.concurrent.ExecutionException;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * ExecutionException during multithreaded validation
 *
 * <p>An
 * [ExecutionException](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutionException.html)
 * occurred during multithreaded validation.
 */
@GtfsValidationNotice(severity = ERROR)
public class ThreadExecutionError extends SystemError {

  /** The name of the exception. */
  private final String exception;

  /** The error message that explains the reason for the exception. */
  private final String message;

  public ThreadExecutionError(ExecutionException exception) {
    this.exception = exception.getCause().getClass().getCanonicalName();
    // Throwable.getMessage() may return null.
    this.message = Strings.nullToEmpty(exception.getCause().getMessage());
  }
}
