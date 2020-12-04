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
import org.mobilitydata.gtfsvalidator.annotation.GtfsTable;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

import java.util.Locale;
import java.util.TimeZone;

@GtfsTable("agency.txt")
@Required
public interface GtfsAgencySchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @ConditionallyRequired
    String agencyId();

    @Required
    String agencyName();

    @FieldType(FieldTypeEnum.URL)
    @Required
    String agencyUrl();

    @Required
    TimeZone agencyTimezone();

    Locale agencyLang();

    @FieldType(FieldTypeEnum.PHONE_NUMBER)
    String agencyPhone();

    @FieldType(FieldTypeEnum.URL)
    String agencyFareUrl();

    @FieldType(FieldTypeEnum.EMAIL)
    String agencyEmail();
}
