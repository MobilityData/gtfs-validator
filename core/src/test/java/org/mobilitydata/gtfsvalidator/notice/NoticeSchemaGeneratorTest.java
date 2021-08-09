/*
 * Copyright 2021 Google LLC
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

package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.io.IOException;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DoubleFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.GtfsTypesValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.StringFieldNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.TestValidator.TestInnerNotice;
import org.mobilitydata.gtfsvalidator.type.GtfsColor;
import org.mobilitydata.gtfsvalidator.type.GtfsDate;
import org.mobilitydata.gtfsvalidator.type.GtfsTime;

public class NoticeSchemaGeneratorTest {
  private static final String OPEN_SOURCE_NOTICES_PACKAGE = "org.mobilitydata.gtfsvalidator";
  private static final String TEST_NOTICES_PACKAGE =
      "org.mobilitydata.gtfsvalidator.notice.testnotices";

  @Test
  public void findNoticeSubclasses() throws IOException {
    assertThat(NoticeSchemaGenerator.findNoticeSubclasses(ImmutableList.of(TEST_NOTICES_PACKAGE)))
        .containsExactly(
            DoubleFieldNotice.class,
            TestInnerNotice.class,
            GtfsTypesValidationNotice.class,
            StringFieldNotice.class);
  }

  @Test
  public void jsonSchemaForPackages_succeeds() throws IOException {
    assertThat(
            NoticeSchemaGenerator.jsonSchemaForPackages(
                ImmutableList.of(OPEN_SOURCE_NOTICES_PACKAGE)))
        .isNotNull();
  }

  @Test
  public void contextFieldsInPackages_testNotices() throws IOException {
    assertThat(
            NoticeSchemaGenerator.contextFieldsInPackages(ImmutableList.of(TEST_NOTICES_PACKAGE)))
        .isEqualTo(
            ImmutableMap.of(
                "DoubleFieldNotice",
                ImmutableMap.of("doubleField", double.class),
                "TestInnerNotice",
                ImmutableMap.of("intField", int.class),
                "GtfsTypesValidationNotice",
                ImmutableMap.of(
                    "color", GtfsColor.class, "date", GtfsDate.class, "time", GtfsTime.class),
                "StringFieldNotice",
                ImmutableMap.of("someField", String.class)));
  }

  @Test
  public void contextFieldsForNotice_foreignKeyViolationNotice() {
    assertThat(NoticeSchemaGenerator.contextFieldsForNotice(ForeignKeyViolationNotice.class))
        .containsExactly(
            "childFieldName",
            String.class,
            "childFilename",
            String.class,
            "csvRowNumber",
            long.class,
            "fieldValue",
            String.class,
            "parentFieldName",
            String.class,
            "parentFilename",
            String.class);
  }

  @Test
  public void jsonSchemaForNotice_foreignKeyViolationNotice() {
    JsonElement expected =
        new Gson()
            .toJsonTree(
                ImmutableMap.of(
                    "type",
                    "object",
                    "properties",
                    new ImmutableMap.Builder<String, Object>()
                        .put("fieldName1", ImmutableMap.of("type", "string"))
                        .put("fieldName2", ImmutableMap.of("type", "string"))
                        .put(
                            "fieldValue1",
                            ImmutableMap.of(
                                "type", ImmutableList.of("string", "integer", "number")))
                        .put(
                            "fieldValue2",
                            ImmutableMap.of(
                                "type", ImmutableList.of("string", "integer", "number")))
                        .put("filename", ImmutableMap.of("type", "string"))
                        .put("newCsvRowNumber", ImmutableMap.of("type", "integer"))
                        .put("oldCsvRowNumber", ImmutableMap.of("type", "integer"))
                        .build()));

    assertThat(
            NoticeSchemaGenerator.jsonSchemaForNotice(
                "DuplicateKeyNotice",
                NoticeSchemaGenerator.contextFieldsForNotice(DuplicateKeyNotice.class)))
        .isEqualTo(expected);
  }
}
