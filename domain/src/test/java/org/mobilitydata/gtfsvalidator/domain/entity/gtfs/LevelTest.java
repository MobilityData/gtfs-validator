package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelTest {

    @Test
    public void createLevelWithNullLevelIdShouldThrowException() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();

        //noinspection ConstantConditions
        underTest.levelId(null)
                .levelIndex(2.0f)
                .levelName("test");

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field level_id can not be null", exception.getMessage());
    }

    @Test
    public void createLevelWithNullLevelIndexShouldThrowException() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();

        underTest.levelId("test")
                .levelIndex(null)
                .levelName("test");

        final Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field level_index can not be null", exception.getMessage());
    }

    @Test
    public void createLevelWithValidValuesShouldNotThrowException() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();

        underTest.levelId("test")
                .levelIndex(2.0f)
                .levelName("test");

        final Level toCheck = underTest.build();
        assertEquals("test", toCheck.getLevelId());
        assertEquals(2.0f, toCheck.getLevelIndex());
        assertEquals("test", toCheck.getLevelId());
    }
}