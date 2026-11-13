/*
 * Copyright 2026 MobilityData IO
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

import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.INFO;
import static org.mobilitydata.gtfsvalidator.notice.SeverityLevel.WARNING;

import java.util.Optional;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidationNotice.FileRefs;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopSchema;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;
import org.mobilitydata.gtfsvalidator.util.S2Earth;

/**
 * Validates that child stops in `stops.txt` are not far from their parent stations.
 *
 * <p>Generated notices:
 *
 * <ul>
 *   <li>{@link ChildStopFarFromParentStationNotice}
 *   <li>{@link ChildStopTooFarFromParentStationNotice}
 * </ul>
 */
@GtfsValidator
public class ParentStationDistanceValidator extends FileValidator {

  static final double FAR_THRESHOLD_METERS = 1_000.0;
  static final double TOO_FAR_THRESHOLD_METERS = 10_000.0;

  private final GtfsStopTableContainer stopTable;

  @Inject
  ParentStationDistanceValidator(GtfsStopTableContainer stopTable) {
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStop stop : stopTable.getEntities()) {
      if (stop.locationType() != GtfsLocationType.STOP
          || !stop.hasParentStation()
          || !stop.hasStopLatLon()) {
        continue;
      }
      Optional<GtfsStop> optionalParentStation = stopTable.byStopId(stop.parentStation());
      if (optionalParentStation.isEmpty()) {
        continue;
      }
      GtfsStop parentStation = optionalParentStation.get();
      if (parentStation.locationType() != GtfsLocationType.STATION
          || !parentStation.hasStopLatLon()) {
        continue;
      }
      double distance = S2Earth.getDistanceMeters(stop.stopLatLon(), parentStation.stopLatLon());
      if (distance > TOO_FAR_THRESHOLD_METERS) {
        noticeContainer.addValidationNotice(
            new ChildStopTooFarFromParentStationNotice(stop, parentStation, distance));
      } else if (distance > FAR_THRESHOLD_METERS) {
        noticeContainer.addValidationNotice(
            new ChildStopFarFromParentStationNotice(stop, parentStation, distance));
      }
    }
  }

  /** A child stop is more than 1km away from the location of its parent station. */
  @GtfsValidationNotice(severity = INFO, files = @FileRefs(GtfsStopSchema.class))
  static class ChildStopFarFromParentStationNotice extends ValidationNotice {

    /** The `stops.stop_id` of the child stop. */
    private final String childStopId;

    /** The `stops.stop_lat` of the child stop. */
    private final double childStopLat;

    /** The `stops.stop_lon` of the child stop. */
    private final double childStopLon;

    /** The `stops.stop_id` of the parent station. */
    private final String parentStationId;

    /** The `stops.stop_lat` of the parent station. */
    private final double parentStationLat;

    /** The `stops.stop_lon` of the parent station. */
    private final double parentStationLon;

    /** The distance in meters between the child stop and its parent station. */
    private final double distance;

    ChildStopFarFromParentStationNotice(
        GtfsStop childStop, GtfsStop parentStation, double distance) {
      this.childStopId = childStop.stopId();
      this.childStopLat = childStop.stopLat();
      this.childStopLon = childStop.stopLon();
      this.parentStationId = parentStation.stopId();
      this.parentStationLat = parentStation.stopLat();
      this.parentStationLon = parentStation.stopLon();
      this.distance = distance;
    }
  }

  /** A child stop is more than 10km away from the location of its parent station. */
  @GtfsValidationNotice(severity = WARNING, files = @FileRefs(GtfsStopSchema.class))
  static class ChildStopTooFarFromParentStationNotice extends ValidationNotice {

    /** The `stops.stop_id` of the child stop. */
    private final String childStopId;

    /** The `stops.stop_lat` of the child stop. */
    private final double childStopLat;

    /** The `stops.stop_lon` of the child stop. */
    private final double childStopLon;

    /** The `stops.stop_id` of the parent station. */
    private final String parentStationId;

    /** The `stops.stop_lat` of the parent station. */
    private final double parentStationLat;

    /** The `stops.stop_lon` of the parent station. */
    private final double parentStationLon;

    /** The distance in meters between the child stop and its parent station. */
    private final double distance;

    ChildStopTooFarFromParentStationNotice(
        GtfsStop childStop, GtfsStop parentStation, double distance) {
      this.childStopId = childStop.stopId();
      this.childStopLat = childStop.stopLat();
      this.childStopLon = childStop.stopLon();
      this.parentStationId = parentStation.stopId();
      this.parentStationLat = parentStation.stopLat();
      this.parentStationLon = parentStation.stopLon();
      this.distance = distance;
    }
  }
}
