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

import org.mobilitydata.gtfsvalidator.annotation.EndRange;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.Positive;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("frequencies.txt")
public interface GtfsFrequencySchema extends GtfsEntity {
  @Required
  @ForeignKey(table = "trips.txt", field = "trip_id")
  @PrimaryKey
  @Index
  String tripId();

  @Required
  @PrimaryKey(translationRecordIdType = RECORD_SUB_ID)
  @EndRange(field = "end_time", allowEqual = false)
  GtfsTime startTime();

  @Required
  GtfsTime endTime();

  @Required
  @Positive
  int headwaySecs();

  GtfsFrequencyExactTimes exactTimes();
}
