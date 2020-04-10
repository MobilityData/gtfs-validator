package org.mobilitydata.gtfsvalidator.domain.entity.gtfs.translations;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class TableNameTest {

    @Test
    public void createTableNameWithNullValueShouldThrowException() {
        String unexpectedEnumValue = null;

        @SuppressWarnings("ConstantConditions") Object underTest = TableName.fromString(unexpectedEnumValue);

        assertNull(underTest);
    }

    @Test
    public void createTableNameWithUnexpectedValueShouldReturnNullValue() {
        String unexpectedEnumValue = "test";

        Object underTest = TableName.fromString(unexpectedEnumValue);

        assertNull(underTest);
    }

    @Test
    public void createTableNameWithExpectedValueAgencyShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "agency";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.AGENCY);
    }

    @Test
    public void createTableNameWithExpectedValueStopsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "stops";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.STOPS);
    }

    @Test
    public void createTableNameWithExpectedValueRoutesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "routes";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.ROUTES);
    }

    @Test
    public void createTableNameWithExpectedValueTripsShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "trips";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.TRIPS);
    }

    @Test
    public void createTableNameWithExpectedValueStopTimesShouldNotThrowExceptionAndReturnCorrectRouteType() {
        String expectedEnumValue = "stop_times";

        TableName tableName = TableName.fromString(expectedEnumValue);

        assertEquals(tableName, TableName.STOP_TIMES);
    }
}