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

import java.math.BigDecimal;
import java.util.Currency;
import org.mobilitydata.gtfsvalidator.annotation.CurrencyAmount;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("fare_products.txt")
public interface GtfsFareProductSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @Required
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Index
  String fareProductId();

  String fareProductName();

  @Required
  @NonNegative
  @CurrencyAmount(currencyField = "currency")
  BigDecimal amount();

  @Required
  Currency currency();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "fare_media.txt", field = "fare_media_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String fareMediaId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "rider_categories.txt", field = "rider_category_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String riderCategoryId();
}
