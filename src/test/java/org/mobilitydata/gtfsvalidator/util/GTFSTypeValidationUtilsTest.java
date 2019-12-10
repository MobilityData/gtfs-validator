package org.mobilitydata.gtfsvalidator.util;

import org.junit.jupiter.api.Test;
import org.mobilitydata.gtfsvalidator.model.OccurrenceModel;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GTFSTypeValidationUtilsTest {

    String fieldName = "fieldNameTest";
    String validatedEntityId = "entity_id: testId";

    @Test
    void parseFloat() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        float returned = GTFSTypeValidationUtils.parseFloat(validatedEntityId,
                fieldName,
                "66.6",
                testList);

        assertEquals(66.6f, returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.parseFloat(validatedEntityId,
                fieldName,
                null,
                testList);
        assertTrue(Float.isNaN(returned));
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.parseFloat(validatedEntityId,
                fieldName,
                "",
                testList);
        assertTrue(Float.isNaN(returned));
        assertEquals(0, testList.size());

        // NaN parsing
        returned = GTFSTypeValidationUtils.parseFloat(validatedEntityId,
                fieldName,
                "NaN",
                testList);
        assertTrue(Float.isNaN(returned));
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is NaN" , error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = GTFSTypeValidationUtils.parseFloat(validatedEntityId,
                fieldName,
                "abc",
                testList);
        assertTrue(Float.isNaN(returned));
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc" , error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());
    }

    @Test
    void validateFloat() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        GTFSTypeValidationUtils.validateFloat(validatedEntityId,
                fieldName,
                66.6f,
                false,
                false,
                testList);

        assertEquals(0, testList.size());

        // can be NaN
        GTFSTypeValidationUtils.validateFloat(validatedEntityId,
                fieldName,
                Float.NaN,
                true,
                false,
                testList);
        assertEquals(0, testList.size());

        // cannot be NaN
        GTFSTypeValidationUtils.validateFloat(validatedEntityId,
                fieldName,
                Float.NaN,
                false,
                false,
                testList);

        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        GTFSTypeValidationUtils.validateFloat(validatedEntityId,
                fieldName,
                0f,
                false,
                false,
                testList);

        assertEquals(0, testList.size());

        // can't be negative, value is negative
        GTFSTypeValidationUtils.validateFloat(validatedEntityId,
                fieldName,
                -0.001f,
                false,
                false,
                testList);

        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -0.001", error.getPrefix());
        assertEquals("E004", error.getRule().getErrorId());
    }

    @Test
    void parseInteger() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        int returned = GTFSTypeValidationUtils.parseInteger(validatedEntityId,
                fieldName,
                "666",
                testList);

        assertEquals(666, returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.parseInteger(validatedEntityId,
                fieldName,
                null,
                testList);
        assertEquals(returned, Integer.MAX_VALUE);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.parseInteger(validatedEntityId,
                fieldName,
                "",
                testList);
        assertEquals(returned, Integer.MAX_VALUE);
        assertEquals(0, testList.size());

        // any non parsable
        returned = GTFSTypeValidationUtils.parseInteger(validatedEntityId,
                fieldName,
                "abc",
                testList);
        assertEquals(returned, Integer.MAX_VALUE);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc" , error.getPrefix());
        assertEquals("E005", error.getRule().getErrorId());
    }

    @Test
    void validateInteger() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        GTFSTypeValidationUtils.validateInteger(validatedEntityId,
                fieldName,
                666,
                false,
                false,
                testList);

        assertEquals(0, testList.size());

        // can be max
        GTFSTypeValidationUtils.validateInteger(validatedEntityId,
                fieldName,
                Integer.MAX_VALUE,
                true,
                false,
                testList);
        assertEquals(0, testList.size());

        // cannot be max
        GTFSTypeValidationUtils.validateInteger(validatedEntityId,
                fieldName,
                Integer.MAX_VALUE,
                false,
                false,
                testList);

        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        GTFSTypeValidationUtils.validateInteger(validatedEntityId,
                fieldName,
                0,
                false,
                false,
                testList);

        assertEquals(0, testList.size());

        // can't be negative, value is negative
        GTFSTypeValidationUtils.validateInteger(validatedEntityId,
                fieldName,
                -1,
                false,
                false,
                testList);

        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -1", error.getPrefix());
        assertEquals("E006", error.getRule().getErrorId());
    }

    @Test
    void validateLatitude() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case - positive
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                90.f,
                false,
                testList);

        assertEquals(0, testList.size());

        // typical case - negative
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                -90.f,
                false,
                testList);

        assertEquals(0, testList.size());

        // can be NaN
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                Float.NaN,
                true,
                testList);
        assertEquals(0, testList.size());

        // cannot be NaN
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                Float.NaN,
                false,
                testList);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too high
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                90.12345f,
                false,
                testList);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 90.12345", error.getPrefix());
        assertEquals("E008", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too low
        GTFSTypeValidationUtils.validateLatitude(validatedEntityId,
                fieldName,
                -90.12345f,
                false,
                testList);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -90.12345", error.getPrefix());
        assertEquals("E008", error.getRule().getErrorId());
    }

    @Test
    void validateLongitude() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case - positive
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                180.f,
                false,
                testList);

        assertEquals(0, testList.size());

        // typical case - negative
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                -180.f,
                false,
                testList);

        assertEquals(0, testList.size());

        // can be NaN
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                Float.NaN,
                true,
                testList);
        assertEquals(0, testList.size());

        // cannot be NaN
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                Float.NaN,
                false,
                testList);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too high
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                180.1234f,
                false,
                testList);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 180.1234", error.getPrefix());
        assertEquals("E009", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too low
        GTFSTypeValidationUtils.validateLongitude(validatedEntityId,
                fieldName,
                -180.1234f,
                false,
                testList);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -180.1234", error.getPrefix());
        assertEquals("E009", error.getRule().getErrorId());
    }

    @Test
    void validateId() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.validateId(fieldName,
                "666sixcentsoixantesix",
                false,
                testList);

        assertEquals("666sixcentsoixantesix", returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                null,
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                null,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals(" fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                "",
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                "",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals(" fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // contains non ASCII
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                "abçé",
                false,
                testList);
        assertEquals("abçé", returned);
        assertEquals(1, testList.size());
        OccurrenceModel warning = testList.get(0);
        assertEquals("abçé fieldNameTest is abçé", warning.getPrefix());
        assertEquals("W001", warning.getRule().getErrorId());

        testList.clear();

        // contains non printable ASCII
        returned = GTFSTypeValidationUtils.validateId(fieldName,
                "ab\u0003",
                false,
                testList);
        assertEquals("ab\u0003", returned);
        assertEquals(1, testList.size());
        warning = testList.get(0);
        assertEquals("ab\u0003 fieldNameTest is ab\u0003", warning.getPrefix());
        assertEquals("W001", warning.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void validateText() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.validateText(validatedEntityId,
                fieldName,
                "666sixcentsoixantesixçàé",
                false,
                testList);

        assertEquals("666sixcentsoixantesixçàé", returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.validateText(validatedEntityId,
                fieldName,
                null,
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.validateText(validatedEntityId,
                fieldName,
                null,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = GTFSTypeValidationUtils.validateText(validatedEntityId,
                fieldName,
                "",
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.validateText(validatedEntityId,
                fieldName,
                "",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void validateUrl() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                "https://mobilitydata.org",
                false,
                testList);

        assertEquals("https://mobilitydata.org", returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                null,
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                null,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                "",
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                "",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // invalid scheme
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                "ftp://mobilitydata.org",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is ftp://mobilitydata.org", error.getPrefix());
        assertEquals("E011", error.getRule().getErrorId());

        testList.clear();

        // any malformed
        returned = GTFSTypeValidationUtils.validateUrl(validatedEntityId,
                fieldName,
                "http://mobilitydataorg",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is http://mobilitydataorg", error.getPrefix());
        assertEquals("E011", error.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void validateColor() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "ABCDEF",
                testList);

        assertEquals("ABCDEF", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "012345",
                testList);

        assertEquals("012345", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "6789af",
                testList);

        assertEquals("6789af", returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                null,
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // incorrect length - too short
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "ABC",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is ABC", error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // incorrect length - too long
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "ABCDEF0",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is ABCDEF0", error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());

        testList.clear();

        // invalid characters
        returned = GTFSTypeValidationUtils.validateColor(validatedEntityId,
                fieldName,
                "AZ-FTJ",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is AZ-FTJ", error.getPrefix());
        assertEquals("E007", error.getRule().getErrorId());
    }

    @Test
    public void validateTime() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case HH:MM:SS
        String returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "26:59:59",
                testList);

        assertEquals("26:59:59", returned);
        assertEquals(0, testList.size());

        // typical case H:MM:SS
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "9:59:59",
                testList);

        assertEquals("9:59:59", returned);
        assertEquals(0, testList.size());

        // suspicious HH:MM:SS
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "99:59:59",
                testList);

        assertEquals("99:59:59", returned);
        assertEquals(1, testList.size());
        OccurrenceModel warning = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 99:59:59", warning.getPrefix());
        assertEquals("W002", warning.getRule().getErrorId());

        testList.clear();

        // invalid HHH:MM:SS
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "999:59:59",
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 999:59:59", error.getPrefix());
        assertEquals("E012", error.getRule().getErrorId());

        testList.clear();

        // null
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                null,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // any non parsable
        returned = GTFSTypeValidationUtils.validateTime(validatedEntityId,
                fieldName,
                "abc",
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc", error.getPrefix());
        assertEquals("E012", error.getRule().getErrorId());
    }

    @Test
    public void validateTimeZone() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.validateTimeZone(validatedEntityId,
                fieldName,
                "America/Montreal",
                testList);

        assertEquals("America/Montreal", returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.validateTimeZone(validatedEntityId,
                fieldName,
                null,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.validateTimeZone(validatedEntityId,
                fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // any non parsable
        returned = GTFSTypeValidationUtils.validateTimeZone(validatedEntityId,
                fieldName,
                "abc",
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc", error.getPrefix());
        assertEquals("E010", error.getRule().getErrorId());
    }
}