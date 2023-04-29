package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.base.Strings;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Describes a runtime exception during loading a table. This normally indicates a bug in validator
 * code.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
@GtfsValidationNotice(severity = ERROR)
public class RuntimeExceptionInLoaderError extends SystemError {

  private final String filename;

  private final String exception;

  private final String message;

  public RuntimeExceptionInLoaderError(String filename, RuntimeException exception) {
    this.filename = filename;
    this.exception = exception.getClass().getCanonicalName();
    this.message = Strings.nullToEmpty(exception.getMessage());
  }
}
