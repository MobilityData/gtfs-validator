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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mobilitydata.gtfsvalidator.input.CountryCode;
import org.mobilitydata.gtfsvalidator.input.CurrentDateTime;
import org.mobilitydata.gtfsvalidator.notice.NoticeContainer;
import org.mobilitydata.gtfsvalidator.validator.DefaultValidatorProvider;
import org.mobilitydata.gtfsvalidator.validator.ValidationContext;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoader;
import org.mobilitydata.gtfsvalidator.validator.ValidatorLoaderException;

/** Runs GtfsLevelTableContainer on test CSV data. */
@RunWith(JUnit4.class)
public class GtfsLevelTableLoaderTest {
  private static final CountryCode TEST_COUNTRY_CODE = CountryCode.forStringOrUnknown("au");
  private static final ZonedDateTime TEST_NOW =
      ZonedDateTime.of(2021, 1, 1, 14, 30, 0, 0, ZoneOffset.UTC);

  private static final ValidationContext VALIDATION_CONTEXT =
      ValidationContext.builder()
          .setCountryCode(TEST_COUNTRY_CODE)
          .setCurrentDateTime(new CurrentDateTime(TEST_NOW))
          .build();

  private static InputStream toInputStream(String s) {
    return new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
  }

  private static GtfsLevelTableContainer load(
      InputStream inputStream, NoticeContainer noticeContainer) throws ValidatorLoaderException {
    ValidatorLoader validatorLoader = new ValidatorLoader();
    GtfsLevelTableLoader loader = new GtfsLevelTableLoader();
    return (GtfsLevelTableContainer)
        loader.load(
            inputStream,
            new DefaultValidatorProvider(VALIDATION_CONTEXT, validatorLoader),
            noticeContainer);
  }

  @Test
  public void validFile() throws IOException, ValidatorLoaderException {
    InputStream inputStream =
        toInputStream("level_id,level_name,level_index\n" + "level1,Ground,1\n");
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLevelTableContainer tableContainer = load(inputStream, noticeContainer);

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
  public void missingRequiredField() throws IOException, ValidatorLoaderException {
    InputStream inputStream = toInputStream("level_id,level_name,level_index\n" + ",Ground,1\n");
    NoticeContainer noticeContainer = new NoticeContainer();
    GtfsLevelTableContainer tableContainer = load(inputStream, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
    assertThat(tableContainer.entityCount()).isEqualTo(0);
    inputStream.close();
  }

  @Test
  public void emptyFile() throws IOException, ValidatorLoaderException {
    InputStream inputStream = toInputStream("");
    NoticeContainer noticeContainer = new NoticeContainer();
    load(inputStream, noticeContainer);

    assertThat(noticeContainer.getValidationNotices()).isNotEmpty();
    assertThat(noticeContainer.getValidationNotices().get(0).getClass().getSimpleName())
        .isEqualTo("EmptyFileNotice");
    inputStream.close();
  }
}
