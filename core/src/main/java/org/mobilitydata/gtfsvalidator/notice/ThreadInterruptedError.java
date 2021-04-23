package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import javax.annotation.Nullable;

/**
 * Describes an InterruptedException during multithreaded validation.
 *
 * <p>{@link InterruptedException} is thrown when a thread is waiting, sleeping, or otherwise
 * occupied, and the thread is interrupted, either before or during the activity.
 *
 * <p>Severity: {@code SeverityLevel.ERROR}
 */
public class ThreadInterruptedError extends SystemError {
  public ThreadInterruptedError(@Nullable String message) {
    super(ImmutableMap.of("message", Strings.nullToEmpty(message)));
  }
}
