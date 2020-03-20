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

package org.mobilitydata.gtfsvalidator.parser;

import org.apache.commons.validator.routines.FloatValidator;
import org.apache.commons.validator.routines.IntegerValidator;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.domain.entity.ParsedEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawEntity;
import org.mobilitydata.gtfsvalidator.domain.entity.RawFileInfo;
import org.mobilitydata.gtfsvalidator.protos.GtfsSpecificationProto;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseFloatNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.CannotParseIntegerNotice;
import org.mobilitydata.gtfsvalidator.usecase.notice.base.ErrorNotice;
import org.mockito.ArgumentMatchers;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class GtfsEntityParserTest {

    @Test
    void validFloatValidationShouldGenerateNothing() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);
        when(mockFloatValidator.isValid(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("float_type", "1.0"),
                0
        ));

        assertEquals(0, result.size());

        verify(mockFloatValidator, times(1)).isValid(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void validIntegerValidationShouldGenerateNothing() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);
        when(mockIntegerValidator.isValid(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mockIntegerValidator
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("integer_type", "1"),
                0
        ));

        assertEquals(0, result.size());

        verify(mockIntegerValidator, times(1)).isValid(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void nullFloatValidationShouldGenerateNothing() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Collections.emptyMap(),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void emptyFloatValidationShouldGenerateNothing() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("float_type", ""),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void notANumberFloatValidationShouldGenerateError() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);
        when(mockFloatValidator.isValid(ArgumentMatchers.eq("NaN"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);
        when(mockFloatValidator.validate(ArgumentMatchers.eq("NaN"),
                (Locale) ArgumentMatchers.any())).thenReturn(Float.NaN);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        when(mockFileSpec.getFilename()).thenReturn("test_filename.tst");
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().filename("test_filename.tst").build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );

        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("float_type", "NaN"),
                0
        ));

        verify(mockFloatValidator, times(1)).isValid(ArgumentMatchers.eq("NaN"),
                (Locale) ArgumentMatchers.any());

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseFloatNotice.class));
        assertEquals("E006", notice.getId());
        assertEquals("Invalid float value", notice.getTitle());
        assertEquals("test_filename.tst", notice.getFilename());
        assertEquals("Value: 'NaN' of field: float_type with type float can't be parsed in file: " +
                "test_filename.tst at row: 0", notice.getDescription());
    }

    @Test
    void invalidFloatValidationShouldGenerateError() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        when(mockFileSpec.getFilename()).thenReturn("test_filename.tst");
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );


        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("float_type", "abc"),
                0
        ));

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseFloatNotice.class));
        assertEquals("E006", notice.getId());
        assertEquals("Invalid float value", notice.getTitle());
        assertEquals("test_filename.tst", notice.getFilename());
        assertEquals("Value: 'abc' of field: float_type with type float can't be parsed in file: " +
                "test_filename.tst at row: 0", notice.getDescription());

        verify(mockFloatValidator, times(1)).isValid(ArgumentMatchers.eq("abc"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void nullIntegerValidationShouldGenerateNothing() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Collections.emptyMap(),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void emptyIntegerValidationShouldGenerateNothing() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );

        Collection<ErrorNotice> result = underTest.validateNumericTypes(new RawEntity(
                Map.of("integer_type", ""),
                0
        ));

        assertEquals(0, result.size());
    }

    @Test
    void invalidIntegerValidationShouldGenerateError() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        when(mockFileSpec.getFilename()).thenReturn("test_filename.tst");
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mockIntegerValidator
        );


        ArrayList<ErrorNotice> result = (ArrayList<ErrorNotice>) underTest.validateNumericTypes(new RawEntity(
                Map.of("integer_type", "abc"),
                0
        ));

        assertEquals(1, result.size());

        ErrorNotice notice = result.get(0);
        assertThat(notice, instanceOf(CannotParseIntegerNotice.class));
        assertEquals("E005", notice.getId());
        assertEquals("Invalid integer value", notice.getTitle());
        assertEquals("test_filename.tst", notice.getFilename());
        assertEquals("Value: 'abc' of field: integer_type with type integer can't be parsed in file: " +
                "test_filename.tst at row: 0", notice.getDescription());

        verify(mockIntegerValidator, times(1)).isValid(ArgumentMatchers.eq("abc"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void validFloatShouldParse() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);
        when(mockFloatValidator.isValid(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);
        when(mockFloatValidator.validate(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any())).thenReturn(1.0f);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );

        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("float_type", "1.0"),
                0
        ));

        assertEquals(1.0f, result.get("float_type"));

        verify(mockFloatValidator, times(1)).isValid(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any());
        // 1 for NaN check, 1 for actual conversion
        verify(mockFloatValidator, times(2)).validate(ArgumentMatchers.eq("1.0"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void emptyFloatShouldParseToNull() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );

        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("float_type", ""),
                0
        ));

        assertNull(result.get("float_type"));
    }

    @Test
    void notANumberFloatShouldParseToNull() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);
        when(mockFloatValidator.isValid(ArgumentMatchers.eq("NaN"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);
        when(mockFloatValidator.validate(ArgumentMatchers.eq("NaN"),
                (Locale) ArgumentMatchers.any())).thenReturn(Float.NaN);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().filename("test_filename.tst").build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );

        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("float_type", "NaN"),
                0
        ));

        assertNull(result.get("float_type"));
    }

    @Test
    void invalidFloatShouldParseToNull() {

        FloatValidator mockFloatValidator = mock(FloatValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("float_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.FLOAT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mockFloatValidator,
                mock(IntegerValidator.class)
        );

        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("float_type", "abc"),
                0
        ));

        assertNull(result.get("float_type"));
    }

    @Test
    void validIntegerShouldParse() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);
        when(mockIntegerValidator.isValid(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any())).thenReturn(true);
        when(mockIntegerValidator.validate(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any())).thenReturn(1);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mockIntegerValidator
        );

        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("integer_type", "1"),
                0
        ));

        assertEquals(1, result.get("integer_type"));

        verify(mockIntegerValidator, times(1)).isValid(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any());

        verify(mockIntegerValidator, times(1)).validate(ArgumentMatchers.eq("1"),
                (Locale) ArgumentMatchers.any());
    }

    @Test
    void emptyIntegerShouldParseToNull() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("integer_type", ""),
                0
        ));

        assertNull(result.get("integer_test"));
    }

    @Test
    void invalidIntegerShouldParseToNull() {

        IntegerValidator mockIntegerValidator = mock(IntegerValidator.class);

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        when(mockFileSpec.getFilename()).thenReturn("test_filename.tst");
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("integer_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.INTEGER);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mockIntegerValidator
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("integer_type", "abc"),
                0
        ));

        assertNull(result.get("integer_type"));
    }

    @Test
    void stringAsIs() {

        GtfsSpecificationProto.CsvSpecProto mockFileSpec = mock(GtfsSpecificationProto.CsvSpecProto.class);
        when(mockFileSpec.getFilename()).thenReturn("test_filename.tst");
        GtfsSpecificationProto.ColumnSpecProto mockColumnSpec = mock(GtfsSpecificationProto.ColumnSpecProto.class);
        when(mockColumnSpec.getName()).thenReturn("string_type");

        GtfsSpecificationProto.ColumnInputType mockInputType = mock(GtfsSpecificationProto.ColumnInputType.class);
        when(mockInputType.getType()).thenReturn(GtfsSpecificationProto.ColumnInputType.InputType.TEXT);

        when(mockColumnSpec.getType()).thenReturn(mockInputType);
        when(mockFileSpec.getColumnList()).thenReturn(List.of(mockColumnSpec));

        GtfsEntityParser underTest = new GtfsEntityParser(
                mockFileSpec,
                new RawFileInfo.RawFileInfoBuilder().build(),
                mock(FloatValidator.class),
                mock(IntegerValidator.class)
        );


        ParsedEntity result = underTest.parse(new RawEntity(
                Map.of("string_type", "a_string"),
                0
        ));

        assertEquals("a_string", result.get("string_type"));
    }

    //TODO: add test for parsedEntity id
}