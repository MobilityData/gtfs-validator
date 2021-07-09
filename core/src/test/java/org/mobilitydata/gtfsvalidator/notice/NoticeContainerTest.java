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

package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class NoticeContainerTest {

  private static final String NOTICE_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.notice.sample";
  private static final String VALIDATOR_PACKAGE_NAME = "org.mobilitydata.gtfsvalidator.validator.sample";

  @Test
  public void exportJson_defaultPrint() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    container.addValidationNotice(new MissingRequiredFileNotice("agency.txt"));
    container.addSystemError(
        new RuntimeExceptionInValidatorError(
            "FaultyValidator", new IndexOutOfBoundsException("Index 0 out of bounds")));
    assertThat(container.exportValidationNotices(false))
        .isEqualTo(
            "{\"notices\":["
                + "{\"code\":\"missing_required_file\",\"severity\":\"ERROR\","
                + "\"totalNotices\":2,\"notices\":"
                + "[{\"filename\":\"stops.txt\"},{\"filename\":\"agency.txt\"}]}]}");
    assertThat(container.exportSystemErrors(false))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"runtime_exception_in_validator_error\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"notices\":[{\"validator\":\"FaultyValidator\",\"exception\":\"java.lang.IndexOutOfBoundsException\",\"message\":\"Index"
                + " 0 out of bounds\"}]}]}");
  }

  @Test
  public void exportJson_prettyPrint() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(new MissingRequiredFileNotice("stops.txt"));
    container.addValidationNotice(new MissingRequiredFileNotice("agency.txt"));
    container.addSystemError(
        new RuntimeExceptionInValidatorError(
            "FaultyValidator", new IndexOutOfBoundsException("Index 0 out of bounds")));
    assertThat(container.exportValidationNotices(true))
        .isEqualTo(
            "{\n"
                + "  \"notices\": [\n"
                + "    {\n"
                + "      \"code\": \"missing_required_file\",\n"
                + "      \"severity\": \"ERROR\",\n"
                + "      \"totalNotices\": 2,\n"
                + "      \"notices\": [\n"
                + "        {\n"
                + "          \"filename\": \"stops.txt\"\n"
                + "        },\n"
                + "        {\n"
                + "          \"filename\": \"agency.txt\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");
    assertThat(container.exportSystemErrors(true))
        .isEqualTo(
            "{\n"
                + "  \"notices\": [\n"
                + "    {\n"
                + "      \"code\": \"runtime_exception_in_validator_error\",\n"
                + "      \"severity\": \"ERROR\",\n"
                + "      \"totalNotices\": 1,\n"
                + "      \"notices\": [\n"
                + "        {\n"
                + "          \"validator\": \"FaultyValidator\",\n"
                + "          \"exception\": \"java.lang.IndexOutOfBoundsException\",\n"
                + "          \"message\": \"Index 0 out of bounds\"\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  ]\n"
                + "}");
  }

  @Test
  public void exportNullInContext() {
    // Test that `null` value in the context is serialized properly.
    NoticeContainer container = new NoticeContainer();
    // Use HashMap because ImmutableMap does not support nulls.
    Map<String, Object> context = new HashMap<>();
    context.put("nullField", null);
    container.addValidationNotice(
        new TestValidationNotice("test_notice", context, SeverityLevel.ERROR));
    assertThat(container.exportValidationNotices(false))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"test_notice\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"notices\":[{\"nullField\":null}]}]}");
  }

  @Test
  public void exportInfinityInContext() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(
        new TestValidationNotice(
            "test_notice",
            ImmutableMap.of("infinityField", Double.POSITIVE_INFINITY),
            SeverityLevel.ERROR));
    assertThat(container.exportValidationNotices(false))
        .isEqualTo(
            "{\"notices\":[{\"code\":\"test_notice\",\"severity\":\"ERROR\","
                + "\"totalNotices\":1,\"notices\":[{\"infinityField\":Infinity}]}]}");
  }

  @Test
  public void exportSeverities() {
    NoticeContainer container = new NoticeContainer();
    container.addValidationNotice(
        new TestValidationNotice("notice_a", ImmutableMap.of("keyA", 1), SeverityLevel.ERROR));
    container.addValidationNotice(
        new TestValidationNotice("notice_b", ImmutableMap.of("keyB", 2), SeverityLevel.ERROR));
    container.addValidationNotice(
        new TestValidationNotice("notice_a", ImmutableMap.of("keyC", 3), SeverityLevel.INFO));
    assertThat(container.exportValidationNotices(false))
        .isEqualTo(
            "{\"notices\":["
                + "{\"code\":\"notice_a\",\"severity\":\"INFO\",\"totalNotices\":1,"
                + "\"notices\":[{\"keyC\":3}]},"
                + "{\"code\":\"notice_a\",\"severity\":\"ERROR\",\"totalNotices\":1,"
                + "\"notices\":[{\"keyA\":1}]},"
                + "{\"code\":\"notice_b\",\"severity\":\"ERROR\",\"totalNotices\":1,"
                + "\"notices\":[{\"keyB\":2}]}]}");
  }

  @Test
  public void addAll() {
    ValidationNotice n1 = new MissingRequiredFileNotice("stops.txt");
    ValidationNotice n2 = new UnknownFileNotice("unknown.txt");
    SystemError e1 =
        new RuntimeExceptionInValidatorError(
            "Validator1", new IndexOutOfBoundsException("Index 0 out of bounds"));
    SystemError e2 =
        new RuntimeExceptionInValidatorError(
            "Validator2", new NegativeArraySizeException("Index -1 out of bounds"));
    NoticeContainer c1 = new NoticeContainer();
    c1.addValidationNotice(n1);
    c1.addSystemError(e1);
    NoticeContainer c2 = new NoticeContainer();
    c2.addValidationNotice(n2);
    c2.addSystemError(e2);
    c1.addAll(c2);
    assertThat(c1.getValidationNotices()).containsExactly(n1, n2);
    assertThat(c1.getSystemErrors()).containsExactly(e1, e2);
  }

  @Test
  public void exportNoticesSchema() throws IOException {
        assertThat(NoticeContainer.exportNoticesSchema(
            true,
            NOTICE_PACKAGE_NAME,
            VALIDATOR_PACKAGE_NAME)).isEqualTo("{\n"
            + "  \"AnotherTestValidationNotice\": [\n"
            + "    {\n"
            + "      \"name\": \"filename\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"csvRowNumber\",\n"
            + "      \"type\": \"BIGINT\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"fieldName\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"fieldValue\",\n"
            + "      \"type\": \"BLOB\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"otherFieldValue\",\n"
            + "      \"type\": \"DOUBLE\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"sampleDate\",\n"
            + "      \"type\": \"BLOB\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"sampleTime\",\n"
            + "      \"type\": \"BLOB\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"sampleColor\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"SeverityLevel\": \"BLOB\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"SomeTestValidationNotice\": [\n"
            + "    {\n"
            + "      \"name\": \"filename\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"csvRowNumber\",\n"
            + "      \"type\": \"BIGINT\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"fieldName\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"fieldValue\",\n"
            + "      \"type\": \"BLOB\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"severityLevel\",\n"
            + "      \"type\": \"BLOB\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"SeverityLevel\": \"BLOB\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"NestedTestValidatorNotice\": [\n"
            + "    {\n"
            + "      \"name\": \"tripId\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"csvRowNumber\",\n"
            + "      \"type\": \"BIGINT\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"SeverityLevel\": \"BLOB\"\n"
            + "    }\n"
            + "  ]\n"
            + "}");
    }
}
