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

import org.apache.commons.validator.routines.FloatValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.UrlValidator;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.base.Notice;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.error.*;
import org.mobilitydata.gtfsvalidator.domain.entity.notice.warning.NonAsciiOrNonPrintableCharNotice;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GtfsTypeValidatorTest {

    private static final String TEST_ID = "test_id";
    public static final String TEST_FILE_TST = "test_file.tst";

    @Test
    void inRangeFloatDoNotGenerateNotice() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);
        when(mockFloatValidator.isInRange(ArgumentMatchers.eq(Float.valueOf(-5.0f)),
                ArgumentMatchers.eq(-6.66f),
                ArgumentMatchers.eq(66.6f))).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_with_range");
        when(mockColumnSpec.getFloatmin()).thenReturn(-6.66f);
        when(mockColumnSpec.getFloatmax()).thenReturn(66.6f);

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mockFloatValidator,
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_with_range", -5.0f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockFloatValidator, times(1)).isInRange(
                ArgumentMatchers.eq(Float.valueOf(-5.0f)),
                ArgumentMatchers.eq(-6.66f),
                ArgumentMatchers.eq(66.6f)
        );
    }

    @Test
    void outOfRangeFloatGenerateNotice() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_with_range");
        when(mockColumnSpec.getFloatmin()).thenReturn(-6.66f);
        when(mockColumnSpec.getFloatmax()).thenReturn(66.6f);

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mockFloatValidator,
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("float_with_range", 66.7f),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(FloatFieldValueOutOfRangeNotice.class));
        assertEquals("E011", notice.getId());
        assertEquals("Out of range float value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:float_with_range of entity with id:test_id -- " +
                        "min:-6.66 max:66.6 actual:66.7",
                notice.getDescription());

        verify(mockFloatValidator, times(1)).isInRange(
                ArgumentMatchers.eq(Float.valueOf(66.7f)),
                ArgumentMatchers.eq(-6.66f),
                ArgumentMatchers.eq(66.6f)
        );
    }

    @Test
    void inRangeIntegerDoNotGenerateNotice() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);
        when(mockIntegerValidator.isInRange(ArgumentMatchers.eq(Integer.valueOf(5)),
                ArgumentMatchers.eq(-6),
                ArgumentMatchers.eq(66))).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_with_range");
        when(mockColumnSpec.getIntmin()).thenReturn(-6);
        when(mockColumnSpec.getIntmax()).thenReturn(66);

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mockIntegerValidator,
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("integer_with_range", 5),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockIntegerValidator, times(1)).isInRange(
                ArgumentMatchers.eq(Integer.valueOf(5)),
                ArgumentMatchers.eq(-6),
                ArgumentMatchers.eq(66)
        );
    }

    @Test
    void outOfRangeIntegerGenerateNotice() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_with_range");
        when(mockColumnSpec.getIntmin()).thenReturn(-6);
        when(mockColumnSpec.getIntmax()).thenReturn(66);

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mockIntegerValidator,
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
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
        assertEquals("Invalid value for field:integer_with_range of entity with id:test_id -- " +
                        "min:-6 max:66 actual:67",
                notice.getDescription());

        verify(mockIntegerValidator, times(1)).isInRange(
                ArgumentMatchers.eq(Integer.valueOf(67)),
                ArgumentMatchers.eq(-6),
                ArgumentMatchers.eq(66)
        );
    }

    @Test
    void colorInCorrectFormatDoNotGenerateNotice() {

        RegexValidator mockColorValidator = mock(RegexValidator.class);
        when(mockColorValidator.isValid(ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("color_with_regex");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.COLOR);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mockColorValidator,
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "FABFAB"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockColorValidator, times(1)).isValid(ArgumentMatchers.eq("FABFAB"));
    }

    @Test
    void colorInIncorrectFormatGenerateNotice() {

        RegexValidator mockColorValidator = mock(RegexValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("color_with_regex");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.COLOR);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mockColorValidator,
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("color_with_regex", "AZ-FTJ"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());
        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidColorNotice.class));
        assertEquals("E014", notice.getId());
        assertEquals("Invalid color", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid color:AZ-FTJ in field:color_with_regex for entity with id:test_id",
                notice.getDescription());

        verify(mockColorValidator, times(1)).isValid(ArgumentMatchers.eq("AZ-FTJ"));
    }

    @Test
    void timeInCorrectFormatDoNotGenerateNotice() {

        RegexValidator mockTimeValidator = mock(RegexValidator.class);
        when(mockTimeValidator.isValid(ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("time_with_regex");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIME);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mockTimeValidator,
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("time_with_regex", "01:02:03"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockTimeValidator, times(1)).isValid(ArgumentMatchers.eq("01:02:03"));
    }

    @Test
    void timeInIncorrectFormatGenerateNotice() {

        RegexValidator mockTimeValidator = mock(RegexValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("time_with_regex");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIME);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mockTimeValidator,
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("time_with_regex", "001:2:00003"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());
        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidTimeNotice.class));
        assertEquals("E016", notice.getId());
        assertEquals("Invalid time", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid time:001:2:00003 in field:time_with_regex for entity with id:test_id",
                notice.getDescription());

        verify(mockTimeValidator, times(1)).isValid(ArgumentMatchers.eq("001:2:00003"));
    }

    @Test
    void urlInCorrectFormatDoNotGenerateNotice() {

        UrlValidator mockUrlValidator = mock(UrlValidator.class);
        when(mockUrlValidator.isValid(ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_url");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.URL);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mockUrlValidator,
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_url", "http://mobilitydata.org"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockUrlValidator, times(1)).isValid(
                ArgumentMatchers.eq("http://mobilitydata.org"));
    }

    @Test
    void urlInInCorrectFormatGenerateNotice() {

        UrlValidator mockUrlValidator = mock(UrlValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_url");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.URL);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mockUrlValidator,
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

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

        verify(mockUrlValidator, times(1)).isValid(
                ArgumentMatchers.eq("ftp://mobilitydata.org"));
    }

    @Test
    void timezoneValidDoNotGenerateNotice() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_timezone");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIMEZONE);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Set.of("America/Montreal")
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_timezone", "America/Montreal"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void timezoneInvalidGenerateNotice() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_timezone");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIMEZONE);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

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
    void idAsciiDoNotGenerateNotice() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_id");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_id", "666sixcentsoixantesix"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }

    @Test
    void idNonAsciiGenerateNotice() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_id");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
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
    }

    @Test
    void idNonPrintableAsciiGenerateNotice() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_id");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        // contains non printable ASCII
        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_id", "ab\u0003"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(NonAsciiOrNonPrintableCharNotice.class));
        assertEquals("W003", notice.getId());
        assertEquals("Suspicious id", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals(
                "Non ascii or non printable character(s) in:ab\u0003 in field:type_id for entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void nullOrEmptyOptionalValueDoNotGenerateError() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);

        GtfsSpecificationProto.ColumnSpecProto mockUnspecifiedColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUnspecifiedInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUnspecifiedInputType.getType())
                .thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INPUT_TYPE_UNSPECIFIED);
        when(mockUnspecifiedColumnSpec.getType()).thenReturn(mockUnspecifiedInputType);

        GtfsSpecificationProto.ColumnSpecProto mockTextColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTextInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTextInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TEXT);
        when(mockTextColumnSpec.getType()).thenReturn(mockTextInputType);

        GtfsSpecificationProto.ColumnSpecProto mockFloatColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockFloatInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockFloatInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);
        when(mockFloatColumnSpec.getType()).thenReturn(mockFloatInputType);

        GtfsSpecificationProto.ColumnSpecProto mockIntegerColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIntegerInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIntegerInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);
        when(mockIntegerColumnSpec.getType()).thenReturn(mockIntegerInputType);

        GtfsSpecificationProto.ColumnSpecProto mockColorColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockColorInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockColorInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.COLOR);
        when(mockColorColumnSpec.getType()).thenReturn(mockColorInputType);

        GtfsSpecificationProto.ColumnSpecProto mockTimezoneColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimezoneInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimezoneInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIMEZONE);
        when(mockTimezoneColumnSpec.getType()).thenReturn(mockTimezoneInputType);

        GtfsSpecificationProto.ColumnSpecProto mockIdColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIdInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIdInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);
        when(mockIdColumnSpec.getType()).thenReturn(mockIdInputType);

        GtfsSpecificationProto.ColumnSpecProto mockUrlColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUrlInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUrlInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.URL);
        when(mockUrlColumnSpec.getType()).thenReturn(mockUrlInputType);

        GtfsSpecificationProto.ColumnSpecProto mockTimeColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimeInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimeInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIME);
        when(mockTimeColumnSpec.getType()).thenReturn(mockTimeInputType);

        GtfsSpecificationProto.ColumnSpecProto mockDateColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockDateInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockDateInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.DATE);
        when(mockDateColumnSpec.getType()).thenReturn(mockDateInputType);

        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockUnspecifiedColumnSpec,
                mockTextColumnSpec,
                mockFloatColumnSpec,
                mockIntegerColumnSpec,
                mockColorColumnSpec,
                mockTimezoneColumnSpec,
                mockIdColumnSpec,
                mockUrlColumnSpec,
                mockTimeColumnSpec,
                mockDateColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Collections.emptyMap(),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
        //noinspection ResultOfMethodCallIgnored
        verify(mockFileSpec, times(1)).getColumnList();
        verify(mockUnspecifiedColumnSpec, times(1)).getName();
        verify(mockTextColumnSpec, times(1)).getName();
        verify(mockFloatColumnSpec, times(1)).getName();
        verify(mockIntegerColumnSpec, times(1)).getName();
        verify(mockColorColumnSpec, times(1)).getName();
        verify(mockTimezoneColumnSpec, times(1)).getName();
        verify(mockIdColumnSpec, times(1)).getName();
        verify(mockUrlColumnSpec, times(1)).getName();
        verify(mockTimeColumnSpec, times(1)).getName();
        verify(mockDateColumnSpec, times(1)).getName();
    }

    @Test
    void nullRequiredValueGenerateError() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        //GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        //when(mockColumnSpec.getName()).thenReturn("type_id");
        //when(mockColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockUnspecifiedColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUnspecifiedInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUnspecifiedInputType.getType())
                .thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INPUT_TYPE_UNSPECIFIED);
        when(mockUnspecifiedColumnSpec.getType()).thenReturn(mockUnspecifiedInputType);
        when(mockUnspecifiedColumnSpec.getName()).thenReturn("type_unspecified");
        when(mockUnspecifiedColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTextColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTextInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTextInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TEXT);
        when(mockTextColumnSpec.getType()).thenReturn(mockTextInputType);
        when(mockTextColumnSpec.getName()).thenReturn("type_text");
        when(mockTextColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockFloatColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockFloatInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockFloatInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);
        when(mockFloatColumnSpec.getType()).thenReturn(mockFloatInputType);
        when(mockFloatColumnSpec.getName()).thenReturn("type_float");
        when(mockFloatColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockIntegerColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIntegerInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIntegerInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);
        when(mockIntegerColumnSpec.getType()).thenReturn(mockIntegerInputType);
        when(mockIntegerColumnSpec.getName()).thenReturn("type_integer");
        when(mockIntegerColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockColorColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockColorInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockColorInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.COLOR);
        when(mockColorColumnSpec.getType()).thenReturn(mockColorInputType);
        when(mockColorColumnSpec.getName()).thenReturn("type_color");
        when(mockColorColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTimezoneColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimezoneInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimezoneInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIMEZONE);
        when(mockTimezoneColumnSpec.getType()).thenReturn(mockTimezoneInputType);
        when(mockTimezoneColumnSpec.getName()).thenReturn("type_timezone");
        when(mockTimezoneColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockIdColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIdInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIdInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);
        when(mockIdColumnSpec.getType()).thenReturn(mockIdInputType);
        when(mockIdColumnSpec.getName()).thenReturn("type_id");
        when(mockIdColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockUrlColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUrlInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUrlInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.URL);
        when(mockUrlColumnSpec.getType()).thenReturn(mockUrlInputType);
        when(mockUrlColumnSpec.getName()).thenReturn("type_url");
        when(mockUrlColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTimeColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimeInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimeInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIME);
        when(mockTimeColumnSpec.getType()).thenReturn(mockTimeInputType);
        when(mockTimeColumnSpec.getName()).thenReturn("type_time");
        when(mockTimeColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockDateColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockDateInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockDateInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.DATE);
        when(mockDateColumnSpec.getType()).thenReturn(mockDateInputType);
        when(mockDateColumnSpec.getName()).thenReturn("type_date");
        when(mockDateColumnSpec.getValueRequired()).thenReturn(true);

        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockUnspecifiedColumnSpec,
                mockTextColumnSpec,
                mockFloatColumnSpec,
                mockIntegerColumnSpec,
                mockColorColumnSpec,
                mockTimezoneColumnSpec,
                mockIdColumnSpec,
                mockUrlColumnSpec,
                mockTimeColumnSpec,
                mockDateColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Collections.emptyMap(),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(10, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_unspecified marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_text marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_float marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_integer marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_color marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(5);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_timezone marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(6);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_id marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(7);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_url marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(8);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_time marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(9);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_date marked as required in entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void emptyRequiredValueGenerateError() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);

        GtfsSpecificationProto.ColumnSpecProto mockUnspecifiedColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUnspecifiedInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUnspecifiedInputType.getType())
                .thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INPUT_TYPE_UNSPECIFIED);
        when(mockUnspecifiedColumnSpec.getType()).thenReturn(mockUnspecifiedInputType);
        when(mockUnspecifiedColumnSpec.getName()).thenReturn("type_unspecified");
        when(mockUnspecifiedColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTextColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTextInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTextInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TEXT);
        when(mockTextColumnSpec.getType()).thenReturn(mockTextInputType);
        when(mockTextColumnSpec.getName()).thenReturn("type_text");
        when(mockTextColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockFloatColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockFloatInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockFloatInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);
        when(mockFloatColumnSpec.getType()).thenReturn(mockFloatInputType);
        when(mockFloatColumnSpec.getName()).thenReturn("type_float");
        when(mockFloatColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockIntegerColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIntegerInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIntegerInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);
        when(mockIntegerColumnSpec.getType()).thenReturn(mockIntegerInputType);
        when(mockIntegerColumnSpec.getName()).thenReturn("type_integer");
        when(mockIntegerColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockColorColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockColorInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockColorInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.COLOR);
        when(mockColorColumnSpec.getType()).thenReturn(mockColorInputType);
        when(mockColorColumnSpec.getName()).thenReturn("type_color");
        when(mockColorColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTimezoneColumnSpec =
                mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimezoneInputType =
                mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimezoneInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIMEZONE);
        when(mockTimezoneColumnSpec.getType()).thenReturn(mockTimezoneInputType);
        when(mockTimezoneColumnSpec.getName()).thenReturn("type_timezone");
        when(mockTimezoneColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockIdColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockIdInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockIdInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.ID);
        when(mockIdColumnSpec.getType()).thenReturn(mockIdInputType);
        when(mockIdColumnSpec.getName()).thenReturn("type_id");
        when(mockIdColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockUrlColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockUrlInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockUrlInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.URL);
        when(mockUrlColumnSpec.getType()).thenReturn(mockUrlInputType);
        when(mockUrlColumnSpec.getName()).thenReturn("type_url");
        when(mockUrlColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockTimeColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockTimeInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockTimeInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TIME);
        when(mockTimeColumnSpec.getType()).thenReturn(mockTimeInputType);
        when(mockTimeColumnSpec.getName()).thenReturn("type_time");
        when(mockTimeColumnSpec.getValueRequired()).thenReturn(true);

        GtfsSpecificationProto.ColumnSpecProto mockDateColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        GtfsSpecificationProto.ColumnInputType mockDateInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockDateInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.DATE);
        when(mockDateColumnSpec.getType()).thenReturn(mockDateInputType);
        when(mockDateColumnSpec.getName()).thenReturn("type_date");
        when(mockDateColumnSpec.getValueRequired()).thenReturn(true);

        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockUnspecifiedColumnSpec,
                mockTextColumnSpec,
                mockFloatColumnSpec,
                mockIntegerColumnSpec,
                mockColorColumnSpec,
                mockTimezoneColumnSpec,
                mockIdColumnSpec,
                mockUrlColumnSpec,
                mockTimeColumnSpec,
                mockDateColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_unspecified", "",
                        "type_text", "",
                        "type_color", "",
                        "type_timezone", "",
                        "type_id", "",
                        "type_url", "",
                        "type_time", "",
                        "type_date", ""),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(10, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_unspecified marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_text marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_float marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_integer marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_color marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(5);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_timezone marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(6);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_id marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(7);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_url marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(8);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_time marked as required in entity with id:test_id",
                notice.getDescription());

        notice = new ArrayList<>(result).get(9);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("E015", notice.getId());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:type_date marked as required in entity with id:test_id",
                notice.getDescription());
    }

    @Test
    void checkCurrencyCodeValidation() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("currency_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.CURRENCY_CODE);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(RegexValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> invalidResult = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("currency_type", "JAN"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, invalidResult.size());

        Notice notice = new ArrayList<>(invalidResult).get(0);
        assertThat(notice, instanceOf(InvalidCurrencyCodeNotice.class));
        assertEquals("E018", notice.getId());
        assertEquals("Invalid currency code", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid currency code: JAN in field: currency_type for entity with id: test_id",
                notice.getDescription());

        Collection<Notice> validResult = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("currency_type", "MXN"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, validResult.size());
    }
}