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

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Positive;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("fare_transfer_rules.txt")
public interface GtfsFareTransferRuleSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "fare_leg_rules.txt", field = "leg_group_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String fromLegGroupId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "fare_leg_rules.txt", field = "leg_group_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String toLegGroupId();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Positive
  int durationLimit();

  @ConditionallyRequired
  GtfsDurationLimitType durationLimitType();

  @Required
  GtfsFareTransferType fareTransferType();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ConditionallyRequired
  int transferCount();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "fare_products.txt", field = "fare_product_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String fareProductId();
}
