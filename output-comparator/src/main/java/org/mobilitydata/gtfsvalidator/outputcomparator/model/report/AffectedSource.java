package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;

/**
 * A dataset source that had a different number of validation errors of a particular type in the
 * comparison set.
 */
@AutoValue
public abstract class AffectedSource {

  public abstract String sourceId();

  public abstract String sourceUrl();

  /**
   * The number of validation errors of the parent's {@link ChangedNotice} notice type for this
   * dataset source.
   */
  public abstract int noticeCount();

  public static AffectedSource create(String sourceId, String sourceUrl, int noticeCount) {
    return new AutoValue_AffectedSource(sourceId, sourceUrl, noticeCount);
  }
}
