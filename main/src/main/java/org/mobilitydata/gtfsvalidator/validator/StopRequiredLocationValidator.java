package org.mobilitydata.gtfsvalidator.validator;

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;

/**
 * Validates {@code stops.stop_lat} and {@code stops.stop_long} exist for a single {@code GtfsStop}
 * of type Stop, Station, Entrance, and Exit.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link StopWithoutLocationNotice}
 * </ul>
 */
@GtfsValidator
public class StopRequiredLocationValidator extends SingleEntityValidator<GtfsStop> {

  @Override
  public void validate(GtfsStop stop, NoticeContainer noticeContainer) {
    if ((stop.locationType() == GtfsLocationType.STOP
            || stop.locationType() == GtfsLocationType.STATION
            || stop.locationType() == GtfsLocationType.ENTRANCE)
        && !stop.hasStopLatLon()) {
      noticeContainer.addValidationNotice(
          new StopWithoutLocationNotice(stop.csvRowNumber(), stop.stopId(), stop.locationType()));
    }
  }

  /**
   * A {@code GtfsStop} is missing {@code stops.stop_lat} and/or {@code stops.stop_long}
   *
   * <p>"Required for locations which are stops (location_type=0), stations (location_type=1) or
   * entrances/exits (location_type=2)." (http://gtfs.org/reference/static#stopstxt)
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class StopWithoutLocationNotice extends ValidationNotice {

    // The row number of the faulty record.
    private final int csvRowNumber;

    // The faulty record's `stops.location_type`.
    private final GtfsLocationType locationType;

    // The faulty record's id.
    private final String stopId;

    StopWithoutLocationNotice(int csvRowNumber, String stopId, GtfsLocationType type) {
      super(SeverityLevel.ERROR);
      this.stopId = stopId;
      this.csvRowNumber = csvRowNumber;
      this.locationType = type;
    }
  }
}
