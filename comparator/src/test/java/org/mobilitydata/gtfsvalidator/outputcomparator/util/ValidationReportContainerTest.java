package org.mobilitydata.gtfsvalidator.outputcomparator.util;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ValidationReportContainerTest {
  private static ValidationReportContainer createValidationReportContainerFromJsonString(
      String jsonString) {
    return ValidationReportContainer.fromJsonString(jsonString);
  }

  private static void validateErrorCount(
      ValidationReportContainer referenceReport,
      ValidationReportContainer otherReport,
      int expected) {
    assertThat(referenceReport.getNewErrorCount(otherReport)).isEqualTo(expected);
  }

  private static void compareErrorCodes(
      ValidationReportContainer referenceReport,
      ValidationReportContainer otherReport,
      boolean expected) {
    assertThat(referenceReport.hasSameErrorCodes(otherReport)).isEqualTo(expected);
  }

  @Test
  public void getNewErrorCount_sameErrorsInValidationReports_zero() {
    validateErrorCount(
        createValidationReportContainerFromJsonString(
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
                + "}"),
        createValidationReportContainerFromJsonString(
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
                + "}"),
        0);
  }

  @Test
  public void getNewErrorCount_noNewErrorInReport_zero() {
    validateErrorCount(
        createValidationReportContainerFromJsonString(
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
                + "}"),
        createValidationReportContainerFromJsonString(
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
                + "}"),
        0);
  }

  @Test
  public void getNewErrorCount_twoNewErrorsInNewReport_two() {
    validateErrorCount(
        createValidationReportContainerFromJsonString(
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
                + "}"),
        createValidationReportContainerFromJsonString(
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
                + "}"),
        2);
  }

  @Test
  public void getErrorCodes_noError_emptySet() {
    assertThat(
            createValidationReportContainerFromJsonString(
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
            createValidationReportContainerFromJsonString("{\n" + "  \"notices\": []\n" + "}")
                .getErrorCodes())
        .isEmpty();
  }

  @Test
  public void getErrorCodes_errorsInReport_nonEmptySet() {
    assertThat(
            createValidationReportContainerFromJsonString(
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
    compareErrorCodes(
        createValidationReportContainerFromJsonString(
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
                + "}"),
        createValidationReportContainerFromJsonString(
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
                + "}"),
        true);
  }

  @Test
  public void hasSameErrorCodes_differentErrorsInReport_false() {
    compareErrorCodes(
        createValidationReportContainerFromJsonString(
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
                + "}"),
        createValidationReportContainerFromJsonString(
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
                + "}"),
        false);
  }
}
