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

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.UNSUPPORTED;

import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.CachedField;
import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("translations.txt")
public interface GtfsTranslationSchema extends GtfsEntity {
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  @CachedField
  String tableName();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  @CachedField
  String fieldName();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @Required
  Locale language();

  @Required
  String translation();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ConditionallyRequired
  @CachedField
  String recordId();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ConditionallyRequired
  @CachedField
  String recordSubId();

  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  @ConditionallyRequired
  @CachedField
  String fieldValue();
}
