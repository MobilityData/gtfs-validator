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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.pathways;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.EntityBuildResult;
import org.mobilitydata.gtfsvalidator.domain.entity.gtfs.GtfsEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.FloatFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.IntegerFieldValueOutOfRangeNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.UnexpectedEnumValueNotice;

import java.util.List;

public class Pathway extends GtfsEntity {
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
    public Boolean getIsBidirectional() {
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
        private Integer originalPathwayModeInteger;
        private Integer originalIsBiDirectionalInteger;
        private final List<Notice> noticeCollection;

        public PathwayBuilder(List<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
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

        public PathwayBuilder pathwayMode(@NotNull final Integer pathwayMode) {
            this.pathwayMode = PathwayMode.fromInt(pathwayMode);
            this.originalPathwayModeInteger = pathwayMode;
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
            this.originalIsBiDirectionalInteger = isBidirectional;
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

        public EntityBuildResult<?> build() {
            noticeCollection.clear();

            if (pathwayId == null || fromStopId == null || toStopId == null || isBidirectional == null ||
                    (length != null && length < 0) || (traversalTime != null && traversalTime < 0) ||
                    (stairCount != null && stairCount < 0) || (minWidth != null && minWidth < 0) || pathwayMode == null) {

                if (pathwayId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("pathways.txt",
                            "pathway_id", pathwayId));
                }
                if (fromStopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("pathways.txt",
                            "from_stop_id", pathwayId));
                }
                if (toStopId == null) {
                    noticeCollection.add(new MissingRequiredValueNotice("pathways.txt",
                            "to_stop_id", pathwayId));
                }
                if (pathwayMode == null) {
                    if (originalPathwayModeInteger == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("pathways.txt",
                                "pathway_mode", pathwayId));
                    } else {
                        noticeCollection.add(new UnexpectedEnumValueNotice("pathways.txt",
                                "pathway_mode", pathwayId, originalPathwayModeInteger));
                    }
                }
                if (isBidirectional == null) {
                    if (originalPathwayModeInteger == null) {
                        noticeCollection.add(new MissingRequiredValueNotice("pathways.txt",
                                "is_bidirectional", pathwayId));
                    } else {
                        noticeCollection.add(new UnexpectedEnumValueNotice("pathways.txt",
                                "is_bidirectional", pathwayId, originalIsBiDirectionalInteger));
                    }
                }
                if (length != null && length < 0) {
                    noticeCollection.add(new FloatFieldValueOutOfRangeNotice("pathways.txt",
                            "length", pathwayId, 0, Float.MAX_VALUE, length));
                }
                if (traversalTime != null && traversalTime < 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("pathways.txt",
                            "traversal_time", pathwayId, 0, Integer.MAX_VALUE,
                            traversalTime));
                }
                if (stairCount != null && stairCount < 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("pathways.txt",
                            "stair_count", pathwayId, 0, Integer.MAX_VALUE,
                            stairCount));
                }
                if (minWidth != null && minWidth < 0) {
                    noticeCollection.add(new FloatFieldValueOutOfRangeNotice("pathways.txt",
                            "min_width", pathwayId, 0, Float.MAX_VALUE,
                            minWidth));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Pathway(pathwayId, fromStopId, toStopId, pathwayMode, isBidirectional,
                        length, traversalTime, stairCount, maxSlope, minWidth, signpostedAs, reversedSignpostedAs));
            }
        }
    }
}