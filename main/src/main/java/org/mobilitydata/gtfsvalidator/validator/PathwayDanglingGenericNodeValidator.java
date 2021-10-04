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

import static org.mobilitydata.gtfsvalidator.table.GtfsLocationType.GENERIC_NODE;

import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStop;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

/**
 * Validates that generic node is not dangling, i.e. does not have just one incident location in the
 * pathway graph.
 *
 * <p>Such generic node is useless because there is no benefit in visiting it.
 *
 * <p>Pathway directions are ignored during that validation.
 */
@GtfsValidator
public class PathwayDanglingGenericNodeValidator extends FileValidator {

  private final GtfsPathwayTableContainer pathwayTable;
  private final GtfsStopTableContainer stopTable;

  @Inject
  PathwayDanglingGenericNodeValidator(
      GtfsPathwayTableContainer pathwayTable, GtfsStopTableContainer stopTable) {
    this.pathwayTable = pathwayTable;
    this.stopTable = stopTable;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    for (GtfsStop location : stopTable.getEntities()) {
      if (!location.locationType().equals(GENERIC_NODE)) {
        continue;
      }
      Set<String> incidentIds = new HashSet<>();
      for (GtfsPathway pathway : pathwayTable.byFromStopId(location.stopId())) {
        incidentIds.add(pathway.toStopId());
      }
      for (GtfsPathway pathway : pathwayTable.byToStopId(location.stopId())) {
        incidentIds.add(pathway.fromStopId());
      }
      if (incidentIds.size() == 1) {
        // The generic node is incident to a single location.
        noticeContainer.addValidationNotice(new PathwayDanglingGenericNodeNotice(location));
      }
    }
  }

  /**
   * Describes a dangling generic node, i.e. that has only one incident location in a pathway graph.
   */
  static class PathwayDanglingGenericNodeNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String stopId;
    private final String stopName;
    private final String parentStation;

    PathwayDanglingGenericNodeNotice(GtfsStop genericNode) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = genericNode.csvRowNumber();
      this.stopId = genericNode.stopId();
      this.stopName = genericNode.stopName();
      this.parentStation = genericNode.parentStation();
    }
  }
}
