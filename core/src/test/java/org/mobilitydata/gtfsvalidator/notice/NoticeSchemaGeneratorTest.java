package org.mobilitydata.gtfsvalidator.notice;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.lang.annotation.AnnotationFormatError;
import org.junit.Test;
import org.mobilitydata.gtfsvalidator.exception.ConstructorParametersInconsistencyException;
import org.mobilitydata.gtfsvalidator.notice.sample.AnotherTestValidationNotice;
import org.mobilitydata.gtfsvalidator.notice.sample.mismatching_types.MismatchingTypesNotice;
import org.mobilitydata.gtfsvalidator.notice.sample.no_annotation.NoAnnotationNotice;
import org.mobilitydata.gtfsvalidator.validator.sample.SampleTestValidator;

public class NoticeSchemaGeneratorTest {

  @Test
  public void exportNoticesSchema()
      throws IOException, ConstructorParametersInconsistencyException {
    assertThat(NoticeSchemaGenerator.export(
        true,
        ImmutableList.of(AnotherTestValidationNotice.class.getPackage().getName()),
        ImmutableList.of(SampleTestValidator.class.getPackage().getName()))).isEqualTo(
        "{\n"
            + "  \"some_test_validation\": [\n"
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
            + "      \"name\": \"filename\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"severityLevel\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"another_test_validation\": [\n"
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
            + "      \"name\": \"filename\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"integerValue\",\n"
            + "      \"type\": \"INTEGER\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"otherFieldValue\",\n"
            + "      \"type\": \"DOUBLE\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"sampleColor\",\n"
            + "      \"type\": \"VARCHAR\"\n"
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
            + "      \"name\": \"severityLevel\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    }\n"
            + "  ],\n"
            + "  \"nested_test_validator\": [\n"
            + "    {\n"
            + "      \"name\": \"csvRowNumber\",\n"
            + "      \"type\": \"BIGINT\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"tripId\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    },\n"
            + "    {\n"
            + "      \"name\": \"severityLevel\",\n"
            + "      \"type\": \"VARCHAR\"\n"
            + "    }\n"
            + "  ]\n"
            + "}"
    );
  }

  @Test
  public void noAnnotationOnNotice_throwsException() {
    AnnotationFormatError exception = assertThrows(AnnotationFormatError.class,
        () -> NoticeSchemaGenerator.export(
            true,
            ImmutableList.of(NoAnnotationNotice.class.getPackage().getName()),
            ImmutableList.of(SampleTestValidator.class.getPackage().getName())));
    assertThat(exception.getMessage()).matches(String
        .format("Validation notice %s does not define constructor for schema export",
            NoAnnotationNotice.class.getSimpleName()));
  }

  @Test
  public void mismatchingTypesInNoticeConstructor_throwsException() {
    ConstructorParametersInconsistencyException exception = assertThrows(ConstructorParametersInconsistencyException.class,
        () -> NoticeSchemaGenerator.export(
            true,
            ImmutableList.of(MismatchingTypesNotice.class.getPackage().getName()),
            ImmutableList.of(SampleTestValidator.class.getPackage().getName())));
    assertThat(exception.getMessage()).matches(String
        .format(
            "Validation notice %s defines parameter %s with different types in its constructors.",
            MismatchingTypesNotice.class.getSimpleName(), "parameterWithWrongType"));
  }
}
