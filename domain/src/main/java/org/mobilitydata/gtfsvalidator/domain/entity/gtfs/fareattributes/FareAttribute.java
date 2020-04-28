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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GenericType;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedValueNotice;

import java.util.List;

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
        private Integer paymentMethodInteger;
        private Integer transfersInteger;

        public FareAttributeBuilder fareId(final String fareId) {
            this.fareId = fareId;
            return this;
        }

        public FareAttributeBuilder price(final Float price) {
            this.price = price;
            return this;
        }

        public FareAttributeBuilder currencyType(final String currencyType) {
            this.currencyType = currencyType;
            return this;
        }

        public FareAttributeBuilder paymentMethod(final Integer paymentMethod) {
            this.paymentMethod = PaymentMethod.fromInt(paymentMethod);
            this.paymentMethodInteger = paymentMethod;
            return this;
        }

        public FareAttributeBuilder transfers(final Integer transfers) {
            this.transfers = Transfers.fromInt(transfers);
            this.transfersInteger = transfers;
            return this;
        }

        public FareAttributeBuilder agencyId(final @Nullable String agencyId) {
            this.agencyId = agencyId; // TODO: to be modified see https://github.com/MobilityData/gtfs-validator/issues/109
            return this;
        }

        @SuppressWarnings("UnusedReturnValue")
        public FareAttributeBuilder transferDuration(final int transferDuration) {
            this.transferDuration = transferDuration;
            return this;
        }

        @SuppressWarnings("rawtypes")
        public GenericType build(final List<Notice> noticeCollection) {
            if (price == null || price < 0 || fareId == null || currencyType == null || paymentMethodInteger == null ||
                    paymentMethodInteger < 0 || paymentMethodInteger > 2 || (transfersInteger != null &&
                    (transfersInteger < 0 || transfersInteger > 2)) || transferDuration != null && transferDuration < 0) {

                if (price == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                            "price", fareId));
                } else if (price < 0) {
                    noticeCollection.add(new FloatFieldValueOutOfRangeNotice("fare_attributes.txt",
                            "price", fareId, 0, Float.MAX_VALUE, price));
                }
                if (fareId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                            "fare_id", fareId));
                }
                if (currencyType == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                            "currency_type", fareId));
                }
                if (paymentMethodInteger == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                            "payment_method", fareId));
                } else if (paymentMethodInteger < 0 || paymentMethodInteger > 1) {
                    noticeCollection.add(new UnexpectedValueNotice("fare_attributes.txt",
                            "payment_method", fareId, paymentMethodInteger));
                }
                //noinspection ConstantConditions
                if (transfersInteger < 0 || transfersInteger > 2) {
                    noticeCollection.add(new UnexpectedValueNotice("fare_attributes.txt",
                            "transfers", fareId, transfersInteger));
                }
                if (transferDuration < 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("fare_attributes.txt",
                            "transfer_duration", fareId, 0, Integer.MAX_VALUE, transferDuration));
                }
                //noinspection unchecked
                return new GenericType(noticeCollection, false);
            }
            //noinspection unchecked
            return new GenericType(new FareAttribute(fareId, price, currencyType, paymentMethod, transfers, agencyId,
                    transferDuration),
                    true);
        }
    }
}