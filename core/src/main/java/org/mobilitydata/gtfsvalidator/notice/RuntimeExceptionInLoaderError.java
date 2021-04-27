package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;

/**
 * Describes a runtime exception during loading a table. This normally indicates a bug in validator
 * code.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class RuntimeExceptionInLoaderError extends SystemError {
  public RuntimeExceptionInLoaderError(String filename, RuntimeException exception) {
    super(
        ImmutableMap.of(
            "filename",
            filename,
            "exception",
            exception.getClass().getCanonicalName(),
            "message",
            Strings.nullToEmpty(exception.getMessage())));
  }
}
