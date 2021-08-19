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

import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLocationType;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates:
 *
 * <p>No stop referenced from {@code pathways.txt} has {@code location_type} value 1 (station).
 *
 * <p>Generated notice:
 *
 * <ul>
 *   <li>{@link LocationTypeStationForStopOnPathwayNotice}
 * </ul>
 */
@GtfsValidator
public class PathwayStopsConsistencyValidator extends FileValidator {

  private final GtfsStopTableContainer stops;
  private final GtfsPathwayTableContainer pathways;

  @Inject
  PathwayStopsConsistencyValidator(
      GtfsStopTableContainer stops, GtfsPathwayTableContainer pathways) {
    this.stops = stops;
    this.pathways = pathways;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsPathway pathway : pathways.getEntities()) {
      GtfsStop[] pathwayStops = {
        stops.byStopId(pathway.fromStopId()), stops.byStopId(pathway.toStopId())
      };
      for (GtfsStop pathwayStop : pathwayStops) {
        if (pathwayStop == null) {
          continue;
        }
        if (pathwayStop.locationType() == GtfsLocationType.STATION) {
          noticeContainer.addValidationNotice(
              new LocationTypeStationForStopOnPathwayNotice(
                  pathway.pathwayId(),
                  pathway.csvRowNumber(),
                  pathwayStop.stopId(),
                  pathwayStop.csvRowNumber()));
        }
      }
    }
  }

  /**
   * One of the two stops referenced by a row from {@code pathways.txt} has {@code location_type =
   * 1} (station).
   *
   * <p>Severity: {@code SeverityLevel.WARNING} to be upgraded to {@code SeverityLevel.ERROR}.
   */
  static class LocationTypeStationForStopOnPathwayNotice extends ValidationNotice {

    private final String pathwayId;
    private final long pathwayCsvRowNumber;
    private final String stopId;
    private final long stopCsvRowNumber;

    public LocationTypeStationForStopOnPathwayNotice(
        String pathwayId, long pathwayCsvRowNumber, String stopId, long stopCsvRowNumber) {
      super(SeverityLevel.WARNING);
      this.pathwayId = pathwayId;
      this.pathwayCsvRowNumber = pathwayCsvRowNumber;
      this.stopId = stopId;
      this.stopCsvRowNumber = stopCsvRowNumber;
    }
  }
}
