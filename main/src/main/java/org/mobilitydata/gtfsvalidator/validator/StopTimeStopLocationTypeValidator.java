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
import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;

/**
 * Validates that each {@code stop_times.stop_id} refers to a stop/platform, i.e. its
 * stops.location_type value must be 0 or empty.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link WrongStopTimeStopLocationTypeNotice} - a single departure_time or arrival_time is
 *       defined for a row (both or none are expected)
 * </ul>
 */
@GtfsValidator
public class StopTimeStopLocationTypeValidator extends FileValidator {

  private final GtfsStopTimeTableContainer stopTimeTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  StopTimeStopLocationTypeValidator(
      GtfsStopTimeTableContainer stopTimeTable, GtfsStopTableContainer stopTable) {
    this.stopTimeTable = stopTimeTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (List<GtfsStopTime> stopTimeList : Multimaps.asMap(stopTimeTable.byTripIdMap()).values()) {
      for (GtfsStopTime stopTime : stopTimeList) {
        Optional<GtfsStop> stop = Optional.ofNullable(stopTable.byStopId(stopTime.stopId()));
        stop.ifPresent(
            gtfsStop -> {
              if (!gtfsStop.locationType().equals(GtfsLocationType.STOP)) {
                noticeContainer.addValidationNotice(
                    new WrongStopTimeStopLocationTypeNotice(
                        stopTime.csvRowNumber(),
                        stopTime.tripId(),
                        stopTime.stopSequence(),
                        stopTime.stopId(),
                        gtfsStop.locationType().name()));
              }
            });
      }
    }
  }

  /**
   * A {@code stop_times.stop_id} refers to a {@code GtfsStop} with {@code stops.location_type !=
   * stop/platform} i.e. their stops.location_type value must be is different than 0 or empty.
   *
   * <p>Severity: {@code SeverityLevel.WARNING} - To be upgraded to {@code SeverityLevel.ERROR}.
   */
  static class WrongStopTimeStopLocationTypeNotice extends ValidationNotice {
    private long csvRowNumber;
    private String tripId;
    private int stopSequence;
    private String stopId;
    private String locationType;

    WrongStopTimeStopLocationTypeNotice(
        long csvRowNumber, String tripId, int stopSequence, String stopId, String locationType) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.tripId = tripId;
      this.stopSequence = stopSequence;
      this.stopId = stopId;
      this.locationType = locationType;
    }
  }
}
