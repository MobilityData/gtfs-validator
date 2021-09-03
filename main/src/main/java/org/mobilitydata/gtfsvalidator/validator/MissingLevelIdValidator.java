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

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Checks that {@code stops.level_id} is provided when pathways.txt uses {@link
 * GtfsPathwayMode#ELEVATOR}. This is an implicit check that levels.txt is provided when pathways
 * uses {@link GtfsPathwayMode#ELEVATOR}. Foreign key validation of {@code pathways.level_id} is
 * performed in {@code GtfsStopLevelIdForeignKeyValidator}
 *
 * <p>Generated notice: {@link MissingLevelIdNotice}.
 */
@GtfsValidator
public class MissingLevelIdValidator extends FileValidator {

  private final GtfsPathwayTableContainer pathways;
  private final GtfsStopTableContainer stops;

  @Inject
  MissingLevelIdValidator(GtfsPathwayTableContainer pathways, GtfsStopTableContainer stops) {
    this.stops = stops;
    this.pathways = pathways;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    Set<String> elevatorEndpoints = new HashSet<>();
    for (GtfsPathway pathway : pathways.getEntities()) {
      if (pathway.pathwayMode().equals(GtfsPathwayMode.ELEVATOR)) {
        elevatorEndpoints.add(pathway.fromStopId());
        elevatorEndpoints.add(pathway.toStopId());
      }
    }
    for (String stopId : elevatorEndpoints) {
      GtfsStop location = stops.byStopId(stopId);
      if (location != null && !location.hasLevelId()) {
        noticeContainer.addValidationNotice(new MissingLevelIdNotice(location));
      }
    }
  }

  /**
   * A row from stops.txt is linked to a row from pathways.txt with {@code pathways.pathway_mode=5}
   * but has no value for {@code stops.level_id}
   *
   * <p>Severity: {@code SeverityLevel.ERROR}
   */
  static class MissingLevelIdNotice extends ValidationNotice {

    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;

    MissingLevelIdNotice(GtfsStop stop) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = stop.csvRowNumber();
      this.stopId = stop.stopId();
      this.stopName = stop.stopName();
    }
  }
}
