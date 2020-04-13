package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Level {

    @NotNull
    private final String levelId;

    private final float levelIndex;

    @Nullable
    private final String levelName;

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