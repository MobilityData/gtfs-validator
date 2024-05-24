package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;

/**
 * A dataset source that had a different number of validation errors of a particular type in the
 * comparison set.
 */
@AutoValue
public abstract class ValidationPerformance {
  public abstract String sourceId();

  public abstract Double referenceValidationTimeSeconds();

  public abstract Double latestValidationTimeSeconds();

  public abstract Double differenceSeconds();

  public static ValidationPerformance create(
      String sourceId,
      Double referenceValidationTimeSeconds,
      Double latestValidationTimeSeconds,
      Double differenceSeconds) {
    return new AutoValue_ValidationPerformance(
        sourceId, referenceValidationTimeSeconds, latestValidationTimeSeconds, differenceSeconds);
  }
}
