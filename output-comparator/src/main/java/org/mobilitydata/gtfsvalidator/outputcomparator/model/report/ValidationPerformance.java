package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;

/**
 * Represents the performance of the validation process for a specific source.
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
