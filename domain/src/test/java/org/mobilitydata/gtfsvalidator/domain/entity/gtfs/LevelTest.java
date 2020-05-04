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
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.MissingRequiredValueNotice;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LevelTest {

    // Field levelId is annotated as `@NonNull` but test require this field to be null. Therefore annotation
    // "@SuppressWarnings("ConstantConditions")" is used here to suppress lint.
    @Test
    public void createLevelWithNullLevelIdShouldGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Level.LevelBuilder underTest = new Level.LevelBuilder(mockNoticeCollection);

        //noinspection ConstantConditions
        underTest.levelId(null)
                .levelIndex(2.0f)
                .levelName("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_id", noticeList.get(0).getFieldName());
        assertEquals("no id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createLevelWithNullLevelIndexShouldGenerateException() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Level.LevelBuilder underTest = new Level.LevelBuilder(mockNoticeCollection);

        underTest.levelId("level id")
                .levelIndex(null)
                .levelName("test");

        final EntityBuildResult<?> entityBuildResult = underTest.build();

        final ArgumentCaptor<MissingRequiredValueNotice> captor =
                ArgumentCaptor.forClass(MissingRequiredValueNotice.class);

        verify(mockNoticeCollection, times(1)).clear();
        verify(mockNoticeCollection, times(1)).add(captor.capture());

        final List<MissingRequiredValueNotice> noticeList = captor.getAllValues();

        assertEquals("levels.txt", noticeList.get(0).getFilename());
        assertEquals("level_index", noticeList.get(0).getFieldName());
        assertEquals("level id", noticeList.get(0).getEntityId());

        assertTrue(entityBuildResult.getData() instanceof List);
        verifyNoMoreInteractions(mockNoticeCollection);
    }

    @Test
    public void createLevelWithValidValuesShouldNotGenerateNotice() {
        @SuppressWarnings("unchecked") final List<Notice> mockNoticeCollection = mock(List.class);
        final Level.LevelBuilder underTest = new Level.LevelBuilder(mockNoticeCollection);

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