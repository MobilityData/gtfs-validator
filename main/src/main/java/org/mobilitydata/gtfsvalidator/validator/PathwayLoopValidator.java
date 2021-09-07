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

import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;

/** Validates that pathway is not a loop, i.e. it does not start and end at the same location. */
@GtfsValidator
public class PathwayLoopValidator extends SingleEntityValidator<GtfsPathway> {

  @Override
  public void validate(GtfsPathway pathway, NoticeContainer noticeContainer) {
    if (pathway.hasFromStopId()
        && pathway.hasToStopId()
        && pathway.fromStopId().equals(pathway.toStopId())) {
      noticeContainer.addValidationNotice(new PathwayLoopNotice(pathway));
    }
  }

  static class PathwayLoopNotice extends ValidationNotice {
    private final long csvRowNumber;
    private final String pathwayId;
    private final String stopId;

    PathwayLoopNotice(GtfsPathway pathway) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = pathway.csvRowNumber();
      this.pathwayId = pathway.pathwayId();
      this.stopId = pathway.fromStopId();
    }
  }
}
