package org.mobilitydata.gtfsvalidator.domain.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Level {

    @NotNull
    private final String levelId;

    @NotNull
    private final Float levelIndex;
    private final String levelName;

    public Level(@NotNull String levelId, @NotNull Float levelIndex, String levelName) {
        this.levelId = levelId;
        this.levelIndex = levelIndex;
        this.levelName = levelName;
    }

    @NotNull
    public String getLevelId() {
        return levelId;
    }

    public float getLevelIndex() {
        return levelIndex;
    }

    public String getLevelName() {
        return levelName;
    }

    @SuppressWarnings("CanBeFinal")
    public static class LevelBuilder {
        private String levelId;
        private Float levelIndex;
        private String levelName;

        LevelBuilder(@NotNull String levelId, @NotNull Float levelIndex) {
            this.levelId = levelId;
            this.levelIndex = levelIndex;
        }

        public LevelBuilder levelName(@Nullable String levelName) {
            this.levelName = levelName;
            return this;
        }

        public Level build() {
            return new Level(levelId, levelIndex, levelName);
        }
    }
}