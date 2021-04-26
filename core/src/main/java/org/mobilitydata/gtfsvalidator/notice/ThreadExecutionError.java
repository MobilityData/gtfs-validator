package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
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
  public ThreadExecutionError(ExecutionException exception) {
    super(
        ImmutableMap.of(
            "exception", exception.getCause().getClass().getCanonicalName(),
            "message", Strings.nullToEmpty(exception.getCause().getMessage())));
  }
}
