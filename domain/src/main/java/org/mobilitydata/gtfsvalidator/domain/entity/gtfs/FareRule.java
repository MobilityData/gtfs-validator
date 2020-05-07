package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in fare_rules.txt. Can not be directly instantiated: user must use the
 * {@link FareRuleBuilder} to create this.
 */
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

    /**
     * Class for all entities defined in fare_rules.txt
     *
     * @param fareId        identifies a fare class
     * @param routeId       identifies a route associated with the fare class
     * @param originId      identifies an origin zone
     * @param destinationId identifies a destination zone
     * @param containsId    identifies the zones that a rider will enter while using a given fare class. Used in some
     *                      systems to calculate correct fare class
     */
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

    /**
     * Builder class to create {@link FareRule} objects. Allows an unordered definition of the different attributes of
     * {@link FareRule}.
     */
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

        /**
         * Sets field fareId value and returns this
         *
         * @param fareId identifies a fare class
         * @return builder for future object creation
         */
        public FareRuleBuilder fareId(@NotNull final String fareId) {
            this.fareId = fareId;
            return this;
        }

        /**
         * Sets field routeId value and returns this
         *
         * @param routeId identifies a route associated with the fare class
         * @return builder for future object creation
         */
        public FareRuleBuilder routeId(@Nullable final String routeId) {
            this.routeId = routeId;
            return this;
        }

        /**
         * Sets field originId value and returns this
         *
         * @param originId identifies an origin zone
         * @return builder for future object creation
         */
        public FareRuleBuilder originId(@Nullable final String originId) {
            this.originId = originId;
            return this;
        }

        /**
         * Sets field destinationId value and returns this
         *
         * @param destinationId identifies a destination zone
         * @return builder for future object creation
         */
        public FareRuleBuilder destinationId(@Nullable final String destinationId) {
            this.destinationId = destinationId;
            return this;
        }

        /**
         * Sets field containsId value and returns this
         *
         * @param containsId identifies the zones that a rider will enter while using a given fare class. Used in some
         *                   systems to calculate correct fare class
         * @return builder for future object creation
         */
        public FareRuleBuilder containsId(@Nullable final String containsId) {
            this.containsId = containsId;
            return this;
        }

        /**
         * Entity representing a row from fare_rules.txt if the requirements from the official GTFS specification
         * are met. Otherwise, method returns an entity representing a list of notices.
         *
         * @return Entity representing a row from fare_rules.txt if the requirements from the official GTFS
         * specification are met. Otherwise, method returns an entity representing a list of notices.
         */
        public EntityBuildResult<?> build() {
            noticeCollection.clear();

            //noinspection ConstantConditions to avoid lint
            if (fareId == null) {
                noticeCollection.add(new MissingRequiredValueNotice("fare_rules.txt", "fare_id",
                        fareId));
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new FareRule(fareId, routeId, originId, destinationId, containsId));
            }
        }
    }
}
