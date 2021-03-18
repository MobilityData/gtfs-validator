package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;

/**
 * Describes a runtime exception during loading a table. This normally indicates a bug in validator
 * code.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class RuntimeExceptionInLoaderError extends SystemError {
  public RuntimeExceptionInLoaderError(
      String filename, String exceptionClassName, @Nullable String message) {
    // Throwable.getMessage() may return null, so we need to support it gracefully.
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "exception",
            exceptionClassName,
            "message",
            message == null ? "" : message));
  }
}
