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

import org.mobilitydata.gtfsvalidator.annotation.*;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;


@GtfsTable("routes.txt")
public interface GtfsRouteSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String routeId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "agency.txt", field = "agency_id")
    @ConditionallyRequired
    String agencyId();

    @ConditionallyRequired
    String routeShortName();

    @ConditionallyRequired
    String routeLongName();

    String routeDesc();

    GtfsRouteType routeType();

    @FieldType(FieldTypeEnum.URL)
    String routeUrl();

    @DefaultValue("FFFFFF")
    GtfsColor routeColor();

    @DefaultValue("000000")
    GtfsColor routeTextColor();

    @NonNegative
    int routeSortOrder();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousPickup();

    @DefaultValue("1")
    GtfsContinuousPickupDropOff continuousDropOff();
}
