package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.collect.Multimaps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.OverlappingFrequencyNotice;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequency;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;

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
  @Inject GtfsFrequencyTableContainer table;

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
                  prev.tripId(), SeverityLevel.ERROR));
        }
      }
    }
  }
}
