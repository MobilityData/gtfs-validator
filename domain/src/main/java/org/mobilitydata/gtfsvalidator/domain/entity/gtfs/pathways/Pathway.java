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

import java.util.ArrayList;
import java.util.List;

/**
 * Class for all entities defined in pathways.txt. Can not be directly instantiated: user must use the
 * {@link PathwayBuilder} to create this.
 */
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

    /**
     * @param pathwayId            the pathway_id field contains an ID that uniquely identifies the pathway
     * @param fromStopId           location at which the pathway begins
     * @param toStopId             location at which the pathway ends
     * @param pathwayMode          type of pathway between the specified (@param fromStopId, @param toStopId) pair
     * @param isBidirectional      indicates in which direction the pathway can be used
     * @param length               horizontal length in meters of the pathway from the origin location (defined in
     *                             fromStopId) to the destination location (defined in toStopId)
     * @param traversalTime        average time in seconds needed to walk through the pathway from the origin location
     *                             (defined in @param fromStopId) to the destination location (defined
     *                             toStopId)
     * @param stairCount           number of stairs of the pathway
     * @param maxSlope             maximum slope ratio of the pathway
     * @param minWidth             minimum width of the pathway in meters
     * @param signpostedAs         string of text from physical signage visible to transit riders
     * @param reversedSignpostedAs same than the @param signPostedAs field, but when the pathways is used backward,
     *                             i.e. from the @param toStopId to the @param fromStopId
     */
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

    /**
     * Builder class to create {@link Pathway} objects. Allows an unordered definition of the different attributes of
     * {@link Pathway}.
     */
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
        private Integer originalPathwayModeInteger;
        private Integer originalIsBiDirectionalInteger;
        private final List<Notice> noticeCollection = new ArrayList<>();

        /**
         * Sets field pathwayId value and returns this
         *
         * @param pathwayId the pathway_id field contains an ID that uniquely identifies the pathway
         * @return builder for future object creation
         */
        public PathwayBuilder pathwayId(@NotNull final String pathwayId) {
            this.pathwayId = pathwayId;
            return this;
        }

        /**
         * Sets field fromStopId value and returns this
         *
         * @param fromStopId location at which the pathway begins
         * @return builder for future object creation
         */
        public PathwayBuilder fromStopId(@NotNull final String fromStopId) {
            this.fromStopId = fromStopId;
            return this;
        }

        /**
         * Sets field toStopId value and returns this
         *
         * @param toStopId location at which the pathway ends
         * @return builder for future object creation
         */
        public PathwayBuilder toStopId(@NotNull final String toStopId) {
            this.toStopId = toStopId;
            return this;
        }

        /**
         * Sets field pathwayMode value and returns this
         *
         * @param pathwayMode type of pathway between the specified (@param fromStopId, @param toStopId) pair
         * @return builder for future object creation
         */
        public PathwayBuilder pathwayMode(@NotNull final Integer pathwayMode) {
            this.pathwayMode = PathwayMode.fromInt(pathwayMode);
            this.originalPathwayModeInteger = pathwayMode;
            return this;
        }

        /**
         * Sets field isBidirectional value and returns this
         *
         * @param isBidirectional indicates in which direction the pathway can be used
         * @return builder for future object creation
         */
        public PathwayBuilder isBidirectional(@NotNull Integer isBidirectional) {
            this.isBidirectional = IsBidirectional.fromInt(isBidirectional);
            this.originalIsBiDirectionalInteger = isBidirectional;
            return this;
        }

        /**
         * Sets field length value and returns this
         *
         * @param length horizontal length in meters of the pathway from the origin location to the destination location
         * @return builder for future object creation
         */
        public PathwayBuilder length(@Nullable final Float length) {
            this.length = length;
            return this;
        }

        /**
         * Sets field traversalTime value and returns this
         *
         * @param traversalTime average time in seconds needed to walk through the pathway from the origin to the
         *                      destination
         * @return builder for future object creation
         */
        public PathwayBuilder traversalTime(@Nullable final Integer traversalTime) {
            this.traversalTime = traversalTime;
            return this;
        }

        /**
         * Sets field stairCount value and returns this
         *
         * @param stairCount number of stairs of the pathway
         * @return builder for future object creation
         */
        public PathwayBuilder stairCount(@Nullable final Integer stairCount) {
            this.stairCount = stairCount;
            return this;
        }

        /**
         * Sets field maxSlope value and returns this
         *
         * @param maxSlope maximum slope ratio of the pathway
         * @return builder for future object creation
         */
        public PathwayBuilder maxSlope(@Nullable final Float maxSlope) {
            this.maxSlope = maxSlope;
            return this;
        }

        /**
         * Sets field minWidth value and returns this
         *
         * @param minWidth minimum width of the pathway in meters
         * @return builder for future object creation
         */
        public PathwayBuilder minWidth(@Nullable final Float minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        /**
         * Sets field signpostedAs value and returns this
         *
         * @param signpostedAs string of text from physical signage visible to transit riders
         * @return builder for future object creation
         */
        public PathwayBuilder signpostedAs(@Nullable final String signpostedAs) {
            this.signpostedAs = signpostedAs;
            return this;
        }

        /**
         * Sets field reversedSignpostedAs value and returns this
         *
         * @param reversedSignpostedAs same than the @param signPostedAs field, but when the pathways is used backward
         * @return builder for future object creation
         */
        public PathwayBuilder reversedSignpostedAs(@Nullable final String reversedSignpostedAs) {
            this.reversedSignpostedAs = reversedSignpostedAs;
            return this;
        }

        /**
         * Return a {@code EntityBuildResult} representing a row from pathways.txt if the requirements from the official
         * GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues.
         *
         * @return an {@code EntityBuildResult} representing a row from pathways.txt if the requirements from the
         * official GTFS specification are met. Otherwise, method returns a collection of notices specifying the issues.
         */
        public EntityBuildResult<?> build() {
            if (pathwayId == null ||
                    fromStopId == null ||
                    toStopId == null ||
                    !IsBidirectional.isEnumValueValid(originalIsBiDirectionalInteger) ||
                    isBidirectional == null ||
                    (length != null && length < 0) ||
                    (traversalTime != null && traversalTime < 0) ||
                    (stairCount != null && stairCount == 0) ||
                    (minWidth != null && minWidth < 0) ||
                    pathwayMode == null) {

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
                    if (originalIsBiDirectionalInteger == null) {
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
                if (stairCount != null && stairCount == 0) {
                    noticeCollection.add(new IntegerFieldValueOutOfRangeNotice("pathways.txt",
                            "stair_count", pathwayId, 0, 0,
                            stairCount));
                }
                if (minWidth != null && minWidth < 0) {
                    noticeCollection.add(new FloatFieldValueOutOfRangeNotice("pathways.txt",
                            "min_width", pathwayId, 0, Float.MAX_VALUE,
                            minWidth));
                }
                return new EntityBuildResult<>(noticeCollection);
            } else {
                return new EntityBuildResult<>(new Pathway(pathwayId, fromStopId, toStopId, pathwayMode,
                        isBidirectional, length, traversalTime, stairCount, maxSlope, minWidth, signpostedAs,
                        reversedSignpostedAs));
            }
        }

        /**
         * Method to reset all fields of builder. Returns builder with all fields set to null.
         *
         * @return builder with all fields set to null;
         */
        public PathwayBuilder clear() {
            pathwayId = null;
            fromStopId = null;
            toStopId = null;
            pathwayMode = null;
            isBidirectional = null;
            length = null;
            traversalTime = null;
            stairCount = null;
            maxSlope = null;
            minWidth = null;
            signpostedAs = null;
            reversedSignpostedAs = null;
            originalPathwayModeInteger = null;
            originalIsBiDirectionalInteger = null;
            noticeCollection.clear();
            return this;
        }
    }
}