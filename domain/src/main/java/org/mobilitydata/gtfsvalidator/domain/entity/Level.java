package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Level {

    @NotNull
    private final String levelId;

    @NotNull
    private final Float levelIndex;

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
        @NotNull
        private String levelId;

        private float levelIndex;

        @Nullable
        private String levelName;

        LevelBuilder(@NotNull String levelId, float levelIndex) {
            this.levelId = levelId;
            this.levelIndex = levelIndex;
        }

        public LevelBuilder levelId(@NotNull final String levelId) {
            this.levelId = levelId;
            return this;
        }

        public LevelBuilder levelIndex(final float levelIndex) {
            this.levelIndex = levelIndex;
            return this;
        }

        public LevelBuilder levelName(@Nullable final String levelName) {
            this.levelName = levelName;
            return this;
        }

        public Level build() {
            return new Level(levelId, levelIndex, levelName);
        }
    }
}