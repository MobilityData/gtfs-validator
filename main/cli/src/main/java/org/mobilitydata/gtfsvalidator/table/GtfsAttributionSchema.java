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

import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("attributions.txt")
public interface GtfsAttributionSchema extends GtfsEntity {
  @FieldType(FieldTypeEnum.ID)
  @PrimaryKey
  String attributionId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "agency.txt", field = "agency_id")
  String agencyId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "routes.txt", field = "route_id")
  String routeId();

  @FieldType(FieldTypeEnum.ID)
  @ForeignKey(table = "trips.txt", field = "trip_id")
  String tripId();

  @Required
  String organizationName();

  GtfsAttributionRole isProducer();

  GtfsAttributionRole isOperator();

  GtfsAttributionRole isAuthority();

  @FieldType(FieldTypeEnum.URL)
  String attributionUrl();

  @FieldType(FieldTypeEnum.EMAIL)
  String attributionEmail();

  @FieldType(FieldTypeEnum.PHONE_NUMBER)
  String attributionPhone();
}
