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
    void parseAndValidateFloat() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        Float returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "66.6",
                false,
                false,
                testList);

        assertEquals(66.6f, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                null,
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // NaN parsing
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "NaN",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is NaN", error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "abc",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc" , error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = GTFSTypeValidationUtils.parseAndValidateFloat(validatedEntityId,
                fieldName,
                "-0.001",
                false,
                false,
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -0.001", error.getPrefix());
        assertEquals("E004", error.getRule().getErrorId());

        testList.clear();
    }

    @Test
    void parseAndValidateLatitude() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case - positive
        Float returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "90",
                false,
                testList);

        assertEquals(90.0f, returned);
        assertEquals(0, testList.size());

        // typical case - negative
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "-90",
                false,
                testList);

        assertEquals(-90.0f, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                null,
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
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
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "",
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
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

        // NaN parsing
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "NaN",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is NaN", error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "abc",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc", error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too high
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "90.1234567",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 90.1234567", error.getPrefix());
        assertEquals("E008", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too low
        returned = GTFSTypeValidationUtils.parseAndValidateLatitude(validatedEntityId,
                fieldName,
                "-90.1234567",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -90.1234567", error.getPrefix());
        assertEquals("E008", error.getRule().getErrorId());
    }

    @Test
    void parseAndValidateLongitude() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case - positive
        Float returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "180",
                false,
                testList);

        assertEquals(180.0f, returned);
        assertEquals(0, testList.size());

        // typical case - negative
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "-180",
                false,
                testList);

        assertEquals(-180.0f, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                null,
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
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
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "",
                true,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
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

        // NaN parsing
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "NaN",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is NaN", error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "abc",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc", error.getPrefix());
        assertEquals("E003", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too high
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "180.1234567",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is 180.1234567", error.getPrefix());
        assertEquals("E009", error.getRule().getErrorId());

        testList.clear();

        // invalid value - too low
        returned = GTFSTypeValidationUtils.parseAndValidateLongitude(validatedEntityId,
                fieldName,
                "-180.1234567",
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -180.1234567", error.getPrefix());
        assertEquals("E009", error.getRule().getErrorId());
    }

    @Test
    void parseAndValidateInteger() {
        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        Integer returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "666",
                false,
                false,
                testList);

        assertEquals(666, returned);
        assertEquals(0, testList.size());

        // can be null
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                null,
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be null
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                null,
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        OccurrenceModel error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // can be empty
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "",
                true,
                false,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // cannot be empty
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is null or empty", error.getPrefix());
        assertEquals("E002", error.getRule().getErrorId());

        testList.clear();

        // any non parsable
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "abc",
                false,
                false,
                testList);
        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is abc", error.getPrefix());
        assertEquals("E005", error.getRule().getErrorId());

        testList.clear();

        // can't be negative, value is zero
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "0",
                false,
                false,
                testList);

        assertEquals(0, returned);
        assertEquals(0, testList.size());

        // can't be negative, value is negative
        returned = GTFSTypeValidationUtils.parseAndValidateInteger(validatedEntityId,
                fieldName,
                "-1",
                false,
                false,
                testList);

        assertNull(returned);
        assertEquals(1, testList.size());
        error = testList.get(0);
        assertEquals("entity_id: testId fieldNameTest is -1", error.getPrefix());
        assertEquals("E006", error.getRule().getErrorId());

        testList.clear();
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
    void parseAndValidateColor() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
                fieldName,
                "ABCDEF",
                testList);

        assertEquals("ABCDEF", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
                fieldName,
                "012345",
                testList);

        assertEquals("012345", returned);
        assertEquals(0, testList.size());

        // typical case
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
                fieldName,
                "6789af",
                testList);

        assertEquals("6789af", returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
                fieldName,
                null,
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
                fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // incorrect length - too short
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
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
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
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
        returned = GTFSTypeValidationUtils.parseAndValidateColor(validatedEntityId,
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
    public void parseAndValidateTimeZone() {

        List<OccurrenceModel> testList = new ArrayList<>();

        // typical case
        String returned = GTFSTypeValidationUtils.parseAndValidateTimeZone(validatedEntityId,
                fieldName,
                "America/Montreal",
                testList);

        assertEquals("America/Montreal", returned);
        assertEquals(0, testList.size());

        // null
        returned = GTFSTypeValidationUtils.parseAndValidateTimeZone(validatedEntityId,
                fieldName,
                null,
                testList);
        assertNull(returned);
        assertEquals(0, testList.size());

        // empty
        returned = GTFSTypeValidationUtils.parseAndValidateTimeZone(validatedEntityId,
                fieldName,
                "",
                testList);

        assertNull(returned);
        assertEquals(0, testList.size());

        // any non parsable
        returned = GTFSTypeValidationUtils.parseAndValidateTimeZone(validatedEntityId,
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