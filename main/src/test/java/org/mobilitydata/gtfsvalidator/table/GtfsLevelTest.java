/*
 * Copyright 2020 Google LLC, MobilityData IO
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.table;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static com.google.common.truth.Truth.assertThat;
import static org.mobilitydata.gtfsvalidator.table.GtfsLevel.*;

@RunWith(JUnit4.class)
public class GtfsLevelTest {
    @Test
    public void shouldReturnFieldValues() {
        GtfsLevel.Builder builder = new GtfsLevel.Builder();
        GtfsLevel underTest = builder
                .setLevelId("level id")
                .setLevelIndex(2d)
                .setLevelName("level name")
                .build();

        assertThat(underTest.levelId()).isEqualTo("level id");
        assertThat(underTest.levelIndex()).isEqualTo(2d);
        assertThat(underTest.levelName()).isEqualTo("level name");

        assertThat(underTest.hasLevelId()).isTrue();
        assertThat(underTest.hasLevelIndex()).isTrue();
        assertThat(underTest.hasLevelName()).isTrue();
    }

    @Test
    public void shouldReturnDefaultValuesForMissingValues() {
        GtfsLevel.Builder builder = new GtfsLevel.Builder();
        GtfsLevel underTest = builder
                .setLevelId(null)
                .setLevelIndex(null)
                .setLevelName(null)
                .build();

        assertThat(underTest.levelId()).isEqualTo(DEFAULT_LEVEL_ID);
        assertThat(underTest.levelIndex()).isEqualTo(DEFAULT_LEVEL_INDEX);
        assertThat(underTest.levelName()).isEqualTo(DEFAULT_LEVEL_NAME);

        assertThat(underTest.hasLevelId()).isFalse();
        assertThat(underTest.hasLevelIndex()).isFalse();
        assertThat(underTest.hasLevelName()).isFalse();
    }

    @Test
    public void shouldResetFieldToDefaultValues() {
        GtfsLevel.Builder builder = new GtfsLevel.Builder();
        builder.setLevelId("level id")
                .setLevelIndex(2d)
                .setLevelName("level name");
        builder.clear();
        GtfsLevel underTest = builder.build();

        assertThat(underTest.levelId()).isEqualTo(DEFAULT_LEVEL_ID);
        assertThat(underTest.levelIndex()).isEqualTo(DEFAULT_LEVEL_INDEX);
        assertThat(underTest.levelName()).isEqualTo(DEFAULT_LEVEL_NAME);

        assertThat(underTest.hasLevelId()).isFalse();
        assertThat(underTest.hasLevelIndex()).isFalse();
        assertThat(underTest.hasLevelName()).isFalse();
    }

    @Test
    public void fieldValuesNotSetShouldBeNull() {
        GtfsLevel.Builder builder = new GtfsLevel.Builder();
        GtfsLevel underTest = builder.build();

        assertThat(underTest.levelId()).isNull();
        assertThat(underTest.levelIndex()).isEqualTo(DEFAULT_LEVEL_INDEX);
        assertThat(underTest.levelName()).isNull();

        assertThat(underTest.hasLevelId()).isFalse();
        assertThat(underTest.hasLevelIndex()).isFalse();
        assertThat(underTest.hasLevelName()).isFalse();
    }
}
