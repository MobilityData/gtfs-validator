package org.mobilitydata.gtfsvalidator.notice;

import com.google.common.collect.ImmutableMap;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Two frequency entries referring to the same trip may not have an overlapping time range.
 *
 * <p>Two entries X and Y are considered to directly overlap if <i>X.start_time &lt;= Y.start_time</i>
 * and <i>Y.start_time &lt; X.end_time</i>.
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
            "tripId", tripId));
  }

  @Override
  public String getCode() {
    return "overlapping_frequency";
  }
}
