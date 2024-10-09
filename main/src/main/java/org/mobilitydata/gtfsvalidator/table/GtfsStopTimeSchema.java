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

import org.mobilitydata.gtfsvalidator.annotation.CachedField;
import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.DefaultValue;
import org.mobilitydata.gtfsvalidator.annotation.EndRange;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("stop_times.txt")
@Required
public interface GtfsStopTimeSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  @Index
  @Required
  @ForeignKey(table = "trips.txt", field = "trip_id")
  String tripId();

  @ConditionallyRequired
  @EndRange(field = "departure_time", allowEqual = true)
  GtfsTime arrivalTime();

  @ConditionallyRequired
  GtfsTime departureTime();

  @FieldType(FieldTypeEnum.ID)
  @Index
  @ConditionallyRequired
  @ForeignKey(table = "stops.txt", field = "stop_id")
  String stopId();

  @FieldType(FieldTypeEnum.ID)
  @ConditionallyRequired
  @ForeignKey(table = "location_groups.txt", field = "location_group_id")
  String locationGroupId();

  @FieldType(FieldTypeEnum.ID)
  @ConditionallyRequired
  String locationId();

  @PrimaryKey(isSequenceUsedForSorting = true, translationRecordIdType = RECORD_SUB_ID)
  @Required
  @NonNegative
  int stopSequence();

  @CachedField
  String stopHeadsign();

  GtfsTime startPickupDropOffWindow();

  GtfsTime endPickupDropOffWindow();

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

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "booking_rules.txt", field = "booking_rule_id")
  String pickupBookingRuleId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "booking_rules.txt", field = "booking_rule_id")
  String dropOffBookingRuleId();
}
