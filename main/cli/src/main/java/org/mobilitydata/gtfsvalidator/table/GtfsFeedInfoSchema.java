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

import java.util.Locale;
import org.mobilitydata.gtfsvalidator.annotation.EndRange;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable(value = "feed_info.txt", singleRow = true)
public interface GtfsFeedInfoSchema extends GtfsEntity {
  @Required
  String feedPublisherName();

  @Required
  @FieldType(FieldTypeEnum.URL)
  String feedPublisherUrl();

  @Required
  Locale feedLang();

  Locale defaultLang();

  @EndRange(field = "feed_end_date", allowEqual = true)
  GtfsDate feedStartDate();

  GtfsDate feedEndDate();

  String feedVersion();

  @FieldType(FieldTypeEnum.EMAIL)
  String feedContactEmail();

  @FieldType(FieldTypeEnum.URL)
  String feedContactUrl();
}
