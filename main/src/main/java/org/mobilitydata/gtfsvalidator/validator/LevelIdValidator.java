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
import org.mobilitydata.gtfsvalidator.table.GtfsLevelTableContainer;
import org.mobilitydata.gtfsvalidator.table.GtfsStopTableContainer;

@GtfsValidator
public class LevelIdValidator extends FileValidator {

  private final GtfsStopTableContainer stops;
  private final GtfsLevelTableContainer levels;

  @Inject
  LevelIdValidator(GtfsStopTableContainer stops, GtfsLevelTableContainer levels) {
    this.stops = stops;
    this.levels = levels;
  }

  @Override
  public void validate(NoticeContainer noticeContainer) {}
}
