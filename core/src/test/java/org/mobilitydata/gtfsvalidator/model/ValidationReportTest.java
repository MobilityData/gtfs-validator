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

package org.mobilitydata.gtfsvalidator.model;

import static com.google.common.truth.Truth.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ValidationReportTest {
  @Rule public final TemporaryFolder tmpDir = new TemporaryFolder();

  private ValidationReport createValidationReportFromPath(String filename, String jsonString)
      throws IOException {
    File validationReportFile = tmpDir.newFile(filename);
    Files.write(validationReportFile.toPath(), jsonString.getBytes(StandardCharsets.UTF_8));
    return ValidationReport.fromPath(validationReportFile.toPath());
  }

  @Test
  public void equals_sameReports_true() throws IOException {
    assertThat(
            createValidationReportFromPath(
                "first_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"))
        .isEqualTo(
            createValidationReportFromPath(
                "other_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"));
  }

  @Test
  public void equals_differentFieldsOrder_true() throws IOException {
    assertThat(
            createValidationReportFromPath(
                "first_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"))
        .isEqualTo(
            createValidationReportFromPath(
                "other_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"));
  }

  @Test
  public void equals_differentReports_false() throws IOException {
    assertThat(
            createValidationReportFromPath(
                "first_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"))
        .isNotEqualTo(
            createValidationReportFromPath(
                "other_report.json",
                "{\n"
                    + "  \"notices\": [\n"
                    + "    {\n"
                    + "      \"code\": \"invalid_url\",\n"
                    + "      \"severity\": \"ERROR\",\n"
                    + "      \"totalNotices\": 1,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"stops.txt\",\n"
                    + "          \"csvRowNumber\": 163,\n"
                    + "          \"fieldName\": \"stop_url\",\n"
                    + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    },\n"
                    + "    {\n"
                    + "      \"code\": \"unknown_column\",\n"
                    + "      \"severity\": \"INFO\",\n"
                    + "      \"totalNotices\": 2,\n"
                    + "      \"sampleNotices\": [\n"
                    + "        {\n"
                    + "          \"filename\": \"trips.txt\",\n"
                    + "          \"fieldName\": \"note_fr\",\n"
                    + "          \"index\": 8\n"
                    + "        }\n"
                    + "      ]\n"
                    + "    }\n"
                    + "  ]\n"
                    + "}"));
  }

  @Test
  public void getErrorCodes_errorsInReport_nonEmptySet() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"code\": \"unknown_column\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 4,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}")
                .getErrorCodes())
        .containsExactly("invalid_url", "unknown_column");
  }

  @Test
  public void getErrorCode_emptyReport_emptySet() throws IOException {
    assertThat(
            createValidationReportFromPath("report.json", "{\n" + "  \"notices\": []\n" + "}")
                .getErrorCodes())
        .isEmpty();
  }

  @Test
  public void getErrorCodes_noError_emptySet() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"INFO\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"code\": \"unknown_column\",\n"
                        + "      \"severity\": \"WARNING\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}")
                .getErrorCodes())
        .isEmpty();
  }

  @Test
  public void hasSameErrorCodes_sameErrorsInReports_true() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "first_report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"code\": \"unknown_column\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}")
                .hasSameErrorCodes(
                    createValidationReportFromPath(
                        "other_report.json",
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"stops.txt\",\n"
                            + "          \"csvRowNumber\": 163,\n"
                            + "          \"fieldName\": \"stop_url\",\n"
                            + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    },\n"
                            + "    {\n"
                            + "      \"code\": \"unknown_column\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"trips.txt\",\n"
                            + "          \"fieldName\": \"note_fr\",\n"
                            + "          \"index\": 8\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }\n"
                            + "  ]\n"
                            + "}")))
        .isTrue();
  }

  @Test
  public void hasSameErrorCodes_differentErrorsInReport_false() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "first_report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    },\n"
                        + "    {\n"
                        + "      \"code\": \"unknown_column\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}")
                .hasSameErrorCodes(
                    createValidationReportFromPath(
                        "other_report.json",
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"stops.txt\",\n"
                            + "          \"csvRowNumber\": 163,\n"
                            + "          \"fieldName\": \"stop_url\",\n"
                            + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }"
                            + "  ]\n"
                            + "}")))
        .isFalse();
  }

  @Test
  public void newErrorCount_sameErrorsInReports_zero() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "first_report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 2,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }"
                        + "  ]\n"
                        + "}")
                .getNewErrorsListing(
                    createValidationReportFromPath(
                        "other_report.json",
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"stops.txt\",\n"
                            + "          \"csvRowNumber\": 163,\n"
                            + "          \"fieldName\": \"stop_url\",\n"
                            + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }"
                            + "  ]\n"
                            + "}"))
                .size())
        .isEqualTo(0);
  }

  @Test
  public void newErrorCount_noNewErrorInReport_zero() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "first_report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }"
                        + "  ]\n"
                        + "}")
                .getNewErrorsListing(
                    createValidationReportFromPath(
                        "other_report.json",
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"some_error_code\",\n"
                            + "      \"severity\": \"INFO\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"stops.txt\",\n"
                            + "          \"csvRowNumber\": 163,\n"
                            + "          \"fieldName\": \"stop_url\",\n"
                            + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }"
                            + "  ]\n"
                            + "}"))
                .size())
        .isEqualTo(0);
  }

  @Test
  public void newErrorCount_twoNewErrorsInNewReport_two() throws IOException {
    assertThat(
            createValidationReportFromPath(
                    "first_report.json",
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"sampleNotices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"stops.txt\",\n"
                        + "          \"csvRowNumber\": 163,\n"
                        + "          \"fieldName\": \"stop_url\",\n"
                        + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }"
                        + "  ]\n"
                        + "}")
                .getNewErrorsListing(
                    createValidationReportFromPath(
                        "other_report.json",
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"stops.txt\",\n"
                            + "          \"csvRowNumber\": 163,\n"
                            + "          \"fieldName\": \"stop_url\",\n"
                            + "          \"fieldValue\": \"http://www.stm.info/fr/infos/reseaux/metro/square-victoria–oaci\"\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    },\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"trips.txt\",\n"
                            + "          \"fieldName\": \"note_fr\",\n"
                            + "          \"index\": 8\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    },\n"
                            + "    {\n"
                            + "      \"code\": \"some_error_code\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"trips.txt\",\n"
                            + "          \"fieldName\": \"note_fr\",\n"
                            + "          \"index\": 8\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    },\n"
                            + "    {\n"
                            + "      \"code\": \"another_error_code\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"sampleNotices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"trips.txt\",\n"
                            + "          \"fieldName\": \"note_fr\",\n"
                            + "          \"index\": 8\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }\n"
                            + "  ]\n"
                            + "}"))
                .size())
        .isEqualTo(2);
  }
}
