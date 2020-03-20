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

    private final float length;
    private final int traversalTime;
    private final int stairCount;
    private final float maxSlope;
    private final float minWidth;
    private final String signpostedAs;
    private final String reversedSignpostedAs;

    public Pathway(@NotNull final String pathwayId,
                   @NotNull final String fromStopId,
                   @NotNull final String toStopId,
                   @NotNull final PathwayMode pathwayMode,
                   @NotNull final IsBidirectional isBidirectional,
                   final float length,
                   final int traversalTime,
                   final int stairCount,
                   final float maxSlope,
                   final float minWidth,
                   final String signpostedAs,
                   final String reversedSignpostedAs) {
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

    public float getLength() {
        return length;
    }

    public int getTraversalTime() {
        return traversalTime;
    }

    public int getStairCount() {
        return stairCount;
    }

    public float getMaxSlope() {
        return maxSlope;
    }

    public float getMinWidth() {
        return minWidth;
    }

    public String getSignpostedAs() {
        return signpostedAs;
    }

    public String getReversedSignpostedAs() {
        return reversedSignpostedAs;
    }

    @SuppressWarnings("CanBeFinal")
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
