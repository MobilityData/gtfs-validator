/*
 * Copyright 2021 Google LLC
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
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableLoader;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Checks that pathway endpoints have correct types.
 *
 * <ul>
 *   <li>a pathway may not lead to a station ({@code location_type=1});
 *   <li>if a platform has boarding areas, the pathway must be connected to its boarding areas
 *       instead of the platform itself.
 * </ul>
 */
@GtfsValidator
public class PathwayEndpointTypeValidator extends FileValidator {

  private final GtfsPathwayTableContainer pathwayTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  PathwayEndpointTypeValidator(
      GtfsPathwayTableContainer pathwayTable, GtfsStopTableContainer stopTable) {
    this.pathwayTable = pathwayTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsPathway pathway : pathwayTable.getEntities()) {
      checkEndpoint(
          pathway,
          GtfsPathwayTableLoader.FROM_STOP_ID_FIELD_NAME,
          pathway.fromStopId(),
          noticeContainer);
      checkEndpoint(
          pathway,
          GtfsPathwayTableLoader.TO_STOP_ID_FIELD_NAME,
          pathway.toStopId(),
          noticeContainer);
    }
  }

  private void checkEndpoint(
      GtfsPathway pathway, String fieldName, String stopId, NoticeContainer noticeContainer) {
    GtfsStop stop = stopTable.byStopId(stopId);
    switch (stop.locationType()) {
      case STOP:
        if (!stopTable.byParentStation(stopId).isEmpty()) {
          noticeContainer.addValidationNotice(
              new PathwayToPlatformWithBoardingAreasNotice(pathway, fieldName, stopId));
        }
        return;
      case STATION:
        noticeContainer.addValidationNotice(
            new PathwayWrongEndpointTypeNotice(pathway, fieldName, stopId));
        return;
      default:
        break;
    }
  }

  /** Describes a pathway which endpoint is a station. */
  static class PathwayWrongEndpointTypeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String pathwayId;
    private final String fieldName;
    private final String stopId;

    PathwayWrongEndpointTypeNotice(GtfsPathway pathway, String fieldName, String stopId) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayId = pathway.pathwayId();
      this.fieldName = fieldName;
      this.stopId = stopId;
    }
  }

  /** Describes a pathway which endpoint is a platform that has boarding areas. */
  static class PathwayToPlatformWithBoardingAreasNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String pathwayId;
    private final String fieldName;
    private final String stopId;

    PathwayToPlatformWithBoardingAreasNotice(GtfsPathway pathway, String fieldName, String stopId) {
      super(SeverityLevel.ERROR);
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayId = pathway.pathwayId();
      this.fieldName = fieldName;
      this.stopId = stopId;
    }
  }
}
