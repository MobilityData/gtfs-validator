package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;

/**
 * Describes a runtime exception during validation. This normally indicates a bug in validator code,
 * e.g., in a custom validator class.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class RuntimeExceptionInValidatorError extends SystemError {
  private String validator;
  private String exception;
  private String message;

  public RuntimeExceptionInValidatorError(String validatorClassName, RuntimeException exception) {
    this.validator = validatorClassName;
    this.exception = exception.getClass().getCanonicalName();
    // Throwable.getMessage() may return null, so we need to support it gracefully.
    this.message = Strings.nullToEmpty(exception.getMessage());
  }
}
