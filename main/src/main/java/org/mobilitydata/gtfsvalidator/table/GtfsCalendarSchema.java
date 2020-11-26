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
import org.mobilitydata.gtfsvalidator.type.GtfsDate;

@GtfsTable("calendar.txt")
@ConditionallyRequired
public interface GtfsCalendarSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String serviceId();

    @Required
    GtfsCalendarService monday();

    @Required
    GtfsCalendarService tuesday();

    @Required
    GtfsCalendarService wednesday();

    @Required
    GtfsCalendarService thursday();

    @Required
    GtfsCalendarService friday();

    @Required
    GtfsCalendarService saturday();

    @Required
    GtfsCalendarService sunday();

    @Required
    GtfsDate startDate();

    @Required
    GtfsDate endDate();
}
