package org.mobilitydata.gtfsvalidator.outputcomparator.model.report;

import com.google.auto.value.AutoValue;
import java.util.List;

/** The root of the generated acceptance report, used for JSON serialization. */
@AutoValue
public abstract class AcceptanceReport {

  public abstract List<ChangedNotice> newErrors();

  public abstract List<ChangedNotice> droppedErrors();

  public abstract List<ChangedNotice> newWarnings();

  public abstract List<ChangedNotice> droppedWarnings();

  public abstract CorruptedSources corruptedSources();

  public abstract List<ValidationPerformance> validationPerformance();

  public static AcceptanceReport create(
      List<ChangedNotice> newErrors,
      List<ChangedNotice> droppedErrors,
      List<ChangedNotice> newWarnings,
      List<ChangedNotice> droppedWarnings,
      CorruptedSources corruptedSources,
      List<ValidationPerformance> validationPerformance) {
    return new AutoValue_AcceptanceReport(
        newErrors,
        droppedErrors,
        newWarnings,
        droppedWarnings,
        corruptedSources,
        validationPerformance);
  }
}
