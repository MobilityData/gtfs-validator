/*
 * Copyright 2020 Google LLC, MobilityData IO
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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.math.BigDecimal;
import java.util.Currency;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_AGENCY_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_CURRENCY_TYPE;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_FARE_ID;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_PRICE;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_PAYMENT_METHOD;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_TRANSFERS;
import static org.mobilitydata.gtfsvalidator.table.GtfsFareAttribute.DEFAULT_TRANSFER_DURATION;

@RunWith(JUnit4.class)
public class GtfsFareAttributeTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsFareAttribute.Builder builder = new GtfsFareAttribute.Builder();

        GtfsFareAttribute underTest = builder
                .setFareId("fare id")
                .setPrice(BigDecimal.valueOf(2))
                .setCurrencyType(Currency.getInstance("USD"))
                .setPaymentMethod(1)
                .setTransfers(1)
                .setTransferDuration(34)
                .setAgencyId("agency id")
                .build();

        assertThat(underTest.fareId()).matches("fare id");
        assertThat(underTest.price()).isEqualTo(BigDecimal.valueOf(2));
        assertThat(underTest.currencyType()).isEqualTo(Currency.getInstance("USD"));
        assertThat(underTest.paymentMethod()).isEqualTo(GtfsFareAttributePaymentMethod.forNumber(1));
        assertThat(underTest.transfers()).isEqualTo(GtfsFareAttributeTransfers.forNumber(1));
        assertThat(underTest.transferDuration()).isEqualTo(34);
        assertThat(underTest.agencyId()).matches("agency id");

        assertThat(underTest.hasFareId()).isTrue();
        assertThat(underTest.hasPrice()).isTrue();
        assertThat(underTest.hasCurrencyType()).isTrue();
        assertThat(underTest.hasPaymentMethod()).isTrue();
        assertThat(underTest.hasTransfers()).isTrue();
        assertThat(underTest.hasAgencyId()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsFareAttribute.Builder builder = new GtfsFareAttribute.Builder();

        GtfsFareAttribute underTest = builder
                .setFareId(null)
                .setPrice(null)
                .setCurrencyType(null)
                .setPaymentMethod(null)
                .setTransfers(null)
                .setTransferDuration(null)
                .setAgencyId(null)
                .build();

        assertThat(underTest.fareId()).matches(DEFAULT_FARE_ID);
        assertThat(underTest.price()).isEqualTo(DEFAULT_PRICE);
        assertThat(underTest.currencyType()).isEqualTo(DEFAULT_CURRENCY_TYPE);
        // FIXME: should this be GtfsFareAttributePaymentMethod.UNRECOGNIZED since there is no default value in case
        //  field is missing?
        assertThat(underTest.paymentMethod())
                .isEqualTo(GtfsFareAttributePaymentMethod.forNumber(DEFAULT_PAYMENT_METHOD));
        assertThat(underTest.transfers()).isEqualTo(GtfsFareAttributeTransfers.forNumber(DEFAULT_TRANSFERS));
        assertThat(underTest.transferDuration()).isEqualTo(DEFAULT_TRANSFER_DURATION);
        assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasPrice()).isFalse();
        assertThat(underTest.hasCurrencyType()).isFalse();
        assertThat(underTest.hasPaymentMethod()).isFalse();
        assertThat(underTest.hasTransfers()).isFalse();
        assertThat(underTest.hasTransferDuration()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsFareAttribute.Builder builder = new GtfsFareAttribute.Builder();

        builder.setFareId("fare id")
                .setPrice(BigDecimal.valueOf(2))
                .setCurrencyType(Currency.getInstance("USD"))
                .setPaymentMethod(1)
                .setTransfers(1)
                .setTransferDuration(34)
                .setAgencyId("agency id");

        builder.clear();
        GtfsFareAttribute underTest = builder.build();

        assertThat(underTest.fareId()).matches(DEFAULT_FARE_ID);
        assertThat(underTest.price()).isEqualTo(DEFAULT_PRICE);
        assertThat(underTest.currencyType()).isEqualTo(DEFAULT_CURRENCY_TYPE);
        // FIXME: should this be GtfsFareAttributePaymentMethod.UNRECOGNIZED since there is no default value in case
        //  field is missing?
        assertThat(underTest.paymentMethod())
                .isEqualTo(GtfsFareAttributePaymentMethod.forNumber(DEFAULT_PAYMENT_METHOD));
        assertThat(underTest.transfers()).isEqualTo(GtfsFareAttributeTransfers.forNumber(DEFAULT_TRANSFERS));
        assertThat(underTest.transferDuration()).isEqualTo(DEFAULT_TRANSFER_DURATION);
        assertThat(underTest.agencyId()).matches(DEFAULT_AGENCY_ID);

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasPrice()).isFalse();
        assertThat(underTest.hasCurrencyType()).isFalse();
        assertThat(underTest.hasPaymentMethod()).isFalse();
        assertThat(underTest.hasTransfers()).isFalse();
        assertThat(underTest.hasTransferDuration()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsFareAttribute.Builder builder = new GtfsFareAttribute.Builder();

        GtfsFareAttribute underTest = builder.build();

        assertThat(underTest.fareId()).isNull();
        assertThat(underTest.price()).isNull();
        assertThat(underTest.currencyType()).isEqualTo(DEFAULT_CURRENCY_TYPE);
        // FIXME: should this be GtfsFareAttributePaymentMethod.UNRECOGNIZED since there is no default value in case
        //  field is missing?
        assertThat(underTest.paymentMethod())
                .isEqualTo(GtfsFareAttributePaymentMethod.forNumber(DEFAULT_PAYMENT_METHOD));
        assertThat(underTest.transfers()).isEqualTo(GtfsFareAttributeTransfers.forNumber(DEFAULT_TRANSFERS));
        assertThat(underTest.transferDuration()).isEqualTo(DEFAULT_TRANSFER_DURATION);
        assertThat(underTest.agencyId()).isNull();

        assertThat(underTest.hasFareId()).isFalse();
        assertThat(underTest.hasPrice()).isFalse();
        assertThat(underTest.hasCurrencyType()).isFalse();
        assertThat(underTest.hasPaymentMethod()).isFalse();
        assertThat(underTest.hasTransfers()).isFalse();
        assertThat(underTest.hasTransferDuration()).isFalse();
        assertThat(underTest.hasAgencyId()).isFalse();
    }
}
