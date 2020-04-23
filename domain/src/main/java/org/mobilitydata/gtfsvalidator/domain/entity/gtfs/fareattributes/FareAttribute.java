/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.fareattributes;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FareAttribute {

    @NotNull
    final String fareId;

    private final float price;

    @NotNull
    final String currencyType;

    @NotNull
    final PaymentMethod paymentMethod;

    @NotNull
    final Transfers transfers;

    @Nullable
    private final String agencyId;
    @Nullable
    private final Integer transferDuration;

    @NotNull
    public String getFareId() {
        return fareId;
    }

    public float getPrice() {
        return price;
    }

    @NotNull
    public String getCurrencyType() {
        return currencyType;
    }

    @NotNull
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    @NotNull
    public Transfers getTransfers() {
        return transfers;
    }

    @Nullable
    public String getAgencyId() {
        return agencyId;
    }

    @Nullable
    public Integer getTransferDuration() {
        return transferDuration;
    }

    public FareAttribute(@NotNull final String fareId,
                         final float price,
                         @NotNull final String currencyType,
                         @NotNull final PaymentMethod paymentMethod,
                         @NotNull final Transfers transfers,
                         @Nullable final String agencyId,
                         @Nullable final Integer transferDuration) {
        this.fareId = fareId;
        this.price = price;
        this.currencyType = currencyType;
        this.paymentMethod = paymentMethod;
        this.transfers = transfers;
        this.agencyId = agencyId;
        this.transferDuration = transferDuration;
    }

    public static class FareAttributeBuilder {
        private String fareId;
        private Float price;
        private String currencyType;
        private PaymentMethod paymentMethod;
        private Transfers transfers;
        private String agencyId;
        private Integer transferDuration;

        public FareAttributeBuilder fareId(String fareId) {
            this.fareId = fareId;
            return this;
        }

        public FareAttributeBuilder price(Float price) {
            this.price = price;
            return this;
        }

        public FareAttributeBuilder currencyType(String currencyType) {
            this.currencyType = currencyType;
            return this;
        }

        public FareAttributeBuilder paymentMethod(Integer paymentMethod) {
            this.paymentMethod = PaymentMethod.fromInt(paymentMethod);
            return this;
        }

        public FareAttributeBuilder transfers(Integer transfers) {
            this.transfers = Transfers.fromInt(transfers);
            return this;
        }

        public FareAttributeBuilder agencyId(@Nullable String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public FareAttributeBuilder transferDuration(int transferDuration) {
            this.transferDuration = transferDuration;
            return this;
        }

        public FareAttribute build() {
            if (fareId == null) {
                throw new IllegalArgumentException("field `fare_id` in file `fare_attributes.txt` can not be null");
            }
            if (price == null) {
                throw new IllegalArgumentException("field `price` in file `fare_attributes.txt` can not be null");
            }
            if (currencyType == null) {
                throw new IllegalArgumentException("field `currency_type` in file `fare_attributes.txt` can not be null");
            }
            if (paymentMethod == null) {
                throw new IllegalArgumentException("unexpected value encountered for field `payment_method` in file" +
                        " `fare_attributes.txt`");
            }
            if (transfers == null) {
                throw new IllegalArgumentException("unexpected value encountered for field `transfers` in file" +
                        " `fare_attributes.txt`");
            }
            return new FareAttribute(fareId, price, currencyType, paymentMethod, transfers, agencyId, transferDuration);
        }
    }
}