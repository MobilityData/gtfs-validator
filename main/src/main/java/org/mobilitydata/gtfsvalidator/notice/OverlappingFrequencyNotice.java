package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Two frequency entries referring to the same trip may not have an overlapping time range.
 *
 * <p>Two entries X and Y are considered to directly overlap if <i>X.start_time <= Y.start_time</i>
 * and <i>Y.start_time < X.end_time</i>.
 *
 * <p>Severity: {@code SeverityLevel.WARNING}
 */
public class OverlappingFrequencyNotice extends ValidationNotice {
  public OverlappingFrequencyNotice(
      long prevCsvRowNumber,
      GtfsTime prevEndTime,
      long currCsvRowNumber,
      GtfsTime currStartTime,
      String tripId) {
    super(
        ImmutableMap.of(
            "prevCsvRowNumber", prevCsvRowNumber,
            "prevEndTime", prevEndTime.toHHMMSS(),
            "currCsvRowNumber", currCsvRowNumber,
            "currStartTime", currStartTime.toHHMMSS(),
            "tripId", tripId),
        SeverityLevel.WARNING);
  }

  @Override
  public String getCode() {
    return "overlapping_frequency";
  }
}
