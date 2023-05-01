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

package org.mobilitydata.gtfsvalidator.notice.schema;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.DuplicateKeyNotice;
import org.mobilitydata.gtfsvalidator.notice.ForeignKeyViolationNotice;
import org.mobilitydata.gtfsvalidator.notice.schema.ReferencesSchema.UrlReference;
import org.mobilitydata.gtfsvalidator.notice.testnotices.DocumentedNotice;
import org.mobilitydata.gtfsvalidator.notice.testnotices.S2LatLngNotice;

public class NoticeSchemaGeneratorTest {
  private static final String OPEN_SOURCE_NOTICES_PACKAGE = "org.mobilitydata.gtfsvalidator";
  private static final String TEST_NOTICES_PACKAGE =
      "org.mobilitydata.gtfsvalidator.notice.testnotices";

  @Test
  public void jsonSchemaForPackages_succeeds() throws IOException {
    assertThat(
            NoticeSchemaGenerator.generateSchemasForNoticesInPackages(
                ImmutableList.of(OPEN_SOURCE_NOTICES_PACKAGE)))
        .isNotNull();
  }

  @Test
  public void generateSchemasForNoticesInPackages_testNotices() throws IOException {
    Map<String, NoticeSchema> schemas =
        NoticeSchemaGenerator.generateSchemasForNoticesInPackages(
            ImmutableList.of(TEST_NOTICES_PACKAGE));
    assertThat(schemas.keySet())
        .containsExactly(
            "documented",
            "double_field",
            "test_inner",
            "gtfs_types_validation",
            "s2_lat_lng",
            "string_field");
  }

  @Test
  public void generateSchemaForNotice_foreignKeyViolationNotice() {
    NoticeSchema schema =
        NoticeSchemaGenerator.generateSchemaForNotice(ForeignKeyViolationNotice.class);
    assertThat(schema.getFields())
        .containsExactly(
            "childFieldName",
            new FieldSchema(
                FieldTypeSchema.STRING,
                "childFieldName",
                "The name of the field that makes reference."),
            "childFilename",
            new FieldSchema(
                FieldTypeSchema.STRING,
                "childFilename",
                "The name of the file from which reference is made."),
            "csvRowNumber",
            new FieldSchema(
                FieldTypeSchema.INTEGER, "csvRowNumber", "The row of the faulty record."),
            "fieldValue",
            new FieldSchema(FieldTypeSchema.STRING, "fieldValue", "The faulty record's value."),
            "parentFieldName",
            new FieldSchema(
                FieldTypeSchema.STRING,
                "parentFieldName",
                "The name of the field that is referred to."),
            "parentFilename",
            new FieldSchema(
                FieldTypeSchema.STRING,
                "parentFilename",
                "The name of the file that is referred to."));
  }

  @Test
  public void generateSchemaForNotice_documentedNotice() {
    NoticeSchema schema = NoticeSchemaGenerator.generateSchemaForNotice(DocumentedNotice.class);
    assertThat(schema.getDescription()).isEqualTo("This is a notice comment.");
    ReferencesSchema refs = schema.getReferences();
    assertThat(refs.getFileReferences()).containsExactly("apples.txt");
    assertThat(refs.getBestPracticesFileReferences()).containsExactly("bananas.txt");
    assertThat(refs.getUrlReferences())
        .containsExactly(new UrlReference("Coconuts", "http://coconuts.com"));
    assertThat(schema.getFields().get("value").getDescription()).isEqualTo("A field comment");
  }

  @Test
  public void generateJsonSchemaForNotice_duplicateKeyNotice() throws IOException {
    NoticeSchema schema = NoticeSchemaGenerator.generateSchemaForNotice(DuplicateKeyNotice.class);
    String actualJson = new GsonBuilder().setPrettyPrinting().create().toJson(schema);
    String expectedJson = retrieveResource("generateJsonSchemaForNotice_duplicateKeyNotice.json");
    assertThat(actualJson).isEqualTo(expectedJson);
  }

  @Test
  public void generateJsonSchemaForNotice_s2LatLngNotice() throws IOException {
    NoticeSchema schema = NoticeSchemaGenerator.generateSchemaForNotice(S2LatLngNotice.class);
    String actualJson = new GsonBuilder().setPrettyPrinting().create().toJson(schema);
    String expectedJson = retrieveResource("generateJsonSchemaForNotice_s2LatLngNotice.json");
    assertThat(actualJson).isEqualTo(expectedJson);
  }

  private static String retrieveResource(String name) throws IOException {
    try (InputStream in =
        NoticeSchemaGeneratorTest.class.getResourceAsStream(
            NoticeSchemaGeneratorTest.class.getSimpleName() + "-" + name)) {
      String content = new String(in.readAllBytes(), StandardCharsets.UTF_8);
      // On Windows systems, resource files will be checked out by Git with Windows line separators.
      // However, all our tests assume Unix line separators.  So we fix up the content here.
      return content.replace(System.lineSeparator(), "\n");
    }
  }
}
