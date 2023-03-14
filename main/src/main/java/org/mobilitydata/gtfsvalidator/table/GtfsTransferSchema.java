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

import static org.mobilitydata.gtfsvalidator.annotation.TranslationRecordIdType.*;

import org.mobilitydata.gtfsvalidator.annotation.*;

@GtfsTable("transfers.txt")
public interface GtfsTransferSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @Required
  @ForeignKey(table = "stops.txt", field = "stop_id")
  @PrimaryKey(translationRecordIdType = RECORD_ID)
  String fromStopId();

  @FieldType(FieldTypeEnum.ID)
  @Required
  @ForeignKey(table = "stops.txt", field = "stop_id")
  @PrimaryKey(translationRecordIdType = RECORD_SUB_ID)
  String toStopId();

  @RequiredColumn
  GtfsTransferType transferType();

  @NonNegative
  int minTransferTime();

  @FieldType(FieldTypeEnum.ID)
  @ConditionallyRequired
  @ForeignKey(table = "trips.txt", field = "trip_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String fromTripId();

  @FieldType(FieldTypeEnum.ID)
  @ConditionallyRequired
  @ForeignKey(table = "trips.txt", field = "trip_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String toTripId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "routes.txt", field = "route_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String fromRouteId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "routes.txt", field = "route_id")
  @PrimaryKey(translationRecordIdType = UNSUPPORTED)
  String toRouteId();
}
