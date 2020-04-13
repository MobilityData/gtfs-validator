package org.mobilitydata.gtfsvalidator.domain.entity.gtfs;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelTest {

    @Test
    public void createLevelWithNullLevelIdShouldThrowException() {
        Level.LevelBuilder underTest = new Level.LevelBuilder();

        //noinspection ConstantConditions
        underTest.levelId(null)
                .levelIndex(2.0f)
                .levelName("test");

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field level_id can not be null", exception.getMessage());
    }

    @Test
    public void createLevelWithNullLevelIndexShouldThrowException() {
        Level.LevelBuilder underTest = new Level.LevelBuilder();

        underTest.levelId("test")
                .levelIndex(null)
                .levelName("test");

        Exception exception = assertThrows(IllegalArgumentException.class,
                underTest::build);

        assertEquals("field level_index can not be null", exception.getMessage());
    }

    @Test
    public void createLevelWithValidValuesShouldNotThrowException() {
        Level.LevelBuilder underTest = new Level.LevelBuilder();

        underTest.levelId("test")
                .levelIndex(2.0f)
                .levelName("test");

        Level toCheck = underTest.build();
        assertEquals("test", toCheck.getLevelId());
        assertEquals(2.0f, toCheck.getLevelIndex());
        assertEquals("test", toCheck.getLevelId());
    }
}