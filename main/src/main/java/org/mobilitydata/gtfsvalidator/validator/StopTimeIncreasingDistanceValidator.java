/*
 * Copyright 2021 MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Validates: stop times of a trip have increasing distance (stops.shape_dist_traveled)
 *
 * <p>Generated notice: {@link DecreasingOrEqualStopTimeDistanceNotice}.
 */
@GtfsValidator
public class StopTimeIncreasingDistanceValidator extends FileValidator {

  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  StopTimeIncreasingDistanceValidator(GtfsStopTimeTableContainer stopTimeTable) {
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      // GtfsStopTime objects are sorted based on @SequenceKey annotation on stop_sequence field.
      for (int i = 1; i < stopTimeList.size(); ++i) {
        GtfsStopTime prev = stopTimeList.get(i - 1);
        GtfsStopTime curr = stopTimeList.get(i);
        if (prev.hasShapeDistTraveled()
            && curr.hasShapeDistTraveled()
            && prev.shapeDistTraveled() >= curr.shapeDistTraveled()) {
          noticeContainer.addValidationNotice(
              new DecreasingOrEqualStopTimeDistanceNotice(
                  curr.tripId(),
                  curr.stopId(),
                  curr.csvRowNumber(),
                  curr.shapeDistTraveled(),
                  curr.stopSequence(),
                  prev.csvRowNumber(),
                  prev.shapeDistTraveled(),
                  prev.stopSequence()));
        }
      }
    }
  }

  /**
   * Decreasing or equal `shape_dist_traveled` in `stop_times.txt`.
   *
   * <p>When sorted by `stop_times.stop_sequence`, two consecutive entries in `stop_times.txt`
   * should have increasing distance, based on the field `shape_dist_traveled`. If the values are
   * equal, this is considered as an error.
   */
  @GtfsValidationNotice(severity = ERROR, files = @FileRefs(GtfsStopTimeSchema.class))
  static class DecreasingOrEqualStopTimeDistanceNotice extends ValidationNotice {

    /** The id of the faulty trip. */
    private final String tripId;

    /** The id of the faulty stop. */
    private final String stopId;

    /** The row number from `stop_times.txt`. */
    private final int csvRowNumber;

    /** Actual distance traveled along the shape from the first shape point to the faulty record. */
    private final double shapeDistTraveled;

    /** The faulty record's `stop_times.stop_sequence`. */
    private final int stopSequence;

    /** The row number from `stop_times.txt` of the previous stop time. */
    private final long prevCsvRowNumber;

    /**
     * Actual distance traveled along the shape from the first shape point to the previous stop
     * time.
     */
    private final double prevStopTimeDistTraveled;

    /** The previous record's `stop_times.stop_sequence`. */
    private final int prevStopSequence;

    DecreasingOrEqualStopTimeDistanceNotice(
        String tripId,
        String stopId,
        int csvRowNumber,
        double shapeDistTraveled,
        int stopSequence,
        long prevCsvRowNumber,
        double prevStopTimeDistTraveled,
        int prevStopSequence) {
      this.tripId = tripId;
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
      this.shapeDistTraveled = shapeDistTraveled;
      this.stopSequence = stopSequence;
      this.prevCsvRowNumber = prevCsvRowNumber;
      this.prevStopTimeDistTraveled = prevStopTimeDistTraveled;
      this.prevStopSequence = prevStopSequence;
    }
  }
}
