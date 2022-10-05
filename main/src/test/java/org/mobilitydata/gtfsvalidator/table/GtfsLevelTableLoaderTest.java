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

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth8.assertThat;

import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.testing.LoadingHelper;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/** Runs GtfsLevelTableContainer on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsLevelTableLoaderTest {

  private LoadingHelper helper = new LoadingHelper();

  @Test
  public void validFile() throws ValidatorLoaderException {
    GtfsLevelTableContainer tableContainer =
        helper.load(
            new GtfsLevelTableDescriptor(), "level_id,level_name,level_index", "level1,Ground,1");

    assertThat(helper.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    Optional<GtfsLevel> optionalLevel = tableContainer.byLevelId("level1");
    assertThat(optionalLevel).isPresent();
    GtfsLevel level = optionalLevel.get();
    assertThat(level.levelId()).isEqualTo("level1");
    assertThat(level.levelName()).isEqualTo("Ground");
    assertThat(level.levelIndex()).isEqualTo(1);
  }

  @Test
  public void missingRequiredField() throws ValidatorLoaderException {
    GtfsLevelTableContainer tableContainer =
        helper.load(new GtfsLevelTableDescriptor(), "level_id,level_name,level_index", ",Ground,1");

    assertThat(helper.getValidationNotices()).isNotEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(0);
  }

  @Test
  public void emptyFile() throws ValidatorLoaderException {
    helper.load(new GtfsLevelTableDescriptor());

    assertThat(helper.getValidationNotices()).isNotEmpty();
    assertThat(helper.getValidationNotices().get(0).getClass().getSimpleName())
        .isEqualTo("EmptyFileNotice");
  }
}
