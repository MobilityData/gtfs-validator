package org.mobilitydata.gtfsvalidator.parser;

import com.google.common.io.Resources;
import com.google.protobuf.TextFormat;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.adapter.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.ErrorNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseFloatNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseIntegerNotice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.*;

class GtfsEntityParserTest {
    @SuppressWarnings("UnstableApiUsage")
    private final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
            Resources.toString(Resources.getResource("test_gtfs_spec.asciipb"), StandardCharsets.UTF_8),
            GtfsSpecificationProto.CsvSpecProtos.class
    );

    GtfsEntityParserTest() throws IOException {
    }

    @Test
    void validNumericTypesValidationShouldGenerateNothing() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", "1"),
                0
        ));

        assertEquals(0, result.size());

        result = underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "-1.0",
                        "integer_test", "-1"),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void emptyNumericTypesValidationShouldGenerateNothing() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "",
                        "integer_test", "1"),
                0
        ));

        assertEquals(0, result.size());

        result = underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", ""),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void notANumberFloatValidationShouldGenerateError() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "NaN",
                        "integer_test", "1"),
                0
        ));

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseFloatNotice.class));
        assertEquals("E006", notice.getId());
        assertEquals("Invalid float value", notice.getTitle());
        assertEquals("test_file_required.txt", notice.getFilename());
        assertEquals("Value: 'NaN' of field: float_test with type float can't be parsed in file: test_file_required.txt at row: 0", notice.getDescription());
    }

    @Test
    void invalidFloatValidationShouldGenerateError() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "abc",
                        "integer_test", "1"),
                0
        ));

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseFloatNotice.class));
        assertEquals("E006", notice.getId());
        assertEquals("Invalid float value", notice.getTitle());
        assertEquals("test_file_required.txt", notice.getFilename());
        assertEquals("Value: 'abc' of field: float_test with type float can't be parsed in file: test_file_required.txt at row: 0", notice.getDescription());
    }

    @Test
    void invalidIntegerValidationShouldGenerateError() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", "abc"),
                0
        ));

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseIntegerNotice.class));
        assertEquals("E005", notice.getId());
        assertEquals("Invalid integer value", notice.getTitle());
        assertEquals("test_file_required.txt", notice.getFilename());
        assertEquals("Value: 'abc' of field: integer_test with type integer can't be parsed in file: test_file_required.txt at row: 0", notice.getDescription());
    }

    @Test
    void validNumericTypesShouldParse() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", "1"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertEquals(1.0f, result.get("float_test"));
        assertEquals(1, result.get("integer_test"));

        result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "-1.0",
                        "integer_test", "-1"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertEquals(-1.0f, result.get("float_test"));
        assertEquals(-1, result.get("integer_test"));
    }

    @Test
    void emptyFloatShouldParseToNull() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "",
                        "integer_test", "1"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertNull(result.get("float_test"));
        assertEquals(1, result.get("integer_test"));
    }

    @Test
    void notANumberFloatShouldParseToNull() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "NaN",
                        "integer_test", "1"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertNull(result.get("float_test"));
        assertEquals(1, result.get("integer_test"));
    }

    @Test
    void invalidFloatShouldParseToNull() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "abc",
                        "integer_test", "1"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertNull(result.get("float_test"));
        assertEquals(1, result.get("integer_test"));
    }

    @Test
    void emptyIntegerShouldParseToNull() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", ""),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertEquals(1.0f, result.get("float_test"));
        assertNull(result.get("integer_test"));
    }

    @Test
    void invalidIntegerShouldParseToNull() {

        GtfsEntityParser underTest = new GtfsEntityParser(
                testGtfsSpec.getCsvspec(0),
                new RawFileInfo.RawFileInfoBuilder().build()
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_test", "a_string", "float_test", "1.0",
                        "integer_test", "abc"),
                0
        ));

        assertEquals("a_string", result.get("string_test"));
        assertEquals(1.0f, result.get("float_test"));
        assertNull(result.get("integer_test"));
    }
}