package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;
import java.util.List;

/** The root of the generated acceptance report, used for JSON serialization. */
@AutoValue
public abstract class AcceptanceReport {

  public abstract List<ChangedNotice> newErrors();

  public abstract List<ChangedNotice> droppedErrors();

  public abstract CorruptedSources corruptedSources();

  public static AcceptanceReport create(
      List<ChangedNotice> newErrors,
      List<ChangedNotice> droppedErrors,
      CorruptedSources corruptedSources) {
    return new AutoValue_AcceptanceReport(newErrors, droppedErrors, corruptedSources);
  }
}
