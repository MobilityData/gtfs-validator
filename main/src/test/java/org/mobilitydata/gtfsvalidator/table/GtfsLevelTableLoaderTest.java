/*
 * Copyright 2020 Google LLC
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.GtfsFeedName;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;

/** Runs GtfsLevelTableContainer on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsLevelTableLoaderTest {
  private static final GtfsFeedName FEED_NAME = GtfsFeedName.parseString("au-sydney-buses");

  private static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  @Test
  public void validFile() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    InputStream inputStream =
        toInputStream("level_id,level_name,level_index\n" + "level1,Ground,1\n");
    GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLevelTableContainer tableContainer =
        (GtfsLevelTableContainer)
            loader.load(inputStream, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(1);
    GtfsLevel level = tableContainer.byLevelId("level1");
    assertThat(level).isNotNull();
    assertThat(level.levelId()).isEqualTo("level1");
    assertThat(level.levelName()).isEqualTo("Ground");
    assertThat(level.levelIndex()).isEqualTo(1);

    inputStream.close();
  }

  @Test
  public void missingRequiredField() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    InputStream inputStream = toInputStream("level_id,level_name,level_index\n" + ",Ground,1\n");
    GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLevelTableContainer tableContainer =
        (GtfsLevelTableContainer)
            loader.load(inputStream, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(0);
    inputStream.close();
  }

  @Test
  public void emptyFile() throws IOException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    InputStream inputStream = toInputStream("");
    GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLevelTableContainer tableContainer =
        (GtfsLevelTableContainer)
            loader.load(inputStream, FEED_NAME, validatorLoader, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
    assertThat(noticeContainer.getValidationNotices().get(0).getClass().getSimpleName())
        .isEqualTo("EmptyFileNotice");
    inputStream.close();
  }
}
