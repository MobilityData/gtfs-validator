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
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

/**
 * Validates the pickup and drop off windows in stop_times.txt.
 *
 * <p>Generated notice: {@link ForbiddenArrivalOrDepartureTimeNotice}, {@link
 * MissingPickupOrDropOffWindowNotice}, {@link InvalidPickupDropOffWindowNotice}
 */
@GtfsValidator
public class PickupDropOffWindowValidator extends SingleEntityValidator<GtfsStopTime> {

  @Override
  public void validate(GtfsStopTime stopTime, NoticeContainer noticeContainer) {
    if (stopTime.hasArrivalTime() || stopTime.hasDepartureTime()) {
      // forbidden_arrival_or_departure_time
      noticeContainer.addValidationNotice(
          new ForbiddenArrivalOrDepartureTimeNotice(
              stopTime.csvRowNumber(),
              stopTime.arrivalTime(),
              stopTime.departureTime(),
              stopTime.startPickupDropOffWindow(),
              stopTime.endPickupDropOffWindow()));
    }
    if (!stopTime.hasStartPickupDropOffWindow() || !stopTime.hasEndPickupDropOffWindow()) {
      noticeContainer.addValidationNotice(
          new MissingPickupOrDropOffWindowNotice(
              stopTime.csvRowNumber(),
              stopTime.startPickupDropOffWindow(),
              stopTime.endPickupDropOffWindow()));
    }
    if (stopTime.hasStartPickupDropOffWindow() && stopTime.hasEndPickupDropOffWindow()) {
      if (stopTime.startPickupDropOffWindow().isAfter(stopTime.endPickupDropOffWindow())
          || stopTime.startPickupDropOffWindow().equals(stopTime.endPickupDropOffWindow())) {
        noticeContainer.addValidationNotice(
            new InvalidPickupDropOffWindowNotice(
                stopTime.csvRowNumber(),
                stopTime.startPickupDropOffWindow(),
                stopTime.endPickupDropOffWindow()));
      }
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
   * Arrival and departure times are forbidden in stop_times.txt when pickup and drop off windows
   * are provided.
   */
  @GtfsValidationNotice(severity = ERROR)
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

  /** Start or end pickup drop off window is missing in stop_times.txt. */
  @GtfsValidationNotice(severity = ERROR)
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
   * Start or end pickup drop off window is invalid in stop_times.txt.
   *
   * <p>The value of `end_pickup_drop_off_window` must be strictly greater than the value of
   * `start_pickup_drop_off_window`.
   */
  @GtfsValidationNotice(severity = ERROR)
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
