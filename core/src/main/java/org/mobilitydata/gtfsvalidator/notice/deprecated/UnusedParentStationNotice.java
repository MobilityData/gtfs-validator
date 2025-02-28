package org.mobilitydata.gtfsvalidator.notice.deprecated;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.UnusedStationNotice;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;

/**
 * Unused parent station.
 *
 * <p>A stop has `location_type` STATION (1) but does not appear in any stop's `parent_station`.
 */
@GtfsValidationNotice(
    severity = INFO,
    deprecated = true,
    deprecationVersion = "7.0.0",
    deprecationReason = "Renamed to `unused_station`",
    replacementNotice = UnusedStationNotice.class)
class UnusedParentStationNotice extends ValidationNotice {
  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The id of the faulty stop. */
  private final String stopId;

  /** The name of the faulty stop. */
  private final String stopName;

  UnusedParentStationNotice(int csvRowNumber, String stopId, String stopName) {
    this.csvRowNumber = csvRowNumber;
    this.stopId = stopId;
    this.stopName = stopName;
  }
}
