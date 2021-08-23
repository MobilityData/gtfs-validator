/*
 * Copyright 2020 Google LLC
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

package org.mobilitydata.gtfsvalidator.table;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.NonZero;
import org.mobilitydata.gtfsvalidator.annotation.Positive;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("pathways.txt")
public interface GtfsPathwaySchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Required
  String pathwayId();

  @FieldType(FieldTypeEnum.ID)
  @Required
  @ForeignKey(table = "stops.txt", field = "stop_id")
  String fromStopId();

  @FieldType(FieldTypeEnum.ID)
  @Required
  @ForeignKey(table = "stops.txt", field = "stop_id")
  String toStopId();

  @Required
  GtfsPathwayMode pathwayMode();

  @Required
  int isBidirectional();

  @NonNegative
  double length();

  @Positive
  int traversalTime();

  @NonZero
  int stairCount();

  double maxSlope();

  @Positive
  double minWidth();

  String signpostedAs();

  String reversedSignpostedAs();
}
