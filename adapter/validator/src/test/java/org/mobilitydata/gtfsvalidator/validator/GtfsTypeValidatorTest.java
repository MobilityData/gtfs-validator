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

import org.apache.commons.validator.routines.*;
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((FloatFieldValueOutOfRangeNotice) notice).getLevel());
        assertEquals(11, notice.getCode());
        assertEquals("Out of range float value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:`float_with_range` of entity with id:`test_id` -- " +
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((IntegerFieldValueOutOfRangeNotice) notice).getLevel());
        assertEquals(10, notice.getCode());
        assertEquals("Out of range integer value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid value for field:`integer_with_range` of entity with id:`test_id` -- " +
                        "min:-6 max:66 actual:67",
                notice.getDescription());

        verify(mockIntegerValidator, times(1)).isInRange(
                ArgumentMatchers.eq(Integer.valueOf(67)),
                ArgumentMatchers.eq(-6),
                ArgumentMatchers.eq(66)
        );
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((InvalidTimeNotice) notice).getLevel());
        assertEquals(16, notice.getCode());
        assertEquals("Invalid time", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid time:`001:2:00003` in field:`time_with_regex` for entity with id:`test_id`",
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
    void urlInIncorrectFormatGenerateNotice() {
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((InvalidUrlNotice) notice).getLevel());
        assertEquals(12, notice.getCode());
        assertEquals("Invalid url", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid url:`ftp://mobilitydata.org` in field:`type_url` for entity with id:`test_id`",
                notice.getDescription());

        verify(mockUrlValidator, times(1)).isValid(
                ArgumentMatchers.eq("ftp://mobilitydata.org"));
    }

    @Test
    void emailInCorrectFormatDoNotGenerateNotice() {
        EmailValidator mockEmailValidator = mock(EmailValidator.class);
        when(mockEmailValidator.isValid(ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_email");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.EMAIL);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(Bcp47Validator.class),
                mockEmailValidator,
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_email", "info@mobilitydata.org"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mockEmailValidator, times(1)).isValid(
                ArgumentMatchers.eq("info@mobilitydata.org"));
    }

    @Test
    void emailInIncorrectFormatGenerateNotice() {
        EmailValidator mockEmailValidator = mock(EmailValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_email");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.EMAIL);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mock(Bcp47Validator.class),
                mockEmailValidator,
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_email", "info@mobilitydata"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());
        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidEmailNotice.class));
        assertEquals("ERROR", ((InvalidEmailNotice) notice).getLevel());
        assertEquals(23, notice.getCode());
        assertEquals("Invalid email", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid email:info@mobilitydata in field:`type_email` for entity with id:`test_id`",
                notice.getDescription());

        verify(mockEmailValidator, times(1)).isValid(
                ArgumentMatchers.eq("info@mobilitydata"));
    }

    @Test
    void langInCorrectFormatDoNotGenerateNotice() {
        Bcp47Validator mocklangValidator = mock(Bcp47Validator.class);
        when(mocklangValidator.isValid(ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_lang");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.LANGUAGE_CODE);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mocklangValidator,
                mock(EmailValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_lang", "en_US"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());

        verify(mocklangValidator, times(1)).isValid(
                ArgumentMatchers.eq("en_US"));
    }

    @Test
    void langInIncorrectFormatGenerateNotice() {
        Bcp47Validator mocklangValidator = mock(Bcp47Validator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("type_lang");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.LANGUAGE_CODE);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsTypeValidator underTest = new GtfsTypeValidator(mockFileSpec,
                mock(FloatValidator.class),
                mock(IntegerValidator.class),
                mock(UrlValidator.class),
                mocklangValidator,
                mock(EmailValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("type_lang", "en_FR"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());
        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidLangNotice.class));
        assertEquals("ERROR", ((InvalidLangNotice) notice).getLevel());
        assertEquals(22, notice.getCode());
        assertEquals("Invalid language code", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid language code:`en_FR` in field:`type_lang` for entity with id:`test_id`",
                notice.getDescription());

        verify(mocklangValidator, times(1)).isValid(
                ArgumentMatchers.eq("en_FR"));
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((InvalidTimezoneNotice) notice).getLevel());
        assertEquals(13, notice.getCode());
        assertEquals("Invalid timezone", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid timezone:`abc` in field:`type_timezone` for entity with id:`test_id`",
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("WARNING", ((NonAsciiOrNonPrintableCharNotice) notice).getLevel());
        assertEquals(3, notice.getCode());
        assertEquals("Suspicious id", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Non ascii or non printable character(s) in:`abçé` in field:`type_id` for entity with id:`test_id`",
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("WARNING", ((NonAsciiOrNonPrintableCharNotice) notice).getLevel());
        assertEquals(3, notice.getCode());
        assertEquals("Suspicious id", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals(
                "Non ascii or non printable character(s) in:`ab\u0003` in field:`type_id` for entity with id:" +
                        "`test_id`",
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_unspecified` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_text` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_float` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_integer` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_color` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(5);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_timezone` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(6);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_id` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(7);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_url` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(8);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_time` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(9);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_date` marked as required in entity with id:`test_id`",
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
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
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_unspecified` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(1);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_text` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(2);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_float` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(3);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_integer` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(4);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_color` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(5);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_timezone` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(6);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_id` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(7);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_url` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(8);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_time` marked as required in entity with id:`test_id`",
                notice.getDescription());

        notice = new ArrayList<>(result).get(9);
        assertThat(notice, instanceOf(MissingRequiredValueNotice.class));
        assertEquals("ERROR", ((MissingRequiredValueNotice) notice).getLevel());
        assertEquals(15, notice.getCode());
        assertEquals("Missing required value", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Missing value for field:`type_date` marked as required in entity with id:`test_id`",
                notice.getDescription());
    }

    @Test
    void invalidCurrencyCodeGenerateNotice() {
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("currency_type", "JAN"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(1, result.size());

        Notice notice = new ArrayList<>(result).get(0);
        assertThat(notice, instanceOf(InvalidCurrencyCodeNotice.class));
        assertEquals("ERROR", ((InvalidCurrencyCodeNotice) notice).getLevel());
        assertEquals(18, notice.getCode());
        assertEquals("Invalid currency code", notice.getTitle());
        assertEquals(TEST_FILE_TST, notice.getFilename());
        assertEquals("Invalid currency code: `JAN` in field: `currency_type` for entity with id: `test_id`",
                notice.getDescription());
    }

    @Test
    void validCurrencyCodeDoNotGenerateNotice() {
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
                mock(Bcp47Validator.class),
                mock(EmailValidator.class),
                mock(RegexValidator.class),
                Collections.emptySet()
        );

        Collection<Notice> result = underTest.validate(new ParsedEntity(
                TEST_ID,
                Map.of("currency_type", "MXN"),
                new RawFileInfo.RawFileInfoBuilder().filename(TEST_FILE_TST).build()
        ));

        assertEquals(0, result.size());
    }
}