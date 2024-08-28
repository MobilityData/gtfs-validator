/*
 * Copyright 2022 Google LLC
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

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("fare_leg_rules.txt")
public interface GtfsFareLegRuleSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @Index
  String legGroupId();

  /**
   * There is actually a foreign key validation on this field, but since it needs to check 2 files
   * to establish the presence of the key a custom validator was written. See {@link
   * org.mobilitydata.gtfsvalidator.annotation.ForeignKey} and {@link
   * org.mobilitydata.gtfsvalidator.validator.GtfsFareLegRuleNetworkIdForeignKeyValidator}.
   */
  @FieldType(FieldTypeEnum.ID)
  String networkId();

  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ForeignKey(table = "areas.txt", field = "area_id")
  String fromAreaId();

  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ForeignKey(table = "areas.txt", field = "area_id")
  String toAreaId();

  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ForeignKey(table = "timeframes.txt", field = "timeframe_group_id")
  String fromTimeframeGroupId();

  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ForeignKey(table = "timeframes.txt", field = "timeframe_group_id")
  String toTimeframeGroupId();

  @FieldType(FieldTypeEnum.ID)
  @Required
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ForeignKey(table = "fare_products.txt", field = "fare_product_id")
  String fareProductId();

  @NonNegative
  int rulePriority();
}
