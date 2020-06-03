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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LevelTest {

    @Test
    public void createLevelWithNullLevelIdShouldGenerateNotice() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();
        // Field levelId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
        // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.levelId(null)
                .levelIndex(2.0f)
                .levelName("test")
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("levels.txt", notice.getFilename());
        assertEquals("level_id", notice.getFieldName());
        assertEquals("no id", notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createLevelWithNullLevelIndexShouldGenerateException() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();
        // Field levelIndex is annotated as `@NonNull` but test require this field to be null. Therefore annotation
        // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
        //noinspection ConstantConditions
        final EntityBuildResult<?> entityBuildResult = underTest.levelId("level id")
                .levelIndex(null)
                .levelName("test")
                .build();

        assertTrue(entityBuildResult.getData() instanceof List);
        // suppressed lint regarding cast. The test is designed so that .getData() returns a list of notices, therefore
        // we do not need to cast check
        //noinspection unchecked
        final List<MissingRequiredValueNotice> noticeCollection =
                (List<MissingRequiredValueNotice>) entityBuildResult.getData();
        final MissingRequiredValueNotice notice = noticeCollection.get(0);

        assertEquals("levels.txt", notice.getFilename());
        assertEquals("level_index", notice.getFieldName());
        assertEquals("level id", notice.getEntityId());

        assertEquals(1, noticeCollection.size());
    }

    @Test
    public void createLevelWithValidValuesShouldNotGenerateNotice() {
        final Level.LevelBuilder underTest = new Level.LevelBuilder();

        underTest.levelId("level id")
                .levelIndex(2.0f)
                .levelName("level name");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        assertTrue(entityBuildResult.getData() instanceof Level);

        final Level toCheck = (Level) entityBuildResult.getData();

        assertEquals("level id", toCheck.getLevelId());
        assertEquals(2.0f, toCheck.getLevelIndex());
        assertEquals("level name", toCheck.getLevelName());
    }
}