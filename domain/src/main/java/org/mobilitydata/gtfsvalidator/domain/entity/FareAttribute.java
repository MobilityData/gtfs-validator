package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FareAttribute {

    @NotNull
    final String fareId;

    private final float price;

    @NotNull
    final String currency;

    @NotNull
    final PaymentMethod paymentMethod;

    @NotNull
    final Transfers transfers;

    private final String agencyId;
    private final int transferDuration;

    public FareAttribute(@NotNull String fareId, float price, @NotNull String currency,
                         @NotNull PaymentMethod paymentMethod, @NotNull Transfers transfers, String agencyId,
                         int transferDuration) {
        this.fareId = fareId;
        this.price = price;
        this.currency = currency;
        this.paymentMethod = paymentMethod;
        this.transfers = transfers;
        this.agencyId = agencyId;
        this.transferDuration = transferDuration;
    }

    public static class FareAttributeBuilder {

        private String fareId;
        private float price;
        private String currency;
        private PaymentMethod paymentMethod;
        private Transfers transfers;
        private String agencyId;
        private int transferDuration;


        public FareAttributeBuilder fareId(String fareId) {
            this.fareId = fareId;
            return this;
        }

        public FareAttributeBuilder price(float price) {
            this.price = price;
            return this;
        }

        public FareAttributeBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public FareAttributeBuilder paymentMethod(PaymentMethod paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public FareAttributeBuilder transfers(Transfers transfers) {
            this.transfers = transfers;
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
            return new FareAttribute(fareId, price, currency, paymentMethod, transfers, agencyId, transferDuration);
        }
    }
}
