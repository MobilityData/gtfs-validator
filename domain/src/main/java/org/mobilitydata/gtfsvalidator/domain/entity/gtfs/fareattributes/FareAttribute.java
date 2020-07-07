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
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in fare_attributes.txt. Can not be directly instantiated: user must use the
 * {@code FareAttribute.FareAttributeBuilder} to create this.
 */
public class FareAttribute extends GtfsEntity {
    @NotNull
    final String fareId;
    @NotNull
    private final Float price;
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

    /**
     * Class for all entities defined in fare_attributes.txt
     *
     * @param fareId           identifies a fare class
     * @param price            fare price, in the unit specified by currency_type
     * @param currencyType     currency used to pay the fare
     * @param paymentMethod    indicates when the fare must be paid
     * @param transfers        indicates the number of transfers permitted on this fare
     * @param agencyId         identifies the relevant agency for a fare
     * @param transferDuration length of time in seconds before a transfer expires
     */
    private FareAttribute(@NotNull final String fareId,
                          @NotNull final Float price,
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

    @NotNull
    public String getFareId() {
        return fareId;
    }

    @NotNull
    public Float getPrice() {
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

    /**
     * Builder class to create {@code FareAttribute} objects. Allows an unordered definition of the different attributes
     * of {@link FareAttribute}.
     */
    public static class FareAttributeBuilder {
        private String fareId;
        private Float price;
        private String currencyType;
        private PaymentMethod paymentMethod;
        private Transfers transfers;
        private String agencyId;
        private Integer transferDuration;
        private Integer originalPaymentMethodInteger;
        private Integer originalTransferInteger;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field fareId value and returns this
         *
         * @param fareId identifies a fare class
         * @return builder for future object creation
         */
        public FareAttributeBuilder fareId(final String fareId) {
            this.fareId = fareId;
            return this;
        }

        /**
         * Sets field price value and returns this
         *
         * @param price fare price, in the unit specified by field {@link FareAttribute #currencyType}
         * @return builder for future object creation
         */
        public FareAttributeBuilder price(final Float price) {
            this.price = price;
            return this;
        }

        /**
         * Sets field currencyType value and returns this
         *
         * @param currencyType currency used to pay the fare
         * @return builder for future object creation
         */
        public FareAttributeBuilder currencyType(final String currencyType) {
            this.currencyType = currencyType;
            return this;
        }

        /**
         * Sets field paymentMethod value and returns this
         *
         * @param paymentMethod indicates when the fare must be paid
         * @return builder for future object creation
         */
        public FareAttributeBuilder paymentMethod(final Integer paymentMethod) {
            this.paymentMethod = PaymentMethod.fromInt(paymentMethod);
            this.originalPaymentMethodInteger = paymentMethod;
            return this;
        }

        /**
         * Sets field transfers value and returns this
         *
         * @param transfers indicates the number of transfers permitted on this fare
         * @return builder for future object creation
         */
        public FareAttributeBuilder transfers(final Integer transfers) {
            this.transfers = Transfers.fromInt(transfers);
            this.originalTransferInteger = transfers;
            return this;
        }

        /**
         * Sets field agencyId value and returns this
         *
         * @param agencyId identifies the relevant agency for a fare
         * @return builder for future object creation
         */
        public FareAttributeBuilder agencyId(final @Nullable String agencyId) {
            this.agencyId = agencyId; // TODO: to be modified see https://github.com/MobilityData/gtfs-validator/issues/109
            return this;
        }

        /**
         * Sets field transferDuration value and returns this
         *
         * @param transferDuration length of time in seconds before a transfer expires
         * @return builder for future object creation
         */
        public FareAttributeBuilder transferDuration(final Integer transferDuration) {
            this.transferDuration = transferDuration;
            return this;
        }

        /**
         * This methods returns an {@code EntityBuildResult} representing a row from fare_attributes.txt if the
         * requirements from the official GTFS specification are met. Otherwise, method returns a collection of notices
         * specifying the issues.
         *
         * @return {@link EntityBuildResult} representing a row from fare_attributes.txt if the requirements from the
         * official GTFS specification are met. Otherwise, method returns a collection pf notices specifying the issues.
         */
        public EntityBuildResult<?> build() {
            if (price == null || price < 0 || fareId == null || currencyType == null ||
                    !PaymentMethod.isEnumValueValid(originalPaymentMethodInteger) ||
                    !Transfers.isEnumValueValid(originalTransferInteger) ||
                    (transferDuration != null && transferDuration < 0)) {

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
                // the following statement is true when PaymentMethod.isEnumValueValid(originalPaymentMethodInteger)
                // returns false
                if (paymentMethod == null) {
                    if (originalPaymentMethodInteger == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                                "payment_method", fareId));
                    } else {
                        noticeCollection.add(new UnexpectedEnumValueNotice("fare_attributes.txt",
                                "payment_method", fareId, originalPaymentMethodInteger));
                    }
                }
                // the following statement is true when Transfers.isEnumValueValid(originalTransferInteger)
                // returns false
                if (transfers == null) {
                    if (!Transfers.isEnumValueValid(originalTransferInteger)) {
                        noticeCollection.add(new UnexpectedEnumValueNotice("fare_attributes.txt",
                                "transfers", fareId, originalTransferInteger));
                    } else {
                        noticeCollection.add(new MissingRequiredValueNotice("fare_attributes.txt",
                                "transfers", fareId));
                    }
                }
                if (transferDuration < 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("fare_attributes.txt",
                            "transfer_duration", fareId, 0, Integer.MAX_VALUE, transferDuration));
                }
                return new EntityBuildResult<>(noticeCollection);
            }
            return new EntityBuildResult<>(new FareAttribute(fareId, price, currencyType, paymentMethod, transfers,
                    agencyId, transferDuration));
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         * @return builder with all fields set to null;
         */
        public FareAttributeBuilder clear() {
            fareId = null;
            price = null;
            currencyType = null;
            paymentMethod = null;
            transfers = null;
            agencyId = null;
            transferDuration = null;
            originalPaymentMethodInteger = null;
            originalTransferInteger = null;
            noticeCollection.clear();
            return this;
        }
    }
}