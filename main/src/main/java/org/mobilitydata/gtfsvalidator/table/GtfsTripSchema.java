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

import org.mobilitydata.gtfsvalidator.annotation.ConditionallyRequired;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.Index;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

@GtfsTable("trips.txt")
@Required
public interface GtfsTripSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @PrimaryKey
    String tripId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "routes.txt", field = "route_id")
    String routeId();

    @FieldType(FieldTypeEnum.ID)
    @Required
    String serviceId();

    String tripHeadsign();

    String tripShortName();

    GtfsTripDirectionId directionId();

    @FieldType(FieldTypeEnum.ID)
    @Index
    String blockId();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "shapes.txt", field = "shape_id")
    @Index
    @ConditionallyRequired
    String shapeId();

    GtfsWheelchairBoarding wheelchairAccessible();

    GtfsBikesAllowed bikesAllowed();
}
