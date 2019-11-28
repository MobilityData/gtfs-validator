package org.mobilitydata.gtfsvalidator.util;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GTFSTypeValidationUtilsTest {

    String fieldName = "filedNameTest";


    @Test
    void parseAndValidateFloat() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        Float returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
                "66.6",
                false,
                false,
                testList);

        assertEquals(66.6f, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(fieldName,
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
        Integer returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
                "666",
                false,
                false,
                testList);

        assertEquals(666, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
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
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(fieldName,
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
        String returned = GTFSTypeValidationUtils.validateString(fieldName,
                "666sixcentsoixantesix",
                false,
                false,
                testList);

        assertEquals("666sixcentsoixantesix", returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.validateString(fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.validateString(fieldName,
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
        returned = GTFSTypeValidationUtils.validateString(fieldName,
                "",
                true,
                false,
                testList);
        assertEquals("", returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.validateString(fieldName,
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
        returned = GTFSTypeValidationUtils.validateString(fieldName,
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
        returned = GTFSTypeValidationUtils.validateString(fieldName,
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
        String returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#ABCDEF",
                testList);

        assertEquals("#ABCDEF", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#012345",
                testList);

        assertEquals("#012345", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#6789af",
                testList);

        assertEquals("#6789af", returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                null,
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // not starting with #
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "ABCDEF",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // incorrect length - too short
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#ABC",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // incorrect length - too long
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#ABCDEF0",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        //  invalid characters
        returned = GTFSTypeValidationUtils.parseAndValidateColor(fieldName,
                "#AZ-FTJ",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(fieldName, error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

    }
}