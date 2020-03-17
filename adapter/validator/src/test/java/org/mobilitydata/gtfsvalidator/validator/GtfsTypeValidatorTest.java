/*
 * Copyright (c) 2020. MobilityData IO.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mobilitydata.gtfsvalidator.validator;

import com.google.common.io.Resources;
import com.google.protobuf.TextFormat;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.*;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.Notice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("UnstableApiUsage")
class GtfsTypeValidatorTest {

    private static final String TEST_ID = "test_id";
    public static final String TEST = "test";
    public static final String TEST_FILE_TST = "test_file.tst";

    @Test
    void inRangeFloatDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_float_range_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_std_with_range", -5.0f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_std_with_range", 5.0f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void outOfRangeFloatGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_float_range_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_std_with_range", 66.7f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(FloatFieldValueOutOfRangeNotice.class));
        assertEquals("E011", notice.getId());
        assertEquals("Out of range float value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:float_std_with_range of entity with id:test_id -- " +
                        "min:-6.66 max:66.6 actual:66.7",
                notice.getDescription());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_std" +
                        "_with_range", -66.6f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(FloatFieldValueOutOfRangeNotice.class));
        assertEquals("E011", notice.getId());
        assertEquals("Out of range float value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:float_std_with_range of entity with id:test_id -- " +
                        "min:-6.66 max:66.6 actual:-66.6",
                notice.getDescription());
    }

    @Test
    void inRangeIntegerDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_integer_range_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("int_dec_with_range", -5),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("int_dec_with_range", 5),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void outOfRangeIntegerGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_integer_range_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("integer_with_range", 67),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(IntegerFieldValueOutOfRangeNotice.class));
        assertEquals("E010", notice.getId());
        assertEquals("Out of range integer value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:integer_with_range of entity with id:test_id -- min:-6 max:66 actual:67",
                notice.getDescription());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("integer_with_range", -7),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(IntegerFieldValueOutOfRangeNotice.class));
        assertEquals("E010", notice.getId());
        assertEquals("Out of range integer value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:integer_with_range of entity with id:test_id -- min:-6 max:66 actual:-7",
                notice.getDescription());
    }

    @Test
    void colorInCorrectFormatDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_color_format_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "ABCDEF"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "012345"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "6789af"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void colorInIncorrectFormatGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_color_format_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // incorrect length - too short
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "ABC"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidColorNotice.class));
        assertEquals("E014", notice.getId());
        assertEquals("Invalid color", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid color:ABC in field:color_with_regex for entity with id:test_id",
                notice.getDescription());

        // incorrect length - too long
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "ABCDEF0"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidColorNotice.class));
        assertEquals("E014", notice.getId());
        assertEquals("Invalid color", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid color:ABCDEF0 in field:color_with_regex for entity with id:test_id",
                notice.getDescription());

        // invalid characters
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "AZ-FTJ"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidColorNotice.class));
        assertEquals("E014", notice.getId());
        assertEquals("Invalid color", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid color:AZ-FTJ in field:color_with_regex for entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void urlInCorrectFormatDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_url_format_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_url", "http://mobilitydata.org"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_url", "https://mobilitydata.org"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void urlInInCorrectFormatGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_url_format_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // invalid scheme
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_url", "ftp://mobilitydata.org"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidUrlNotice.class));
        assertEquals("E012", notice.getId());
        assertEquals("Invalid url", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid url:ftp://mobilitydata.org in field:type_url for entity with id:test_id",
                notice.getDescription());


        // any malformed

        // invalid scheme
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_url", "http://mobilitydataorg"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidUrlNotice.class));
        assertEquals("E012", notice.getId());
        assertEquals("Invalid url", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid url:http://mobilitydataorg in field:type_url for entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void timezoneValidDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_timezone_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_timezone", "America/Montreal"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void timezoneInvalidGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_timezone_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // any non parsable
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_timezone", "abc"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidTimezoneNotice.class));
        assertEquals("E013", notice.getId());
        assertEquals("Invalid timezone", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid timezone:abc in field:type_timezone for entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void idAsciiDoNotGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_id_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_id", "666sixcentsoixantesix"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void idNonAsciiGenerateNotice() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_id_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // contains non ASCII
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_id", "abçé"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(NonAsciiOrNonPrintableCharNotice.class));
        assertEquals("W003", notice.getId());
        assertEquals("Suspicious id", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Non ascii or non printable character(s) in:abçé in field:type_id for entity with id:test_id",
                notice.getDescription());


        // contains non printable ASCII
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_id", "ab\u0003"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(NonAsciiOrNonPrintableCharNotice.class));
        assertEquals("W003", notice.getId());
        assertEquals("Suspicious id", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals(
                "Non ascii or non printable character(s) in:ab\u0003 in field:type_id for entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void nullOrEmptyOptionalValueDoNotGenerateError() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_required_field_value_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // empty optional values
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_required_test", 0.0f,
                        "integer_required_test", 0,
                        "color_optional_test", "",
                        "color_required_test", "ABCDEF",
                        "unspecified_optional_test", "",
                        "unspecified_required_test", "a_string",
                        "text_optional_test", "",
                        "text_required_test", "a_string"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        // null optional values
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_required_test", 0.0f,
                        "integer_required_test", 0,
                        "color_required_test", "ABCDEF",
                        "unspecified_required_test", "a_string",
                        "text_required_test", "a_string"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void nullOrEmptyRequiredValueGenerateError() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_required_field_value_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        // empty required values
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_optional_test", "",
                        "integer_optional_test", "",
                        "color_optional_test", "",
                        "color_required_test", "",
                        "unspecified_optional_test", "",
                        "unspecified_required_test", "",
                        "text_optional_test", "",
                        "text_required_test", ""),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(5, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:float_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:integer_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:color_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:unspecified_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:text_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        // null required values
        result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_optional_test", "",
                        "integer_optional_test", "",
                        "color_optional_test", "",
                        "unspecified_optional_test", "",
                        "text_optional_test", ""),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(5, result.size());

        notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:float_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:integer_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:color_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals(
                "Missing value for field:unspecified_required_test marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:text_required_test marked as required in entity with id:test_id",
                notice.getDescription());


    }

    @Test
    void dateTypeIsUnsupported() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_date_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("date_test", TEST),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(UnsupportedGtfsTypeNotice.class));
        assertEquals("I001", notice.getId());
        assertEquals("Unsupported gtfs type", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Tried to validate an unsupported Gtfs type in file:test_file.tst, " +
                        "entityId:test_id, field name:date_test -->IGNORED",
                notice.getDescription());
    }

    @Test
    void timeTypeIsUnsupported() throws IOException {

        final GtfsSpecificationProto.CsvSpecProtos testGtfsSpec = TextFormat.parse(
                Resources.toString(Resources.getResource("validator_time_test_gtfs_spec.asciipb"),
                        StandardCharsets.UTF_8),
                GtfsSpecificationProto.CsvSpecProtos.class
        );

        GtfsTypeValidator underTest = new GtfsTypeValidator(testGtfsSpec.getCsvspec(0)
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("time_test", TEST),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(UnsupportedGtfsTypeNotice.class));
        assertEquals("I001", notice.getId());
        assertEquals("Unsupported gtfs type", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Tried to validate an unsupported Gtfs type in file:test_file.tst, " +
                        "entityId:test_id, field name:time_test -->IGNORED",
                notice.getDescription());
    }
}