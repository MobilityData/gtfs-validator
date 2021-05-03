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

import com.google.common.collect.ImmutableMap;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyExactTimes;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsFrequencyTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeTimepoint;

/**
 * Validates that all frequencies having 'exact_times=0' are not linked to a timepoint {@code
 * GtfsStopTime}.
 *
 * <p>Generated notice: {@link InvalidFrequencyBasedTripNotice}.
 */
@GtfsValidator
public class FrequencyBasedTripValidator extends FileValidator {
  private final GtfsFrequencyTableContainer frequencyTable;
  private final GtfsStopTimeTableContainer stopTimeTable;

  @Inject
  FrequencyBasedTripValidator(
      GtfsFrequencyTableContainer frequencyTable, GtfsStopTimeTableContainer stopTimeTable) {
    this.frequencyTable = frequencyTable;
    this.stopTimeTable = stopTimeTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    frequencyTable
        .byTripIdMap()
        .forEach(
            (tripId, frequency) -> {
              if (!frequency.exactTimes().equals(GtfsFrequencyExactTimes.FREQUENCY_BASED)) {
                return;
              }
              for (GtfsStopTime stopTime : stopTimeTable.byTripId(tripId)) {
                if (stopTime.timepoint().equals(GtfsStopTimeTimepoint.APPROXIMATE)) {
                  return;
                }
                noticeContainer.addValidationNotice(
                    new InvalidFrequencyBasedTripNotice(tripId, frequency.csvRowNumber()));
                return;
              }
            });
  }

  /**
   * A {@code GtfsFrequency} has 'exact_times=0' when the associated {@code GtfsStopTime} is a
   * timepoint.
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class InvalidFrequencyBasedTripNotice extends ValidationNotice {
    InvalidFrequencyBasedTripNotice(String tripId, long csvRowNumber) {
      super(
          ImmutableMap.of(
              "tripId", tripId,
              "filename", GtfsFrequencyTableLoader.FILENAME,
              "csvRowNumber", csvRowNumber),
          SeverityLevel.ERROR);
    }
  }
}
