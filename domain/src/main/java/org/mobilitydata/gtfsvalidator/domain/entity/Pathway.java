package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Pathway {

    @NotNull
    private final String pathwayId;

    @NotNull
    private final String fromStopId;

    @NotNull
    private final String toStopId;

    @NotNull
    private final PathwayMode pathwayMode;

    @NotNull
    private final IsBidirectional isBidirectional;

    @Nullable
    private final Float length;

    @Nullable
    private final Integer traversalTime;

    @Nullable
    private final Integer stairCount;

    @Nullable
    private final Float maxSlope;

    @Nullable
    private final Float minWidth;

    @Nullable
    private final String signpostedAs;

    @Nullable
    private final String reversedSignpostedAs;

    private Pathway(@NotNull final String pathwayId,
                    @NotNull final String fromStopId,
                    @NotNull final String toStopId,
                    @NotNull final PathwayMode pathwayMode,
                    @NotNull final IsBidirectional isBidirectional,
                    @Nullable final Float length,
                    @Nullable final Integer traversalTime,
                    @Nullable final Integer stairCount,
                    @Nullable final Float maxSlope,
                    @Nullable final Float minWidth,
                    @Nullable final String signpostedAs,
                    @Nullable final String reversedSignpostedAs) {
        this.pathwayId = pathwayId;
        this.fromStopId = fromStopId;
        this.toStopId = toStopId;
        this.pathwayMode = pathwayMode;
        this.isBidirectional = isBidirectional;
        this.length = length;
        this.traversalTime = traversalTime;
        this.stairCount = stairCount;
        this.maxSlope = maxSlope;
        this.minWidth = minWidth;
        this.signpostedAs = signpostedAs;
        this.reversedSignpostedAs = reversedSignpostedAs;
    }

    @NotNull
    public String getPathwayId() {
        return pathwayId;
    }

    @NotNull
    public String getFromStopId() {
        return fromStopId;
    }

    @NotNull
    public String getToStopId() {
        return toStopId;
    }

    @NotNull
    public PathwayMode getPathwayMode() {
        return pathwayMode;
    }

    @NotNull
    public IsBidirectional getIsBidirectional() {
        return isBidirectional;
    }

    @Nullable
    public Float getLength() {
        return length;
    }

    @Nullable
    public Integer getTraversalTime() {
        return traversalTime;
    }

    @Nullable
    public Integer getStairCount() {
        return stairCount;
    }

    @Nullable
    public Float getMaxSlope() {
        return maxSlope;
    }

    @Nullable
    public Float getMinWidth() {
        return minWidth;
    }

    @Nullable
    public String getSignpostedAs() {
        return signpostedAs;
    }

    @Nullable
    public String getReversedSignpostedAs() {
        return reversedSignpostedAs;
    }

    public static class PathwayBuilder {
        private String pathwayId;
        private String fromStopId;
        private String toStopId;
        private PathwayMode pathwayMode;
        private IsBidirectional isBidirectional;
        private Float length;
        private Integer traversalTime;
        private Integer stairCount;
        private Float maxSlope;
        private Float minWidth;
        private String signpostedAs;
        private String reversedSignpostedAs;

        PathwayBuilder(@NotNull final String pathwayId,
                       @NotNull final String fromStopId,
                       @NotNull final String toStopId,
                       @NotNull final PathwayMode pathwayMode,
                       @NotNull final IsBidirectional isBidirectional) {
            this.pathwayId = pathwayId;
            this.fromStopId = fromStopId;
            this.toStopId = toStopId;
            this.pathwayMode = pathwayMode;
            this.isBidirectional = isBidirectional;
        }

        public PathwayBuilder pathwayId(@NotNull final String pathwayId) {
            this.pathwayId = pathwayId;
            return this;
        }

        public PathwayBuilder fromStopId(@NotNull final String fromStopId) {
            this.fromStopId = fromStopId;
            return this;
        }

        public PathwayBuilder toStopId(@NotNull final String toStopId) {
            this.toStopId = toStopId;
            return this;
        }

        public PathwayBuilder pathwayMode(@NotNull final PathwayMode pathwayMode) {
            this.pathwayMode = pathwayMode;
            return this;
        }

        public PathwayBuilder isBidirectional(@NotNull final IsBidirectional isBidirectional) {
            this.isBidirectional = isBidirectional;
            return this;
        }

        public PathwayBuilder length(@Nullable final Float length) {
            this.length = length;
            return this;
        }

        public PathwayBuilder traversalTime(@Nullable final Integer traversalTime) {
            this.traversalTime = traversalTime;
            return this;
        }

        public PathwayBuilder stairCount(@Nullable final Integer stairCount) {
            this.stairCount = stairCount;
            return this;
        }

        public PathwayBuilder maxSlope(@Nullable final Float maxSlope) {
            this.maxSlope = maxSlope;
            return this;
        }

        public PathwayBuilder minWidth(@Nullable final Float minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public PathwayBuilder signpostedAs(@Nullable final String signpostedAs) {
            this.signpostedAs = signpostedAs;
            return this;
        }

        public PathwayBuilder reversedSignpostedAs(@Nullable final String reversedSignpostedAs) {
            this.reversedSignpostedAs = reversedSignpostedAs;
            return this;
        }

        public Pathway build() {
            return new Pathway(pathwayId, fromStopId, toStopId, pathwayMode, isBidirectional, length, traversalTime,
                    stairCount, maxSlope, minWidth, signpostedAs, reversedSignpostedAs);
        }
    }
}
