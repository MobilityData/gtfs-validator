package org.mobilitydata.gtfsvalidator.validator;

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.ERROR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
      List<GtfsStopTime> stopTimesForTrip = new ArrayList<>(entry.getValue());
      // Checking entities two by two
      // Checking entities two by two
      for (int i = 0; i < stopTimesForTrip.size(); i++) {
        GtfsStopTime stopTime1 = stopTimesForTrip.get(i);
        for (int j = i + 1; j < stopTimesForTrip.size(); j++) {
          GtfsStopTime stopTime2 = stopTimesForTrip.get(j);

          // If the two entities have overlapping pickup/drop-off windows
          if (!(stopTime1.hasEndPickupDropOffWindow()
              && stopTime1.hasStartPickupDropOffWindow()
              && stopTime2.hasEndPickupDropOffWindow()
              && stopTime2.hasStartPickupDropOffWindow()
              && stopTime1.hasLocationId()
              && stopTime2.hasLocationId())) {
            continue;
          }

          if (stopTime1.locationId().equals(stopTime2.locationId())) {
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
                    stopTime1.tripId(),
                    stopTime1.stopSequence(),
                    stopTime1.locationId(),
                    stopTime1.startPickupDropOffWindow(),
                    stopTime1.endPickupDropOffWindow(),
                    stopTime2.stopSequence(),
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
    /** The `trip_id` of the entities. */
    private final String tripId;

    /** The `stop_sequence` of the first entity in `stop_times.txt`. */
    private final Integer stopSequence1;

    /** The `location_id` of the first entity. */
    private final String locationId1;

    /** The `start_pickup_drop_off_window` of the first entity in `stop_times.txt`. */
    private final GtfsTime startPickupDropOffWindow1;

    /** The `end_pickup_drop_off_window` of the first entity in `stop_times.txt`. */
    private final GtfsTime endPickupDropOffWindow1;

    /** The `stop_sequence` of the second entity in `stop_times.txt`. */
    private final Integer stopSequence2;

    /** The `location_id` of the second entity. */
    private final String locationId2;

    /** The `start_pickup_drop_off_window` of the second entity in `stop_times.txt`. */
    private final GtfsTime startPickupDropOffWindow2;

    /** The `end_pickup_drop_off_window` of the second entity in `stop_times.txt`. */
    private final GtfsTime endPickupDropOffWindow2;

    OverlappingZoneAndPickupDropOffWindowNotice(
        String tripId,
        Integer stopSequence1,
        String locationId1,
        GtfsTime startPickupDropOffWindow1,
        GtfsTime endPickupDropOffWindow1,
        Integer stopSequence2,
        String locationId2,
        GtfsTime startPickupDropOffWindow2,
        GtfsTime endPickupDropOffWindow2) {
      this.tripId = tripId;
      this.stopSequence1 = stopSequence1;
      this.locationId1 = locationId1;
      this.startPickupDropOffWindow1 = startPickupDropOffWindow1;
      this.endPickupDropOffWindow1 = endPickupDropOffWindow1;
      this.stopSequence2 = stopSequence2;
      this.locationId2 = locationId2;
      this.startPickupDropOffWindow2 = startPickupDropOffWindow2;
      this.endPickupDropOffWindow2 = endPickupDropOffWindow2;
    }
  }
}
