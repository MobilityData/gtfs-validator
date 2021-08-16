package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates that <i>frequencies.txt</i> entries referring to the same trip do not overlap.
 *
 * <p>Two entries X and Y are considered to directly overlap if <i>X.start_time &lt;=
 * Y.start_time</i> and <i>Y.start_time &lt; X.end_time</i>.
 *
 * <p>Time complexity: <i>O(n log n)</i> where <i>n</i> is the number of entries in
 * <i>frequencies.txt</i>.
 *
 * <p>Generated notice: {@link OverlappingFrequencyNotice}.
 */
@GtfsValidator
public class OverlappingFrequencyValidator extends FileValidator {
  private final GtfsFrequencyTableContainer table;

  @Inject
  OverlappingFrequencyValidator(GtfsFrequencyTableContainer table) {
    this.table = table;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsFrequency> unorderedList : Multimaps.asMap(table.byTripIdMap()).values()) {
      List<GtfsFrequency> frequencyList = new ArrayList<>(unorderedList);
      Collections.sort(
          frequencyList,
          Comparator.comparing(GtfsFrequency::startTime)
              .thenComparing(GtfsFrequency::endTime)
              .thenComparing(GtfsFrequency::headwaySecs));

      for (int i = 1; i < frequencyList.size(); ++i) {
        GtfsFrequency prev = frequencyList.get(i - 1);
        GtfsFrequency curr = frequencyList.get(i);
        if (curr.startTime().isBefore(prev.endTime())) {
          noticeContainer.addValidationNotice(
              new OverlappingFrequencyNotice(
                  prev.csvRowNumber(),
                  prev.endTime(),
                  curr.csvRowNumber(),
                  curr.startTime(),
                  prev.tripId()));
        }
      }
    }
  }

  /**
   * Two frequency entries referring to the same trip may not have an overlapping time range.
   *
   * <p>Two entries X and Y are considered to directly overlap if <i>X.start_time &lt;=
   * Y.start_time</i> and <i>Y.start_time &lt;X.end_time</i>.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class OverlappingFrequencyNotice extends ValidationNotice {
    private final long prevCsvRowNumber;
    private final GtfsTime prevEndTime;
    private final long currCsvRowNumber;
    private final GtfsTime currStartTime;
    private final String tripId;

    OverlappingFrequencyNotice(
        long prevCsvRowNumber,
        GtfsTime prevEndTime,
        long currCsvRowNumber,
        GtfsTime currStartTime,
        String tripId) {
      super(SeverityLevel.ERROR);
      this.prevCsvRowNumber = prevCsvRowNumber;
      this.prevEndTime = prevEndTime;
      this.currCsvRowNumber = currCsvRowNumber;
      this.currStartTime = currStartTime;
      this.tripId = tripId;
    }
  }
}
