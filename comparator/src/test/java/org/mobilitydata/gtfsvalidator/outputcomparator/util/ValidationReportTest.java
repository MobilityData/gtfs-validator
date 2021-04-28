/*
 * Copyright 2020 MobilityData IO
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

package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import static com.google.common.truth.Truth.assertThat;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidationReportTest {
  private static final Gson GSON = new Gson();

  private static ValidationReport createValidationReportFromJsonString(String jsonString) {
    return GSON.fromJson(jsonString, ValidationReport.class);
  }

  private static boolean testEquality(
      ValidationReport firstValidationReport, ValidationReport otherValidationReport) {
    return firstValidationReport.equals(otherValidationReport);
  }

  @Test
  public void equals_sameReports_true() {
    assertThat(
            testEquality(
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}"),
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
  public void equals_differentFieldsOrder_true() {
    assertThat(
            testEquality(
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}"),
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"totalNotices\": 1,\n"
                        + "      \"severity\": \"INFO\",\n"
                        + "      \"notices\": [\n"
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
  public void equals_differentReports_false() {
    assertThat(
            testEquality(
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}"),
                createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
                        + "        {\n"
                        + "          \"filename\": \"trips.txt\",\n"
                        + "          \"fieldName\": \"note_fr\",\n"
                        + "          \"index\": 8\n"
                        + "        }\n"
                        + "      ]\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}")))
        .isFalse();
  }

  @Test
  public void getErrorEntries_returns_nonEmptySet() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
  public void getErrorEntries_returns_emptySet() {
    assertThat(
            createValidationReportFromJsonString("{\n" + "  \"notices\": []\n" + "}")
                .getErrorCodes())
        .isEmpty();
  }

  @Test
  public void getErrorCodes_noError_emptySet() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"INFO\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
  public void getErrorCodes_emptyReport_emptySet() {
    assertThat(
            createValidationReportFromJsonString("{\n" + "  \"notices\": []\n" + "}")
                .getErrorCodes())
        .isEmpty();
  }

  @Test
  public void getErrorCodes_errorsInReport_nonEmptySet() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
  public void hasSameErrorCodes_sameErrorsInReports_true() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
                    createValidationReportFromJsonString(
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"notices\": [\n"
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
                            + "      \"notices\": [\n"
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
  public void hasSameErrorCodes_differentErrorsInReport_false() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                        + "      \"notices\": [\n"
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
                    createValidationReportFromJsonString(
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"notices\": [\n"
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
  public void getNewErrorCount_sameErrorsInReports_zero() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 2,\n"
                        + "      \"notices\": [\n"
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
                .getNewErrorCount(
                    createValidationReportFromJsonString(
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"notices\": [\n"
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
        .isEqualTo(0);
  }

  @Test
  public void getNewErrorCount_noNewErrorInReport_zero() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                .getNewErrorCount(
                    createValidationReportFromJsonString(
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"some_error_code\",\n"
                            + "      \"severity\": \"INFO\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"notices\": [\n"
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
        .isEqualTo(0);
  }

  @Test
  public void getNewErrorCount_twoNewErrorsInNewReport_two() {
    assertThat(
            createValidationReportFromJsonString(
                    "{\n"
                        + "  \"notices\": [\n"
                        + "    {\n"
                        + "      \"code\": \"invalid_url\",\n"
                        + "      \"severity\": \"ERROR\",\n"
                        + "      \"totalNotices\": 1,\n"
                        + "      \"notices\": [\n"
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
                .getNewErrorCount(
                    createValidationReportFromJsonString(
                        "{\n"
                            + "  \"notices\": [\n"
                            + "    {\n"
                            + "      \"code\": \"invalid_url\",\n"
                            + "      \"severity\": \"ERROR\",\n"
                            + "      \"totalNotices\": 1,\n"
                            + "      \"notices\": [\n"
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
                            + "      \"notices\": [\n"
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
                            + "      \"notices\": [\n"
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
                            + "      \"notices\": [\n"
                            + "        {\n"
                            + "          \"filename\": \"trips.txt\",\n"
                            + "          \"fieldName\": \"note_fr\",\n"
                            + "          \"index\": 8\n"
                            + "        }\n"
                            + "      ]\n"
                            + "    }\n"
                            + "  ]\n"
                            + "}")))
        .isEqualTo(2);
  }
}
