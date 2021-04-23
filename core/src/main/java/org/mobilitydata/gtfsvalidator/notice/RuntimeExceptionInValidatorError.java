package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Describes a runtime exception during validation. This normally indicates a bug in validator code,
 * e.g., in a custom validator class.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class RuntimeExceptionInValidatorError extends SystemError {
  public RuntimeExceptionInValidatorError(String validatorClassName, RuntimeException exception) {
    // Throwable.getMessage() may return null, so we need to support it gracefully.
    super(
        ImmutableMap.of(
            "validator",
            validatorClassName,
            "exception",
            exception.getClass().getCanonicalName(),
            "message",
            Strings.nullToEmpty(exception.getMessage())));
  }
}
