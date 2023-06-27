package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.auto.value.AutoValue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencySchema;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframe;
import org.mobilitydata.gtfsvalidator.table.GtfsTimeframeTableContainer;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsValidator
public class TimeframeOverlapValidator extends FileValidator {

  private final GtfsTimeframeTableContainer timeframeContainer;

  @Inject
  public TimeframeOverlapValidator(GtfsTimeframeTableContainer timeframeContainer) {
    this.timeframeContainer = timeframeContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Map<TimeframeKey, List<GtfsTimeframe>> timeframesByKey =
        timeframeContainer.getEntities().stream()
            .collect(Collectors.groupingBy(TimeframeKey::create, Collectors.toList()));
    for (Map.Entry<TimeframeKey, List<GtfsTimeframe>> entry : timeframesByKey.entrySet()) {
      List<GtfsTimeframe> timeframes = new ArrayList<>(entry.getValue());
      Collections.sort(
          timeframes,
          Comparator.comparing(GtfsTimeframe::startTime).thenComparing(GtfsTimeframe::endTime));
      for (int i = 1; i < timeframes.size(); ++i) {
        GtfsTimeframe prev = timeframes.get(i - 1);
        GtfsTimeframe curr = timeframes.get(i);
        if (curr.startTime().isBefore(prev.endTime())) {
          noticeContainer.addValidationNotice(
              new TimeframeOverlapNoice(
                  prev.csvRowNumber(),
                  prev.endTime(),
                  curr.csvRowNumber(),
                  curr.startTime(),
                  entry.getKey().timeframeGroupId(),
                  entry.getKey().serviceId()));
        }
      }
    }
  }

  @AutoValue
  abstract static class TimeframeKey {
    abstract String timeframeGroupId();

    abstract String serviceId();

    static TimeframeKey create(GtfsTimeframe timeframe) {
      return new AutoValue_TimeframeOverlapValidator_TimeframeKey(
          timeframe.timeframeGroupId(), timeframe.serviceId());
    }
  }

  /**
   * Two entries in `timeframes.txt` with the same `timeframe_group_id` and `service_id` have
   * overlapping time intervals.
   *
   * <p>Timeframes with the same group and service dates must not overlap in time. Two entries X and
   * Y are considered to directly overlap if `X.start_time &lt;= Y.start_time` and `Y.start_time
   * &lt; X.end_time`.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsFrequencySchema.class))
  static class TimeframeOverlapNoice extends ValidationNotice {

    /** The row number of the first timeframe entry. */
    private final long prevCsvRowNumber;

    /** The first timeframe end time. */
    private final GtfsTime prevEndTime;

    /** The row number of the second timeframe entry. */
    private final long currCsvRowNumber;

    /** The start time of the second timeframe entry. */
    private final GtfsTime currStartTime;

    /** The timeframe group id associated with the two entries. */
    private final String timeframeGroupId;

    /** The service id associated with the two entries. */
    private final String serviceId;

    TimeframeOverlapNoice(
        long prevCsvRowNumber,
        GtfsTime prevEndTime,
        long currCsvRowNumber,
        GtfsTime currStartTime,
        String timeframeGroupId,
        String serviceId) {
      this.prevCsvRowNumber = prevCsvRowNumber;
      this.prevEndTime = prevEndTime;
      this.currCsvRowNumber = currCsvRowNumber;
      this.currStartTime = currStartTime;
      this.timeframeGroupId = timeframeGroupId;
      this.serviceId = serviceId;
    }
  }
}
