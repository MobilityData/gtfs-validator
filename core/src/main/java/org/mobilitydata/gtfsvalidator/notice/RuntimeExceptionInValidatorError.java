package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.base.Strings;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Describes a runtime exception during validation. This normally indicates a bug in validator code,
 * e.g., in a custom validator class.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
@GtfsValidationNotice(severity = ERROR)
public class RuntimeExceptionInValidatorError extends SystemError {

  private final String validator;

  private final String exception;

  private final String message;

  public RuntimeExceptionInValidatorError(String validatorClassName, RuntimeException exception) {
    this.validator = validatorClassName;
    this.exception = exception.getClass().getCanonicalName();
    // Throwable.getMessage() may return null, so we need to support it gracefully.
    this.message = Strings.nullToEmpty(exception.getMessage());
  }
}
