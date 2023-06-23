package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.base.Strings;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * RuntimeException while loading GTFS dataset in memory.
 *
 * <p>A
 * [RuntimeException](https://docs.oracle.com/javase/8/docs/api/java/lang/RuntimeException.html)
 * occurred while loading a table. This normally indicates a bug in validator.
 */
@GtfsValidationNotice(severity = ERROR)
public class RuntimeExceptionInLoaderError extends SystemError {

  /** The name of the file that caused the exception. */
  private final String filename;

  /** The name of the exception. */
  private final String exception;

  /** The error message that explains the reason for the exception. */
  private final String message;

  public RuntimeExceptionInLoaderError(String filename, RuntimeException exception) {
    this.filename = filename;
    this.exception = exception.getClass().getCanonicalName();
    this.message = Strings.nullToEmpty(exception.getMessage());
  }
}
