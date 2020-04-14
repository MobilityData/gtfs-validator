package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
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
    private final Boolean isBidirectional;

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
                    @NotNull final Boolean isBidirectional,
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

    @SuppressWarnings("unused")
    @NotNull
    public String getFromStopId() {
        return fromStopId;
    }

    @SuppressWarnings("unused")
    @NotNull
    public String getToStopId() {
        return toStopId;
    }

    @SuppressWarnings("unused")
    @NotNull
    public PathwayMode getPathwayMode() {
        return pathwayMode;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Boolean getIsBidirectional() {
        return isBidirectional;
    }

    @SuppressWarnings("unused")
    @Nullable
    public Float getLength() {
        return length;
    }

    @SuppressWarnings("unused")
    @Nullable
    public Integer getTraversalTime() {
        return traversalTime;
    }

    @SuppressWarnings("unused")
    @Nullable
    public Integer getStairCount() {
        return stairCount;
    }

    @SuppressWarnings("unused")
    @Nullable
    public Float getMaxSlope() {
        return maxSlope;
    }

    @SuppressWarnings("unused")
    @Nullable
    public Float getMinWidth() {
        return minWidth;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getSignpostedAs() {
        return signpostedAs;
    }

    @SuppressWarnings("unused")
    @Nullable
    public String getReversedSignpostedAs() {
        return reversedSignpostedAs;
    }

    public static class PathwayBuilder {
        private String pathwayId;
        private String fromStopId;
        private String toStopId;
        private PathwayMode pathwayMode;
        private Boolean isBidirectional;

        @Nullable
        private Float length;

        @Nullable
        private Integer traversalTime;

        @Nullable
        private Integer stairCount;

        @Nullable
        private Float maxSlope;

        @Nullable
        private Float minWidth;

        @Nullable
        private String signpostedAs;

        @Nullable
        private String reversedSignpostedAs;

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

        public PathwayBuilder pathwayMode(@NotNull final Integer pathwayMode) {
            this.pathwayMode = PathwayMode.fromInt(pathwayMode);
            return this;
        }

        public PathwayBuilder isBidirectional(@NotNull Integer isBidirectional) {
            //noinspection ConstantConditions
            if (isBidirectional == null) {
                this.isBidirectional = null;
            } else if (isBidirectional == 0) {
                this.isBidirectional = false;
            } else if (isBidirectional == 1) {
                this.isBidirectional = true;
            }
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
            if (pathwayId == null) {
                throw new IllegalArgumentException("field pathway_id can not be null");
            }
            if (fromStopId == null) {
                throw new IllegalArgumentException("field from_stop_id can not be null");
            }
            if (toStopId == null) {
                throw new IllegalArgumentException("field to_stop_id can not be null");
            }
            if (pathwayMode == null) {
                throw new IllegalArgumentException("unexpected value for field pathway_mode");
            }
            if (isBidirectional == null) {
                throw new IllegalArgumentException("invalid value for field is_bidirectional");
            }
            if (length != null && length < 0) {
                throw new IllegalArgumentException("invalid value for field length");
            }
            if (traversalTime != null && traversalTime < 0) {
                throw new IllegalArgumentException("invalid value for field traversal_time");
            }
            if (stairCount != null && stairCount < 0) {
                throw new IllegalArgumentException("invalid value for field stair_count");
            }
            if (minWidth != null && minWidth < 0) {
                throw new IllegalArgumentException("invalid value for field min_width");
            }
            return new Pathway(pathwayId, fromStopId, toStopId, pathwayMode, isBidirectional, length, traversalTime,
                    stairCount, maxSlope, minWidth, signpostedAs, reversedSignpostedAs);
        }
    }
}
