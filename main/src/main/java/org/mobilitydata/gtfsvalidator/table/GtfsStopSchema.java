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

import java.time.ZoneId;

@GtfsTable("stops.txt")
@Required
public interface GtfsStopSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @Required
    @PrimaryKey
    String stopId();

    String stopCode();

    @ConditionallyRequired
    String stopName();

    String ttsStopName();

    String stopDesc();

    @FieldType(FieldTypeEnum.LATITUDE)
    @ConditionallyRequired
    double stopLat();

    @FieldType(FieldTypeEnum.LONGITUDE)
    @ConditionallyRequired
    double stopLon();

    @FieldType(FieldTypeEnum.ID)
    @Index
    @ConditionallyRequired
    String zoneId();

    @FieldType(FieldTypeEnum.URL)
    String stopUrl();

    GtfsLocationType locationType();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "stops.txt", field = "stop_id")
    @ConditionallyRequired
    String parentStation();

    @FieldType(FieldTypeEnum.TIMEZONE)
    ZoneId stopTimezone();

    GtfsWheelchairBoarding wheelchairBoarding();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "levels.txt", field = "level_id")
    String levelId();

    String platformCode();
}
