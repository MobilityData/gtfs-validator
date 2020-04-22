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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LevelTest {

    // Field levelId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
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