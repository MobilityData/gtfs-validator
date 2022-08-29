package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;
import java.util.List;

/**
 * A report object used for JSON serialization noting the set of corrupted sources in the comparison
 * set.
 */
@AutoValue
public abstract class CorruptedSources {
  /** The total number of dataset sources in the comparison set. */
  public abstract int sourceIdCount();

  /**
   * The total number of corrupted dataset sources in the comparison set.
   *
   * @return
   */
  public abstract int corruptedSourcesCount();

  /** The dataset ids of the corrupted sources. */
  public abstract List<String> corruptedSources();

  /** A percentage in the range [0,100]. */
  public abstract float percentCorruptedSourcesThreshold();

  /** Returns true if the percentage of corrupted sources is above the threshold. */
  public abstract boolean aboveThreshold();

  public static Builder builder() {
    return new AutoValue_CorruptedSources.Builder();
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder setSourceIdCount(int sourceIdCount);

    public abstract Builder setCorruptedSourcesCount(int corruptedSourcesCount);

    public abstract Builder setCorruptedSources(List<String> corruptedSources);

    public abstract Builder setPercentCorruptedSourcesThreshold(
        float percentCorruptedSourcesThreshold);

    public abstract Builder setAboveThreshold(boolean aboveThreshold);

    public abstract CorruptedSources build();
  }
}
