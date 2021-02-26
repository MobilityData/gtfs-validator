package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;

/**
 * Describes an InterruptedException during multithreaded validation.
 *
 * <p>{@link InterruptedException} is thrown when a thread is waiting, sleeping, or otherwise
 * occupied, and the thread is interrupted, either before or during the activity.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class ThreadInterruptedError extends SystemError {
  public ThreadInterruptedError(String message) {
    super(ImmutableMap.of("message", message));
  }

  @Override
  public String getCode() {
    return "thread_interrupted";
  }
}
