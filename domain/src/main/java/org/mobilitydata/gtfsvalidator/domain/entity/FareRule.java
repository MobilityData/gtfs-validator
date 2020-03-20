package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FareRule {

    @NotNull
    final String fareId;

    private final String routeId;
    private final String originId;
    private final String destinationId;
    private final String containsId;

    public FareRule(@NotNull final String fareId,
                    final String routeId,
                    final String originId,
                    final String destinationId,
                    final String containsId) {
        this.fareId = fareId;
        this.routeId = routeId;
        this.originId = originId;
        this.destinationId = destinationId;
        this.containsId = containsId;
    }

    @NotNull
    public String getFareId() {
        return fareId;
    }

    public String getRouteId() {
        return routeId;
    }

    public String getOriginId() {
        return originId;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public String getContainsId() {
        return containsId;
    }

    public static class FareRuleBuilder {

        @SuppressWarnings("CanBeFinal")
        private String fareId;
        private String routeId;
        private String originId;
        private String destinationId;
        private String containsId;

        public FareRuleBuilder(@NotNull final String fareId) {
            this.fareId = fareId;
        }

        public FareRuleBuilder routeId(@Nullable String routeId) {
            this.routeId = routeId;
            return this;
        }

        public FareRuleBuilder originId(@Nullable String originId) {
            this.originId = originId;
            return this;
        }

        public FareRuleBuilder destinationId(@Nullable String destinationId) {
            this.destinationId = destinationId;
            return this;
        }

        public FareRuleBuilder containsId(String containsId) {
            this.containsId = containsId;
            return this;
        }

        public FareRule build() {
            return new FareRule(fareId, routeId, originId, destinationId, containsId);
        }
    }
}
