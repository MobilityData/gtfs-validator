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
import org.mobilitydata.gtfsvalidator.annotation.DefaultValue;
import org.mobilitydata.gtfsvalidator.annotation.FieldType;
import org.mobilitydata.gtfsvalidator.annotation.FieldTypeEnum;
import org.mobilitydata.gtfsvalidator.annotation.FirstKey;
import org.mobilitydata.gtfsvalidator.annotation.ForeignKey;
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.Required;
import org.mobilitydata.gtfsvalidator.annotation.SequenceKey;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

@GtfsTable("stop_times.txt")
@Required
public interface GtfsStopTimeSchema extends GtfsEntity {
    @Required
    @ForeignKey(table = "trips.txt", field = "trip_id")
    @FirstKey
    String tripId();

    @ConditionallyRequired
    GtfsTime arrivalTime();

    @ConditionallyRequired
    GtfsTime departureTime();

    @FieldType(FieldTypeEnum.ID)
    @Required
    @ForeignKey(table = "stops.txt", field = "stop_id")
    String stopId();

    @Required
    @NonNegative
    @SequenceKey
    int stopSequence();

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
    GtfsStopTimesTimepoint timepoint();
}
