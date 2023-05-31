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

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("stop_times.txt")
@RequiredFile
public interface GtfsStopTimeSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Index
  @RequiredValue
  @ForeignKey(table = "trips.txt", field = "trip_id")
  String tripId();

  @ConditionallyRequired
  @EndRange(field = "departure_time", allowEqual = true)
  GtfsTime arrivalTime();

  @ConditionallyRequired
  GtfsTime departureTime();

  @FieldType(FieldTypeEnum.ID)
  @Index
  @RequiredValue
  @ForeignKey(table = "stops.txt", field = "stop_id")
  String stopId();

  @PrimaryKey(isSequenceUsedForSorting = true, translationRecordIdType = RECORD_SUB_ID)
  @RequiredValue
  @NonNegative
  int stopSequence();

  @CachedField
  String stopHeadsign();

  GtfsPickupDropOff pickupType();

  GtfsPickupDropOff dropOffType();

  @DefaultValue("1")
  GtfsContinuousPickupDropOff continuousPickup();

  @DefaultValue("1")
  GtfsContinuousPickupDropOff continuousDropOff();

  @NonNegative
  double shapeDistTraveled();

  @DefaultValue("1")
  GtfsStopTimeTimepoint timepoint();
}
