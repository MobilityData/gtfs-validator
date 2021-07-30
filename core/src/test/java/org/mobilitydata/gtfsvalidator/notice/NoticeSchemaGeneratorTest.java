package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.notice.sample.AnotherTestValidationNotice;
import org.mobilitydata.gtfsvalidator.validator.sample.SampleTestValidator;

public class NoticeSchemaGeneratorTest {

  @Test
  public void exportNoticesSchema() throws IOException {
    String schema = NoticeSchemaGenerator.export(true,
        ImmutableList.of(AnotherTestValidationNotice.class.getPackage().getName()),
        ImmutableList.of(SampleTestValidator.class.getPackage().getName()));
//    assertThat(NoticeSchemaGenerator.export(
//        true,
//        ImmutableList.of(AnotherTestValidationNotice.class.getPackage().getName()),
//        ImmutableList.of(SampleTestValidator.class.getPackage().getName()))).isEqualTo(
//        "{\n"
//            + "  \"some_test_validation\": [\n"
//            + "    {\n"
//            + "      \"name\": \"filename\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"csvRowNumber\",\n"
//            + "      \"type\": \"BIGINT\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"fieldName\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"fieldValue\",\n"
//            + "      \"type\": \"BLOB\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"severityLevel\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    }\n"
//            + "  ],\n"
//            + "  \"another_test_validation\": [\n"
//            + "    {\n"
//            + "      \"name\": \"filename\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"csvRowNumber\",\n"
//            + "      \"type\": \"BIGINT\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"fieldName\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"fieldValue\",\n"
//            + "      \"type\": \"BLOB\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"otherFieldValue\",\n"
//            + "      \"type\": \"DOUBLE\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"sampleDate\",\n"
//            + "      \"type\": \"BLOB\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"sampleTime\",\n"
//            + "      \"type\": \"BLOB\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"sampleColor\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"severityLevel\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    }\n"
//            + "  ],\n"
//            + "  \"nested_test_validator\": [\n"
//            + "    {\n"
//            + "      \"name\": \"tripId\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"csvRowNumber\",\n"
//            + "      \"type\": \"BIGINT\"\n"
//            + "    },\n"
//            + "    {\n"
//            + "      \"name\": \"severityLevel\",\n"
//            + "      \"type\": \"VARCHAR\"\n"
//            + "    }\n"
//            + "  ]\n"
//            + "}");
  }
}
