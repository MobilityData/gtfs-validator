package org.mobilitydata.gtfsvalidator.notice;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;

/**
 * Unused station.
 *
 * <p>A stop has `location_type` STATION (1) but does not appear in any stop's `parent_station`.
 */
@GtfsValidationNotice(severity = INFO)
public class UnusedStationNotice extends ValidationNotice {
  /** The row number of the faulty record. */
  private final int csvRowNumber;

  /** The id of the faulty stop. */
  private final String stopId;

  /** The name of the faulty stop. */
  private final String stopName;

  public UnusedStationNotice(int csvRowNumber, String stopId, String stopName) {
    this.csvRowNumber = csvRowNumber;
    this.stopId = stopId;
    this.stopName = stopName;
  }
}
