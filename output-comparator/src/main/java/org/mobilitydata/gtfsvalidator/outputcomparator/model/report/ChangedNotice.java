package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Tracks where the number of validation errors of a particular type differs between validation
 * reports for datasets in the comparison set.
 */
public class ChangedNotice {
  private final String noticeCode;
  private int affectedSourcesCount;
  private final List<AffectedSource> affectedSources = new ArrayList<>();

  public ChangedNotice(String noticeCode) {
    this.noticeCode = noticeCode;
  }

  public String noticeCode() {
    return this.noticeCode;
  }

  public ChangedNotice addAffectedSource(AffectedSource affectedSource) {
    this.affectedSources.add(affectedSource);
    this.affectedSourcesCount = this.affectedSources.size();
    return this;
  }

  public void sortAffectedSources() {
    Collections.sort(affectedSources, Comparator.comparing(AffectedSource::sourceId));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ChangedNotice)) {
      return false;
    }
    ChangedNotice rhs = (ChangedNotice) o;

    return Objects.equals(noticeCode, rhs.noticeCode)
        && Objects.equals(this.affectedSources, rhs.affectedSources);
  }

  @Override
  public String toString() {
    return noticeCode + " " + affectedSources;
  }

  public List<AffectedSource> getAffectedSources() {
    return affectedSources;
  }
}
