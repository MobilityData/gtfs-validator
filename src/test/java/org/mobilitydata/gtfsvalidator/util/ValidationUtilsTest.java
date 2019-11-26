package org.mobilitydata.gtfsvalidator.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilsTest {

    String fieldName = "filedNameTest";


    @Test
    void parseAndValidateFloat() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        Float returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "66.6",
                false,
                false,
                testList);

        assertEquals(66.6f, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                null,
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // NaN parsing
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "NaN",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // Any non parsable
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "abc",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = ValidationUtils.parseAndValidateFloat(fieldName,
                "-0.001",
                false,
                false,
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E004", error.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void parseAndValidateInteger() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        Integer returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "666",
                false,
                false,
                testList);

        assertEquals(666, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                null,
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "abc",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E005", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = ValidationUtils.parseAndValidateInteger(fieldName,
                "-1",
                false,
                false,
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E006", error.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void validateString() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = ValidationUtils.validateString(fieldName,
                "666sixcentsoixantesix",
                false,
                false,
                testList);

        assertEquals("666sixcentsoixantesix", returned);
        assertEquals(0, testList.size());

        // can be null
        returned = ValidationUtils.validateString(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = ValidationUtils.validateString(fieldName,
                null,
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = ValidationUtils.validateString(fieldName,
                "",
                true,
                false,
                testList);
        assertEquals("", returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = ValidationUtils.validateString(fieldName,
                "",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // onlyPrintableAscii is true, value contains non ASCII
        returned = ValidationUtils.validateString(fieldName,
                "abçé",
                false,
                true,
                testList);
        assertEquals("abçé", returned);
        assertEquals(1, testList.size());
        OccurrenceModel warning = testList.get(0);
        assertEquals(fieldName, warning.getPrefix());
        assertEquals("W001", warning.getRule().getErrorId());

        testList.clear();

        // onlyPrintableAscii is true, value contains non printable ASCII
        returned = ValidationUtils.validateString(fieldName,
                "ab\u0003",
                false,
                true,
                testList);
        assertEquals("ab\u0003", returned);
        assertEquals(1, testList.size());
        warning = testList.get(0);
        assertEquals(fieldName, warning.getPrefix());
        assertEquals("W001", warning.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void parseAndValidateColor() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#ABCDEF",
                testList);

        assertEquals("#ABCDEF", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#012345",
                testList);

        assertEquals("#012345", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#6789af",
                testList);

        assertEquals("#6789af", returned);
        assertEquals(0, testList.size());

        // null
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                null,
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // not starting with #
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "ABCDEF",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // incorrect length - too short
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#ABC",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // incorrect length - too long
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#ABCDEF0",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        //  invalid characters
        returned = ValidationUtils.parseAndValidateColor(fieldName,
                "#AZ-FTJ",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

    }
}