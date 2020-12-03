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
import org.mobilitydata.gtfsvalidator.annotation.NonNegative;
import org.mobilitydata.gtfsvalidator.annotation.PrimaryKey;
import org.mobilitydata.gtfsvalidator.annotation.Required;

import java.math.BigDecimal;
import java.util.Currency;

@GtfsTable("fare_attributes.txt")
public interface GtfsFareAttributeSchema extends GtfsEntity {
    @FieldType(FieldTypeEnum.ID)
    @PrimaryKey
    @Required
    String fareId();

    @Required
    @NonNegative
    BigDecimal price();

    @Required
    Currency currencyType();

    @Required
    GtfsFareAttributePaymentMethod paymentMethod();

    /**
     * Indicates the number of transfers permitted on this fare.
     * <p>
     * If this field is left empty, it means that unlimited transfers are permitted.
     * The {@code GtfsFareAttributeTransfers} enum does not have a special constant for that case. So, before calling
     * {@code transfers()}, always check if this field is set using {@code hasTransfers()} method.
     *
     * @return The number of permitted transfers.
     */
    GtfsFareAttributeTransfers transfers();

    @FieldType(FieldTypeEnum.ID)
    @ForeignKey(table = "agency.txt", field = "agency_id")
    @ConditionallyRequired
    String agencyId();

    @NonNegative
    int transfer_duration();
}
