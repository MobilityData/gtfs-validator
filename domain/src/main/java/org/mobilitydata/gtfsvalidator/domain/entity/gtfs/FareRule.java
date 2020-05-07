package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.ArrayList;
import java.util.List;

public class FareRule extends GtfsEntity {
    @NotNull
    final String fareId;
    @Nullable
    private final String routeId;
    @Nullable
    private final String originId;
    @Nullable
    private final String destinationId;
    @Nullable
    private final String containsId;

    private FareRule(@NotNull final String fareId,
                     @Nullable final String routeId,
                     @Nullable final String originId,
                     @Nullable final String destinationId,
                     @Nullable final String containsId) {
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

    @Nullable
    public String getRouteId() {
        return routeId;
    }

    @Nullable
    public String getOriginId() {
        return originId;
    }

    @Nullable
    public String getDestinationId() {
        return destinationId;
    }

    @Nullable
    public String getContainsId() {
        return containsId;
    }

    public static class FareRuleBuilder {
        @SuppressWarnings("NotNullFieldNotInitialized") // to avoid lint
        @NotNull
        private String fareId;
        @Nullable
        private String routeId;
        @Nullable
        private String originId;
        @Nullable
        private String destinationId;
        @Nullable
        private String containsId;
        private final List<Notice> noticeCollection = new ArrayList<>();

        public FareRuleBuilder fareId(@NotNull final String fareId) {
            this.fareId = fareId;
            return this;
        }

        public FareRuleBuilder routeId(@Nullable final String routeId) {
            this.routeId = routeId;
            return this;
        }

        public FareRuleBuilder originId(@Nullable final String originId) {
            this.originId = originId;
            return this;
        }

        public FareRuleBuilder destinationId(@Nullable final String destinationId) {
            this.destinationId = destinationId;
            return this;
        }

        public FareRuleBuilder containsId(@Nullable final String containsId) {
            this.containsId = containsId;
            return this;
        }

        public EntityBuildResult<?> build() {
            noticeCollection.clear();

            //noinspection ConstantConditions to avoid lint
            if (fareId == null) {
                noticeCollection.add(new MissingRequiredValueNotice("fare_rules.txt", "fare_id", fareId));
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new FareRule(fareId, routeId, originId, destinationId, containsId));
            }
        }
    }
}
