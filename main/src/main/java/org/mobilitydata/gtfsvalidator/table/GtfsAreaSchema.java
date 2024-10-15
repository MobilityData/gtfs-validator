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

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable(
    value = "areas.txt",
    // We specify no limit (-1) here to support the experimental 'wkt' column, which encodes
    // an area polygon as a WKT string, which can grow quite large.  While this column has not yet
    // been added to the spec, it is present in many feeds (e.g. those produced by Trillium), which
    // can cause issues for the CSV parser.
    maxCharsPerColumn = -1)
public interface GtfsAreaSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @Required
  @PrimaryKey
  String areaId();

  @NoInvalidCharacters
  String areaName();
}
