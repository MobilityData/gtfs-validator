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

import com.google.common.collect.Multimaps;
import java.util.List;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.validator.ShapeIncreasingDistanceValidator.DecreasingOrEqualShapeDistanceNotice;

/**
 * Validates: stop times of a trip have increasing distance (stops.shape_dist_traveled)
 *
 * <p>Generated notice: {@link DecreasingOrEqualShapeDistanceNotice}.
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
   * When sorted on `stops.stop_sequence` key, stop times should have strictly increasing values for
   * `stops.shape_dist_traveled`
   *
   * <p>"Values used for shape_dist_traveled must increase along with stop_sequence"
   * (http://gtfs.org/reference/static/#stoptimestxt)
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class DecreasingOrEqualStopTimeDistanceNotice extends ValidationNotice {
    private String tripId;
    private long csvRowNumber;
    private double shapeDistTraveled;
    private int stopSequence;
    private long prevCsvRowNumber;
    private double prevStopTimeDistTraveled;
    private int prevStopSequence;

    DecreasingOrEqualStopTimeDistanceNotice(
        String tripId,
        long csvRowNumber,
        double shapeDistTraveled,
        int stopSequence,
        long prevCsvRowNumber,
        double prevStopTimeDistTraveled,
        int prevStopSequence) {
      super(SeverityLevel.ERROR);
      this.tripId = tripId;
      this.csvRowNumber = csvRowNumber;
      this.shapeDistTraveled = shapeDistTraveled;
      this.stopSequence = stopSequence;
      this.prevCsvRowNumber = prevCsvRowNumber;
      this.prevStopTimeDistTraveled = prevStopTimeDistTraveled;
      this.prevStopSequence = prevStopSequence;
    }
  }
}
