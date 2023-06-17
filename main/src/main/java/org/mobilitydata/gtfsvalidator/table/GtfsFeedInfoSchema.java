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
import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable(value = "feed_info.txt", singleRow = true)
@RecommendedFile
public interface GtfsFeedInfoSchema extends GtfsEntity {
  @RequiredValue
  String feedPublisherName();

  @RequiredValue
  @FieldType(FieldTypeEnum.URL)
  String feedPublisherUrl();

  @RequiredValue
  Locale feedLang();

  Locale defaultLang();

  @RecommendedValue
  @EndRange(field = "feed_end_date", allowEqual = true)
  GtfsDate feedStartDate();

  @RecommendedValue
  GtfsDate feedEndDate();

  @RecommendedValue
  String feedVersion();

  @RecommendedValue
  @FieldType(FieldTypeEnum.EMAIL)
  String feedContactEmail();

  @RecommendedValue
  @FieldType(FieldTypeEnum.URL)
  String feedContactUrl();
}
