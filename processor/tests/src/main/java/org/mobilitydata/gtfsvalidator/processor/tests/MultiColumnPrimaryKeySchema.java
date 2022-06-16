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
package org.mobilitydata.gtfsvalidator.processor.tests;

import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;

@GtfsTable("multi_column_primary_key.txt")
public interface MultiColumnPrimaryKeySchema {
  @PrimaryKey
  @Index
  String idA();

  @PrimaryKey
  String idB();

  @PrimaryKey
  String idC();

  String fruit();
}
