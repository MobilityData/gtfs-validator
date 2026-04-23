/*
 * Copyright 2024 MobilityData
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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTime;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTimeSchema;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates pickup and drop-off windows in the `stop_times.txt` file to ensure compliance with GTFS
 * rules.
 *
 * <p>This validator checks for: - Forbidden use of arrival or departure times when pickup or
 * drop-off windows are provided. - Missing start or end pickup/drop-off windows when one of them is
 * present. - Invalid pickup/drop-off windows where the end time is not strictly later than the
 * start time.
 *
 * <p>Generated notices include: - {@link ForbiddenArrivalOrDepartureTimeNotice} - {@link
 * MissingPickupOrDropOffWindowNotice} - {@link InvalidPickupDropOffWindowNotice}
 */
@GtfsValidator
public class PickupDropOffWindowValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    // Skip validation if neither start nor end pickup/drop-off window is present
    if (!stopTime.hasStartPickupDropOffWindow() && !stopTime.hasEndPickupDropOffWindow()) {
      return;
    }

    // Check for forbidden coexistence of arrival/departure times with pickup/drop-off windows
    if (stopTime.hasArrivalTime() || stopTime.hasDepartureTime()) {
      noticeContainer.addValidationNotice(
          new ForbiddenArrivalOrDepartureTimeNotice(
              stopTime.csvRowNumber(),
              stopTime.hasArrivalTime() ? stopTime.arrivalTime() : null,
              stopTime.hasDepartureTime() ? stopTime.departureTime() : null,
              stopTime.hasStartPickupDropOffWindow() ? stopTime.startPickupDropOffWindow() : null,
              stopTime.hasEndPickupDropOffWindow() ? stopTime.endPickupDropOffWindow() : null));
    }

    // Check for missing start or end pickup/drop-off window
    if (!stopTime.hasStartPickupDropOffWindow() || !stopTime.hasEndPickupDropOffWindow()) {
      noticeContainer.addValidationNotice(
          new MissingPickupOrDropOffWindowNotice(
              stopTime.csvRowNumber(),
              stopTime.hasStartPickupDropOffWindow() ? stopTime.startPickupDropOffWindow() : null,
              stopTime.hasEndPickupDropOffWindow() ? stopTime.endPickupDropOffWindow() : null));
      return;
    }

    // Check for invalid pickup/drop-off window (start time must be strictly before end time)
    if (stopTime.startPickupDropOffWindow().isAfter(stopTime.endPickupDropOffWindow())
        || stopTime.startPickupDropOffWindow().equals(stopTime.endPickupDropOffWindow())) {
      noticeContainer.addValidationNotice(
          new InvalidPickupDropOffWindowNotice(
              stopTime.csvRowNumber(),
              stopTime.startPickupDropOffWindow(),
              stopTime.endPickupDropOffWindow()));
    }
  }

  @Override
  public boolean shouldCallValidate(ColumnInspector header) {
    // No point in validating if there is no start_pickup_drop_off_window column
    // and no end_pickup_drop_off_window column
    return header.hasColumn(GtfsStopTime.START_PICKUP_DROP_OFF_WINDOW_FIELD_NAME)
        || header.hasColumn(GtfsStopTime.END_PICKUP_DROP_OFF_WINDOW_FIELD_NAME);
  }

  /**
   * The arrival or departure times are provided alongside pickup or drop-off windows in
   * `stop_times.txt`.
   *
   * <p>This violates GTFS specification, as both cannot coexist for a single stop time record.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs(GtfsStopTimeSchema.class))
  public static class ForbiddenArrivalOrDepartureTimeNotice extends ValidationNotice {

    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The arrival time of the faulty record. */
    private final GtfsTime arrivalTime;

    /** The departure time of the faulty record. */
    private final GtfsTime departureTime;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public ForbiddenArrivalOrDepartureTimeNotice(
        int csvRowNumber,
        GtfsTime arrivalTime,
        GtfsTime departureTime,
        GtfsTime startPickupDropOffWindow,
        GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.arrivalTime = arrivalTime;
      this.departureTime = departureTime;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }

  /**
   * Either the start or end pickup/drop-off window is missing in `stop_times.txt`.
   *
   * <p>GTFS specification requires both the start and end pickup/drop-off windows to be provided
   * together, if used.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs(GtfsStopTimeSchema.class))
  public static class MissingPickupOrDropOffWindowNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public MissingPickupOrDropOffWindowNotice(
        int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }

  /**
   * The pickup/drop-off window in `stop_times.txt` is invalid.
   *
   * <p>The `end_pickup_drop_off_window` must be strictly later than the
   * `start_pickup_drop_off_window`.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs(GtfsStopTimeSchema.class))
  public static class InvalidPickupDropOffWindowNotice extends ValidationNotice {
    /** The row of the faulty record. */
    private final int csvRowNumber;

    /** The start pickup drop off window of the faulty record. */
    private final GtfsTime startPickupDropOffWindow;

    /** The end pickup drop off window of the faulty record. */
    private final GtfsTime endPickupDropOffWindow;

    public InvalidPickupDropOffWindowNotice(
        int csvRowNumber, GtfsTime startPickupDropOffWindow, GtfsTime endPickupDropOffWindow) {
      this.csvRowNumber = csvRowNumber;
      this.startPickupDropOffWindow = startPickupDropOffWindow;
      this.endPickupDropOffWindow = endPickupDropOffWindow;
    }
  }
}
