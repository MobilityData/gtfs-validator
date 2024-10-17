/*
 * Copyright 2021 Google LLC
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
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;

/**
 * Checks that stops and only stops have stop times.
 *
 * <ul>
 *   <li>every stop (or platform) should have stop times;
 *   <li>every non-stop location (station, entrance etc) may not have stop times;
 *   <li>if a stop is part of a location group referenced in stop_times.txt, it should not trigger a
 *       warning.
 * </ul>
 */
@GtfsValidator
public class LocationHasStopTimesValidator extends FileValidator {

  private final GtfsStopTableContainer stopTable;

  private final GtfsStopTimeTableContainer stopTimeTable;

  private final GtfsLocationGroupStopsTableContainer locationGroupStopTable;

  @Inject
  LocationHasStopTimesValidator(
      GtfsStopTableContainer stopTable,
      GtfsStopTimeTableContainer stopTimeTable,
      GtfsLocationGroupStopsTableContainer locationGroupStopTable) {
    this.stopTable = stopTable;
    this.stopTimeTable = stopTimeTable;
    this.locationGroupStopTable = locationGroupStopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Set<String> stopIdsInStopTimesandLocationGroupStops = new HashSet<>();
    Set<String> locationGroupIdsInStopTimes = new HashSet<>();

    for (GtfsStopTime stopTime : stopTimeTable.getEntities()) {
      if (!stopTime.stopId().isEmpty()) {
        stopIdsInStopTimesandLocationGroupStops.add(stopTime.stopId());
      }
      if (!stopTime.locationGroupId().isEmpty()) {
        locationGroupIdsInStopTimes.add(stopTime.locationGroupId());
      }
    }

    for (String locationGroupId : locationGroupIdsInStopTimes) {
      List<GtfsLocationGroupStops> locationGroupStops =
          locationGroupStopTable.byLocationGroupId(locationGroupId);
      for (var locationGroupStop : locationGroupStops) {
        stopIdsInStopTimesandLocationGroupStops.add(locationGroupStop.stopId());
      }
    }

    for (GtfsStop stop : stopTable.getEntities()) {
      List<GtfsStopTime> stopTimes = stopTimeTable.byStopId(stop.stopId());
      if (stop.locationType().equals(GtfsLocationType.STOP)) {
        if (stopTimes.isEmpty()
            && !stopIdsInStopTimesandLocationGroupStops.contains(stop.stopId())) {
          noticeContainer.addValidationNotice(new StopWithoutStopTimeNotice(stop));
        }
      } else if (!stopTimes.isEmpty()) {
        noticeContainer.addValidationNotice(
            new LocationWithUnexpectedStopTimeNotice(stop, stopTimes.get(0)));
      }
    }
  }

  /**
   * A stop in `stops.txt` is not referenced by any `stop_times.stop_id`.
   *
   * <p>Such stops are not used by any trip and normally do not provide user value. This notice may
   * indicate a typo in `stop_times.txt`.
   */
  @GtfsValidationNotice(
      severity = WARNING,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsStopSchema.class}))
  static class StopWithoutStopTimeNotice extends ValidationNotice {

    /** The row number of the faulty record. */
    private final int csvRowNumber;

    /** The id of the faulty stop. */
    private final String stopId;

    /** The name of the faulty stop. */
    private final String stopName;

    StopWithoutStopTimeNotice(GtfsStop stop) {
      this.csvRowNumber = stop.csvRowNumber();
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
    }
  }

  /**
   * A location in `stops.txt` that is not a stop is referenced by some `stop_times.stop_id`.
   *
   * <p>Referenced locations (using `stop_times.stop_id`) must be stops/platforms, i.e. their
   * `stops.location_type` value must be 0 or empty.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @FileRefs({GtfsStopTimeSchema.class, GtfsStopSchema.class}))
  static class LocationWithUnexpectedStopTimeNotice extends ValidationNotice {

    /** The row number of the faulty record from `stops.txt`. */
    private final int csvRowNumber;

    /** The id of the faulty record from `stops.txt`. */
    private final String stopId;

    /** The `stops.stop_name` of the faulty record. */
    private final String stopName;

    /** The row number of the faulty record from `stop_times.txt`. */
    private final long stopTimeCsvRowNumber;

    LocationWithUnexpectedStopTimeNotice(GtfsStop location, GtfsStopTime stopTime) {
      this.csvRowNumber = location.csvRowNumber();
      this.stopId = location.stopId();
      this.stopName = location.stopName();
      this.stopTimeCsvRowNumber = stopTime.csvRowNumber();
    }
  }
}
