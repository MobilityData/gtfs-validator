package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.Collection;
import java.util.Map;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsValidator
public class OverlappingPickupDropOffZoneValidator extends FileValidator {

  private final GtfsStopTimeTableContainer stopTimeTableContainer;
  private final GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer;

  @Inject
  OverlappingPickupDropOffZoneValidator(
      GtfsStopTimeTableContainer table, GtfsGeoJsonFeaturesContainer geoJsonFeaturesContainer) {
    this.stopTimeTableContainer = table;
    this.geoJsonFeaturesContainer = geoJsonFeaturesContainer;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (stopTimeTableContainer.isMissingFile() || geoJsonFeaturesContainer.isMissingFile()) {
      return;
    }
    // For all entities with the same trip id
    for (Map.Entry<String, Collection<GtfsStopTime>> entry :
        stopTimeTableContainer.byTripIdMap().asMap().entrySet()) {
      Collection<GtfsStopTime> stopTimesForTrip = entry.getValue();
      // Checking entities two by two
      for (GtfsStopTime stopTime1 : stopTimesForTrip) {
        for (GtfsStopTime stopTime2 : stopTimesForTrip) {
          // If the two entities are the same, skip
          if (stopTime1.equals(stopTime2)) {
            continue;
          }
          // If the two entities have overlapping pickup/drop-off windows
          if (!(stopTime1.hasEndPickupDropOffWindow()
              && stopTime1.hasStartPickupDropOffWindow()
              && stopTime2.hasEndPickupDropOffWindow()
              && stopTime2.hasStartPickupDropOffWindow()
              && stopTime1.hasLocationId()
              && stopTime2.hasLocationId())) {
            continue;
          }
          if (stopTime1.startPickupDropOffWindow().isAfter(stopTime2.endPickupDropOffWindow())
              || stopTime1.endPickupDropOffWindow().isBefore(stopTime2.startPickupDropOffWindow())
              || stopTime1.endPickupDropOffWindow().equals(stopTime2.startPickupDropOffWindow())
              || stopTime1.startPickupDropOffWindow().equals(stopTime2.endPickupDropOffWindow())) {
            continue;
          }
          // If the two entities have overlapping pickup/drop-off zones
          GtfsGeoJsonFeature stop1GeoJsonFeature =
              geoJsonFeaturesContainer.byLocationId(stopTime1.locationId());
          GtfsGeoJsonFeature stop2GeoJsonFeature =
              geoJsonFeaturesContainer.byLocationId(stopTime2.locationId());
          if (stop1GeoJsonFeature == null || stop2GeoJsonFeature == null) {
            continue;
          }
          if (stop1GeoJsonFeature.geometryOverlaps(stop2GeoJsonFeature)) {
            noticeContainer.addValidationNotice(
                new OverlappingZoneAndPickupDropOffWindowNotice(
                    stopTime1.locationId(),
                    stopTime1.startPickupDropOffWindow(),
                    stopTime1.endPickupDropOffWindow(),
                    stopTime2.locationId(),
                    stopTime2.startPickupDropOffWindow(),
                    stopTime2.endPickupDropOffWindow()));
          }
        }
      }
    }
  }

  /**
   * Two entities have overlapping pickup/drop-off windows and zones.
   *
   * <p>Two entities in `stop_times.txt` with the same `trip_id` have overlapping pickup/drop-off
   * windows and have overlapping zones in `locations.geojson`.
   */
  @GtfsValidationNotice(
      severity = ERROR,
      files = @GtfsValidationNotice.FileRefs({GtfsGeoJsonFeature.class, GtfsStopTime.class}))
  static class OverlappingZoneAndPickupDropOffWindowNotice extends ValidationNotice {

    /** The `location_id` of the first entity. */
    private final String locationIdA;

    /** The `start_pickup_drop_off_window` of the first entity in `stop_times.txt`. */
    private final GtfsTime startPickupDropOffWindowA;

    /** The `end_pickup_drop_off_window` of the first entity in `stop_times.txt`. */
    private final GtfsTime endPickupDropOffWindowA;

    /** The `location_id` of the second entity. */
    private final String locationIdB;

    /** The `start_pickup_drop_off_window` of the second entity in `stop_times.txt`. */
    private final GtfsTime startPickupDropOffWindowB;

    /** The `end_pickup_drop_off_window` of the second entity in `stop_times.txt`. */
    private final GtfsTime endPickupDropOffWindowB;

    OverlappingZoneAndPickupDropOffWindowNotice(
        String locationIdA,
        GtfsTime startPickupDropOffWindowA,
        GtfsTime endPickupDropOffWindowA,
        String locationIdB,
        GtfsTime startPickupDropOffWindowB,
        GtfsTime endPickupDropOffWindowB) {
      this.locationIdA = locationIdA;
      this.startPickupDropOffWindowA = startPickupDropOffWindowA;
      this.endPickupDropOffWindowA = endPickupDropOffWindowA;
      this.locationIdB = locationIdB;
      this.startPickupDropOffWindowB = startPickupDropOffWindowB;
      this.endPickupDropOffWindowB = endPickupDropOffWindowB;
    }
  }
}
