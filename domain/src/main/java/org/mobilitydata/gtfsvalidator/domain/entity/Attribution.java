package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;

public class Attribution {

    private final String attributionId;
    private final String agencyId;
    private final String routeId;
    private final String tripId;

    @NotNull
    private final String organizationName;

    private final IsProducer isProducer;
    private final IsOperator isOperator;
    private final IsAuthority isAuthority;
    private final String attributionUrl;
    private final String attributionEmail;
    private final String attributionPhone;

    public Attribution(String attributionId,
                       String agencyId,
                       String routeId,
                       String tripId,
                       @NotNull String organizationName,
                       IsProducer isProducer,
                       IsOperator isOperator,
                       IsAuthority isAuthority,
                       String attributionUrl,
                       String attributionEmail,
                       String attributionPhone) {
        this.attributionId = attributionId;
        this.agencyId = agencyId;
        this.routeId = routeId;
        this.tripId = tripId;
        this.organizationName = organizationName;
        this.isProducer = isProducer;
        this.isOperator = isOperator;
        this.isAuthority = isAuthority;
        this.attributionUrl = attributionUrl;
        this.attributionEmail = attributionEmail;
        this.attributionPhone = attributionPhone;
    }

    public String getAttributionId() {
        return attributionId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getTripId() {
        return tripId;
    }

    @NotNull
    public String getOrganizationName() {
        return organizationName;
    }

    public IsProducer getIsProducer() {
        return isProducer;
    }

    public IsOperator getIsOperator() {
        return isOperator;
    }

    public IsAuthority getIsAuthority() {
        return isAuthority;
    }

    public String getAttributionUrl() {
        return attributionUrl;
    }

    public String getAttributionEmail() {
        return attributionEmail;
    }

    public String getAttributionPhone() {
        return attributionPhone;
    }

    public static class AttributionBuilder {
        private String attributionId;
        private String agencyId;
        private String routeId;
        private String tripId;
        private String organizationName;
        private IsProducer isProducer;
        private IsOperator isOperator;
        private IsAuthority isAuthority;
        private String attributionUrl;
        private String attributionEmail;
        private String attributionPhone;

        public AttributionBuilder(@NotNull String organizationName) {
            this.organizationName = organizationName;
        }

        public AttributionBuilder attributionId(String attributionId) {
            this.attributionId = attributionId;
            return this;
        }

        public AttributionBuilder agencyId(String agencyId) {
            this.agencyId = agencyId;
            return this;
        }

        public AttributionBuilder routeId(String routeId) {
            this.routeId = routeId;
            return this;
        }

        public AttributionBuilder tripId(String tripId) {
            this.tripId = tripId;
            return this;
        }

        public AttributionBuilder organizationName(String organizationName) {
            this.organizationName = organizationName;
            return this;
        }

        public AttributionBuilder isProducer(IsProducer isProducer) {
            this.isProducer = isProducer;
            return this;
        }

        public AttributionBuilder isAuthority(IsAuthority isAuthority) {
            this.isAuthority = isAuthority;
            return this;
        }

        public AttributionBuilder isOperator(IsOperator isOperator) {
            this.isOperator = isOperator;
            return this;
        }

        public AttributionBuilder attributionUrl(String attributionUrl) {
            this.attributionUrl = attributionUrl;
            return this;
        }

        public AttributionBuilder attributionEmail(String attributionEmail) {
            this.attributionEmail = attributionEmail;
            return this;
        }

        public AttributionBuilder attributionPhone(String attributionPhone) {
            this.attributionPhone = attributionPhone;
            return this;
        }

        public Attribution build() {
            return new Attribution(attributionId, agencyId, routeId, tripId, organizationName, isProducer, isOperator,
                    isAuthority, attributionUrl, attributionEmail, attributionPhone);
        }
    }
}
