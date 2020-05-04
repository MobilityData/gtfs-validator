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

package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

/**
 * Class for all entities defined in levels.txt. Can not be directly instantiated: user must use the
 * {@code Level.LevelBuilder} to create this.
 */
public class Level extends GtfsEntity {
    @NotNull
    private final String levelId;
    private final float levelIndex;
    @Nullable
    private final String levelName;

    /**
     * @param levelId    id of the level that can be referenced from stops.txt
     * @param levelIndex numeric index of the level that indicates relative position of this level in relation to other
     *                   levels
     * @param levelName  optional name of the level
     */
    private Level(@NotNull final String levelId,
                  final float levelIndex,
                  @Nullable final String levelName) {
        this.levelId = levelId;
        this.levelIndex = levelIndex;
        this.levelName = levelName;
    }

    @NotNull
    public String getLevelId() {
        return levelId;
    }

    @NotNull
    public Float getLevelIndex() {
        return levelIndex;
    }

    @Nullable
    public String getLevelName() {
        return levelName;
    }

    public static class LevelBuilder {
        private String levelId;
        private Float levelIndex;
        @Nullable
        private String levelName;
        private final List<Notice> noticeCollection;

        public LevelBuilder(final List<Notice> noticeCollection) {
            this.noticeCollection = noticeCollection;
        }

        /**
         * Sets field levelId value and returns this
         *
         * @param levelId id of the level that can be referenced from stops.txt
         * @return builder for future object creation
         */
        public LevelBuilder levelId(@NotNull final String levelId) {
            this.levelId = levelId;
            return this;
        }

        public LevelBuilder levelIndex(final Float levelIndex) {
            this.levelIndex = levelIndex;
            return this;
        }

        public LevelBuilder levelName(@Nullable final String levelName) {
            this.levelName = levelName;
            return this;
        }

        public Level build() {
            if (levelId == null) {
                throw new IllegalArgumentException("field level_id can not be null");
            }
            if (levelIndex == null) {
                throw new IllegalArgumentException("field level_index can not be null");
            }
            return new Level(levelId, levelIndex, levelName);
        }
    }
}