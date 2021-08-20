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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import javax.inject.Inject;
import org.mobilitydata.gtfsvalidator.annotation.GtfsValidator;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.notice.SeverityLevel;
import org.mobilitydata.gtfsvalidator.notice.ValidationNotice;
import org.mobilitydata.gtfsvalidator.table.GtfsLevelTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsPathway;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayMode;
import org.mobilitydata.gtfsvalidator.table.GtfsPathwayTableContainer;

/**
 * Checks that levels.txt is provided if at least a row from pathways.txt has {@code
 * pathway.pathway_mode=5}. Only one notice is generated here assuming that the content of {@code
 * pathways.txt} is correct.
 *
 * <p>Generated notice: {@link MissingLevelNotice}.
 */
@GtfsValidator
public class LevelPresenceValidator extends FileValidator {

  private final GtfsLevelTableContainer levels;
  private final GtfsPathwayTableContainer pathways;

  @Inject
  LevelPresenceValidator(GtfsLevelTableContainer levels, GtfsPathwayTableContainer pathways) {
    this.levels = levels;
    this.pathways = pathways;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {
    if (levels.entityCount() != 0) {
      return;
    }
    ListMultimap<GtfsPathwayMode, GtfsPathway> byPathwayModeMap = ArrayListMultimap.create();
    for (GtfsPathway pathway : pathways.getEntities()) {
      byPathwayModeMap.put(pathway.pathwayMode(), pathway);
    }
    if (byPathwayModeMap.get(GtfsPathwayMode.ELEVATOR) == null) {
      return;
    }
    GtfsPathway pathway = byPathwayModeMap.get(GtfsPathwayMode.ELEVATOR).get(0);
    noticeContainer.addValidationNotice(
        new MissingLevelNotice(pathway.csvRowNumber(), pathway.pathwayId()));
  }

  /**
   * A row from pathways.txt has {@code pathways.pathway_mode=5} but levels.txt is empty or not
   * provided.
   *
   * <p>Severity: {@code SeverityLevel.WARNING}. To be upgraded to {@code SeverityLevel.ERROR}.
   */
  static class MissingLevelNotice extends ValidationNotice {

    private final long csvRowNumber;
    private final String pathwayId;

    MissingLevelNotice(long csvRowNumber, String pathwayId) {
      super(SeverityLevel.WARNING);
      this.csvRowNumber = csvRowNumber;
      this.pathwayId = pathwayId;
    }
  }
}
