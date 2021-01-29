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
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.annotation.Inject;
import org.mobilitydata.gtfsvalidator.notice.DecreasingShapeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.DecreasingStopTimeDistanceNotice;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Validates: stop times of a trip have increasing distance (stops.shape_dist_traveled)
 *
 * <p>Generated notice: {@link DecreasingShapeDistanceNotice}.
 */
@GtfsValidator
public class StopTimeIncreasingDistanceValidator extends FileValidator {
  @Inject GtfsStopTimeTableContainer stopTimeTable;

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      // GtfsStopTime objects are sorted based on @SequenceKey annotation on stop_sequence field.
      for (int i = 1; i < stopTimeList.size(); ++i) {
        GtfsStopTime prev = stopTimeList.get(i - 1);
        GtfsStopTime curr = stopTimeList.get(i);
        if (prev.hasShapeDistTraveled()
            && curr.hasShapeDistTraveled()
            && prev.shapeDistTraveled() > curr.shapeDistTraveled()) {
          noticeContainer.addValidationNotice(
              new DecreasingStopTimeDistanceNotice(
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
}
