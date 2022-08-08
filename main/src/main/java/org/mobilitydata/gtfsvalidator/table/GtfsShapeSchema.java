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

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.RECORD_SUB_ID;

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("shapes.txt")
public interface GtfsShapeSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @Required
  @PrimaryKey
  @Index
  String shapeId();

  @FieldType(FieldTypeEnum.LATITUDE)
  @Required
  double shapePtLat();

  @FieldType(FieldTypeEnum.LONGITUDE)
  @Required
  double shapePtLon();

  @Required
  @NonNegative
  @PrimaryKey(isSequenceUsedForSorting = true, translationRecordIdType = RECORD_SUB_ID)
  int shapePtSequence();

  @NonNegative
  double shapeDistTraveled();
}
